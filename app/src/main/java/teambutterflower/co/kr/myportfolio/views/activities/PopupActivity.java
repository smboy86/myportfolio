package teambutterflower.co.kr.myportfolio.views.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.regex.Pattern;

import cn.trinea.android.common.entity.HttpResponse;
import cn.trinea.android.common.service.HttpCache;
import teambutterflower.co.kr.myportfolio.R;
import teambutterflower.co.kr.myportfolio.utils.JSONWeatherParser;
import teambutterflower.co.kr.myportfolio.utils.TimerHandler;
import teambutterflower.co.kr.myportfolio.utils.VisualizerView;
import teambutterflower.co.kr.myportfolio.utils.WeatherHttpClient;
import teambutterflower.co.kr.myportfolio.utils.model.Weather;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;


/**
 * Created by SeongMin on 2015-02-24.
 */
public class PopupActivity extends Activity implements View.OnClickListener {
    // grid_game
    int cnt = 0;
    double start_time = 0;
    double end_time;
    double best_time = 9999;
    String best_record = "0";

    int level = 9;
    int row = 3;
    int col = 3;

    SoundPool soundPool;
    int[] soundId = new int[2];

    private AlertDialog mDialog = null;

    //TimerHandler timer = new TimerHandler();

    // Weather App
    private TextView cityText;
    private TextView condDescr;
    private TextView temp;
    private TextView press;
    private TextView windSpeed;
    private TextView windDeg;

    private TextView hum;
    private ImageView imgView;

    // HttpCache
    private HttpCache httpCache;

    private Context context;


    // Item - Music Visualizer
    private MediaPlayer mMediaPlayer;
    private Visualizer mVisualizer;
    private LinearLayout mLinearLayout;
    private VisualizerView mVisualizerView;

    //int level = 100;
    //int scale = 50;
    //int level;
    //int scale;


    // Item - Battery Ststus
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(Intent.ACTION_BATTERY_CHANGED)){
                //health, icon-small, level, plugged, present, scale, status, technology, temperature, voltage

                // 1. 잔량
                int level = intent.getIntExtra("level", 0);
                int scale = intent.getIntExtra("scale", 0);

                TextView textViewRemain = (TextView) findViewById(R.id.battery_remain_edit);
                textViewRemain.setText(((float)level  / (float)scale * 100) + " (%)");

                // 2. 상태
                TextView textViewHealth = (TextView) findViewById(R.id.battery_status_edit);
                int health = intent.getIntExtra("health", 0);

                switch (health){
                    case 1:
                        textViewHealth.setText("UNKNOWN");
                        break;
                    case 2:
                        textViewHealth.setText("GOOD");
                        break;
                    case 3:
                        textViewHealth.setText("OVERHEAT");
                        break;
                    case 4:
                        textViewHealth.setText("DEAD");
                        break;
                    case 5:
                        textViewHealth.setText("OVER_VOLTAGE");
                        break;
                    case 6:
                        textViewHealth.setText("UNSPECIFIED_FAILUER"); // 오타 같은데 모르겠다 -_-
                        break;
                }

                // 3. 충전 여부
                TextView textViewCharge = (TextView) findViewById(R.id.battery_charge_edit);
                int status = intent.getIntExtra("status", 0);

                switch (status){
                    case BatteryManager.BATTERY_STATUS_UNKNOWN:
                        textViewCharge.setText("UNKNOWN");
                        break;
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        textViewCharge.setText("CHARGING");
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        textViewCharge.setText("DISCHARGING");
                        break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        textViewCharge.setText("NOT_CHARGING");
                        break;
                    case BatteryManager.BATTERY_STATUS_FULL:
                        textViewCharge.setText("FULL");
                        break;
                }

                // 4. 충전 방법
                TextView textViewChargePlugged = (TextView) findViewById(R.id.battery_charge_plugged_edit);
                int plugged = intent.getIntExtra("plugged", 0);

                Log.d("Battery", String.valueOf(plugged));

                switch (plugged){
                    case BatteryManager.BATTERY_PLUGGED_AC:
                        textViewChargePlugged.setText("Connected to AC Adaptor");
                        break;
                    case BatteryManager.BATTERY_PLUGGED_USB:
                        textViewChargePlugged.setText("Connected to USB");
                        break;
                    case BatteryManager.BATTERY_PLUGGED_WIRELESS: // 4번인데 좀 이상한?
                        textViewChargePlugged.setText("Use Battery");
                        break;
                    default:
                        textViewChargePlugged.setText("Use Battery");
                        break;
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();

        try{ // 뭔가 얍삽한 방법 같다
            if(mBroadcastReceiver != null){
                unregisterReceiver(mBroadcastReceiver);
            }
        }catch(IllegalArgumentException e){
            Log.w("Broadcast Battery", e.toString());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123456 && resultCode == RESULT_OK) {
            /*

            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
                //	RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE, uri);
                strRingTone = uri.toString();
                // ringtone 이름을 표시
                Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
//    	        ((TextView)findViewById(R.id.alarm_set_l2_value)).setText(ringtone.getTitle(this));
                String value = "벨소리" + "\n" +  ringtone.getTitle(this);
                SpannableStringBuilder ssb = new SpannableStringBuilder();
                ssb.append(value);
                ssb.setSpan(new ForegroundColorSpan(0xFFf4A460), 3, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ((TextView)findViewById(R.id.ringtone)).setText(ssb);
            }
            */
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            String strRingTone = uri.toString();
            Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
            Toast.makeText(getApplicationContext(), "벨소리 : " + "\n" + ringtone.getTitle(this), LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        switch (intent.getStringExtra("viewName")){
            case "Resume":
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                setContentView(R.layout.pop_resume);

                break;

            case "LinearLayout":
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                setContentView(R.layout.pop_linearlayout);

                Button button01 = (Button) findViewById(R.id.button01);

                button01.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        makeText(PopupActivity.this, "Button01 Click", LENGTH_SHORT).show();
                    }
                });

                Button button02 = (Button) findViewById(R.id.button02);

                button02.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        makeText(PopupActivity.this, "Button02 Click", LENGTH_SHORT).show();
                    }
                });

                Button button03 = (Button) findViewById(R.id.button03);

                button03.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        makeText(PopupActivity.this, "Button03 Click", LENGTH_SHORT).show();
                    }
                });

                break;

            case "RelativeLayout":
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                setContentView(R.layout.pop_relativelayout);

                EditText userName = (EditText) findViewById(R.id.usernameEntry);
                userName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                        if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE){
                            makeText(PopupActivity.this, "EnterKey input", LENGTH_SHORT).show();
                        }
                        return false;
                    }
                });

                EditText password = (EditText) findViewById(R.id.passwordEntry);
                password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                        if (actionId == EditorInfo.IME_ACTION_DONE){
                            makeText(PopupActivity.this, "DoneKey input", LENGTH_SHORT).show();
                        }
                        return false;
                    }
                });

                Button loginBtn = (Button) findViewById(R.id.loginBtn);

                loginBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        makeText(PopupActivity.this, "로그인 합니다.", LENGTH_SHORT).show();
                    }
                });

                Button exitBtn = (Button) findViewById(R.id.exitBtn);

                exitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

                break;


            case "WebView":
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                setContentView(R.layout.pop_webview);

                WebView webView = (WebView) findViewById(R.id.webPopup);
                webView.setWebViewClient(new myWebViewClient());
                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webView.loadUrl("http://google.com");

                break;

            case "Music Visualizer":
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                setContentView(R.layout.pop_service);

                Button startbtn = (Button) findViewById(R.id.service_start_btn);
                startbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*
                        Intent serivceIntent = new Intent(v.getContext(), MyService.class);
                        startService(serivceIntent);
                        */

                        TextView textView = (TextView) findViewById(R.id.service_start_result);
                        textView.setText("Music Start");

                        mLinearLayout = (LinearLayout) findViewById(R.id.vizualizer);

                        musicStart();
                    }
                });

                Button stopbtn = (Button) findViewById(R.id.service_stop_btn);
                stopbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*
                        Intent serivceIntent = new Intent(v.getContext(), MyService.class);
                        stopService(serivceIntent);
                        */
                        TextView textView = (TextView) findViewById(R.id.service_start_result);
                        textView.setText("Music Stop");

                        musicStop();
                    }
                });

                break;

            case "Ringtone":
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                Intent intentRingtone = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intentRingtone.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);
                startActivityForResult(intentRingtone, 123456);

                break;

            case "Battery Status":
                //requestWindowFeature(Window.FEATURE_NO_TITLE);
                setContentView(R.layout.pop_battery);

                // 뭔가 되게 심플한 리시버?
                IntentFilter filter = new IntentFilter();
                filter.addAction(Intent.ACTION_BATTERY_CHANGED);
                registerReceiver(mBroadcastReceiver, filter);

                break;
            case "HttpCache":
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                setContentView(R.layout.pop_httpcache);

                context = getApplicationContext();

                Button cacheBtn = (Button) findViewById(R.id.httpcache_btn);

                cacheBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HttpCache httpCache = new HttpCache(context);

                        EditText editText = (EditText) findViewById(R.id.httpcache_edit);
                        String url = String.valueOf(editText.getText());

                        httpCache.httpGet(url, new HttpCache.HttpCacheListener() {
                            @Override
                            protected void onPreGet() {
                                super.onPreGet();

                                TextView textView = (TextView) findViewById(R.id.httpcache_view);
                                textView.setText("loading...");

                            }

                            @Override
                            protected void onPostGet(HttpResponse httpResponse, boolean isInCache) {
                                super.onPostGet(httpResponse, isInCache);

                                if (httpResponse != null) {
                                    // get data success
                                    TextView textView = (TextView) findViewById(R.id.httpcache_view);
                                    textView.setText(httpResponse.getResponseBody());
                                } else{
                                    TextView textView = (TextView) findViewById(R.id.httpcache_view);
                                    textView.setText("httpcache fail");
                                }
                            }
                        });

                    }
                });
                break;
   /*
            case "REST API":

                requestWindowFeature(Window.FEATURE_NO_TITLE);
                setContentView(R.layout.pop_restapi);

                final Spinner spinner = (Spinner) findViewById(R.id.rest_spinner);
                spinner.setPrompt("선택");
                final ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(this, R.array.rest_spinner, android.R.layout.simple_spinner_item);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinner.setAdapter(arrayAdapter);


                GetApiTask api = new GetApiTask();
                api.execute("seoul");

                //Log.d("spinner", (String) arrayAdapter.getItem(spinner.getSelectedItemPosition()));

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        GetApiTask api = new GetApiTask();
                        api.execute((String) arrayAdapter.getItem(position));
                        Log.d("getItem", (String) arrayAdapter.getItem(position));

                        Toast.makeText(getApplicationContext(), "Asdfasdf", LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


                break;
 */
            case "Weather API":
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                setContentView(R.layout.pop_weather);

                final Spinner spinner = (Spinner) findViewById(R.id.rest_spinner);
                spinner.setPrompt("Seoul");
                final ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(this, R.array.rest_spinner, android.R.layout.simple_spinner_item);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinner.setAdapter(arrayAdapter);

                String city = "Seoul,KR"; // "seoul"; // "Rome,IT";


                cityText = (TextView) findViewById(R.id.cityText);
                condDescr = (TextView) findViewById(R.id.condDescr);
                temp = (TextView) findViewById(R.id.temp);
                hum = (TextView) findViewById(R.id.hum);
                press = (TextView) findViewById(R.id.press);
                windSpeed = (TextView) findViewById(R.id.windSpeed);
                windDeg = (TextView) findViewById(R.id.windDeg);
                imgView = (ImageView) findViewById(R.id.condIcon);

                JSONWeatherTask task = new JSONWeatherTask();
                task.execute(new String[]{city});


                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String city = (String) arrayAdapter.getItem(position);
                        JSONWeatherTask api = new JSONWeatherTask();
                        api.execute(new String[]{city});

                        Log.d("getItem", (String) arrayAdapter.getItem(position));

                        Toast.makeText(getApplicationContext(), city + "의 날씨입니다.", LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                break;

            case "System Widget":
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                setContentView(R.layout.pop_widget);

                // Radio button
                RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_group);
                radioGroup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("radio", "click");
                        Toast.makeText(getApplicationContext(), String.valueOf(v.getId()) + " : 선택 되었습니다.", LENGTH_SHORT).show();

                    }
                });

                // Check button
                // Spinner
                Spinner customSpinner = (Spinner) findViewById(R.id.custom_spinner);
                CustomSpinnerAdapter adapter= new CustomSpinnerAdapter(this, android.R.layout.simple_spinner_item, populateReindeer());
                customSpinner.setAdapter(adapter);


                // Toggle
                final ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggle_old);
                toggleButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(toggleButton.isChecked()){
                            Toast.makeText(getApplicationContext(), toggleButton.getTextOn(), LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getApplicationContext(), toggleButton.getTextOff(), LENGTH_SHORT).show();
                        }
                    }
                });
                break;

            case "1 to 9":
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                setContentView(R.layout.pop_grid_game);

                initValue();

                Button resetBtn = (Button) findViewById(R.id.resetBtn);
                resetBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resetGame();
                    }
                });

                Button gradeBtnGrid = (Button) findViewById(R.id.gradeBtnGrid);
                gradeBtnGrid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog = createDialog();
                        mDialog.show();
                    }
                });
                Button soundBtn = (Button) findViewById(R.id.sound);
                soundBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        soundPool.play(soundId[0], 0.2f, 0.2f, 0, 0, 1f);
                    }
                });

                TextView timerTextview = (TextView) findViewById(R.id.timer);
                new TimerHandler(timerTextview).start2();
                //new TimerHandler(timer, "ss.SS", TimerHandler.UP_A_DAY).start();

                resetGame();
                break;

            case "SMS send":
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                setContentView(R.layout.pop_sms);

                final EditText telNo = (EditText) findViewById(R.id.telNo);

                // 영문만 허용
                telNo.setFilters(new InputFilter[]{filterNum});



                final EditText smsContent = (EditText) findViewById(R.id.smsContent);

                Button sendMessage = (Button) findViewById(R.id.sendMessage);

                sendMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //makeText(PopupActivity.this, "메세지 발송", LENGTH_SHORT).show();
                        //sms.sendTextMessage(상대문자, null, 보낼내용, sentPi, d);
                        String sTelNo = telNo.getText().toString();
                        String sContent = smsContent.getText().toString();

                        if(sTelNo == null || sTelNo.equals("")){
                            makeText(PopupActivity.this, "전화번호를 입력해주세요", LENGTH_SHORT).show();
                            return;
                        }else if(sContent == null || sContent.equals("")){
                            makeText(PopupActivity.this, "보낼 내용이 없습니다.", LENGTH_SHORT).show();
                            return;
                        }

                        SmsManager smsManager = android.telephony.SmsManager.getDefault();
                        try{
                            smsManager.sendTextMessage(sTelNo, null, sContent, null, null);
                        }catch(Exception e){
                            e.printStackTrace();
                        }

                        // 문자 내역 남기기 (SKT는 안남을 때가 있다고 한다.)
                        ContentValues values = new ContentValues();
                        values.put("address", sTelNo);
                        values.put("body", sContent);
                        getContentResolver().insert(Uri.parse("content://sms/sent"), values);

                        // 보내고 대화창으로 이동
                        Uri uri = Uri.parse("smsto: " + telNo.getText());
                        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                        startActivity(intent);

                    }
                });


                break;


            default:
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                setContentView(R.layout.pop_webview);

                webView = (WebView) findViewById(R.id.webPopup);
                webView.setWebViewClient(new myWebViewClient());
                webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webView.loadUrl("http://butterflower.dothome.co.kr");
                break;
        }

    }

    // 15.5.12 글자 입력제한
    protected InputFilter filterNum = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[0-9]+$"); // [a-zA-Z], [ㄱ-가-힣]
            if(!ps.matcher(source).matches()){
               return "";
            }

            return null;
        }
    };

    // 등수 대화창
    private AlertDialog createDialog(){
        AlertDialog.Builder ab = new AlertDialog.Builder(PopupActivity.this, AlertDialog.THEME_HOLO_LIGHT);
        ab.setTitle("Best Record");

        ab.setMessage(best_record + " sec!");
        ab.setCancelable(true);

        ab.setNegativeButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                setDismiss(mDialog);
            }
        });

        return ab.create();
    }

    private void setDismiss(Dialog dialog){
        if(dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    private void resetGame(){
        initValue();

        GridLayout gridLayout = (GridLayout) findViewById(R.id.gridlayout);

        gridLayout.setColumnCount(col);
        gridLayout.setRowCount(row);

        gridLayout.removeAllViews();

        Button button[] = new Button[level];
        Random ran = new Random();
        HashSet lotto_num = new HashSet();

        for (;;){
            lotto_num.add(ran.nextInt(level));
            if((lotto_num.size())==level) break;
        }

        Iterator<Integer> itr = lotto_num.iterator();
        int ran_num[] = new int [level];
        int j=0;
        while(itr.hasNext()){
            ran_num[j] = (int)itr.next();
            j++;
        }
        for (int i=0; i < level ;i++){
            int temp;
            int ran1, ran2;
            ran1 = ran.nextInt(level);
            ran2 = ran.nextInt(level);
            if(ran1 != ran2) {
                temp = ran_num[ran1];
                ran_num[ran1] = ran_num[ran2];
                ran_num[ran2] = temp;
            }
        }
        // UI 부분 3 X 3 으로 이미 생성된 배경에 버튼을 붙여 넣는다.
        for(int i=0; i < level ;i++){ // 버튼 생성 부분 , 난수 처리가 필요하다.
            button[i] = new Button(this);
            button[i].setId(ran_num[i]);
            button[i].setText(String.valueOf(ran_num[i] + 1));
            //button[i].setWidth(dp(16));
            button[i].setWidth(200);
            gridLayout.addView(button[i]);
            button[i].setOnClickListener(this); // 이벤트 핸들러 달기
        }

        //timer.stop();
        //timer.start2();

    }

    private int dp(int num){
        return (int)(num * context.getResources().getDisplayMetrics().density);
    }

    private void initValue(){
        cnt = 0;
        start_time = System.currentTimeMillis();

        // 효과음 - soundpool
        // API 21 version
        // https://stackoverflow.com/questions/29600900/soundpool-builder-samples-no-ready
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        soundId[0] = soundPool.load(PopupActivity.this, R.raw.end_game, 1);
        soundId[1] = soundPool.load(PopupActivity.this, R.raw.grid_click, 1);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == cnt ){
            soundPool.play(soundId[1], 0.2f, 0.2f, 0, 0, 1f);
            v.setVisibility(View.INVISIBLE);
            cnt++;    // 카운트를 올려 다음 카운트시 +1
        }

        if(cnt == level){ // 끝나는 부분
            end_time = System.currentTimeMillis();
            double record = (end_time-start_time) / 1000;
            Toast.makeText(getApplicationContext(), "Congratulation!!!"+"\n"+" Record: " + String.format("%.2f", record) + "sec", Toast.LENGTH_SHORT).show();
            // ** parameters **
            // 1: 리소스 식별
            // 2-3: 소리크기
            // 4: 우선순위
            // 5: 파라미터 반복정보(0==반복안함, 1==1번반복(총2번), -1==무한반복)
            // 6: 재생속도(1==1x, 2==2x)
            soundPool.play(soundId[0], 0.2f, 0.2f, 0, 0, 1f);

            if(record < best_time){
                best_time = record;
                best_record = String.valueOf(String.format("%.2f", best_time));
            }
        }
    }

    private class JSONWeatherTask extends AsyncTask<String, Void, Weather>{
        @Override
        protected Weather doInBackground(String... params) {
            Weather weather = new Weather();
            String data = ( (new WeatherHttpClient()).getWeatherData(params[0]));

            try {
                weather = JSONWeatherParser.getWeather(data);

                // Let's retrieve the icon
                weather.iconData = ( (new WeatherHttpClient()).getImage(weather.currentCondition.getIcon()));


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return weather;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(), "날씨를 측정하고 있습니다 ^^", LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Weather weather) {

            super.onPostExecute(weather);

            if (weather.iconData != null && weather.iconData.length > 0) {
                Bitmap img = BitmapFactory.decodeByteArray(weather.iconData, 0, weather.iconData.length);
                imgView.setImageBitmap(img);
            }

            cityText.setText("도시 " + weather.location.getCity() + "," + weather.location.getCountry());
            condDescr.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
            temp.setText("" + Math.round((weather.temperature.getTemp() - 273.15)) + "℃");
            hum.setText("" + weather.currentCondition.getHumidity() + "%");
            press.setText("" + weather.currentCondition.getPressure() + " hPa");
            windSpeed.setText("" + weather.wind.getSpeed() + " mps");

            String windDegW = "";
            float windDegF = weather.wind.getDeg();

            if(337 < windDegF || windDegF < 23){
                windDegW = "북풍";
            }else if(23 < windDegF && windDegF < 68){
                windDegW = "북동풍";
            }else if(68 < windDegF && windDegF < 113){
                windDegW = "동풍";
            }else if(113 < windDegF && windDegF < 158){
                windDegW = "남동풍";
            }else if(158 < windDegF && windDegF < 203){
                windDegW = "남풍";
            }else if(203 < windDegF && windDegF < 248){
                windDegW = "남서풍";
            }else if(248 < windDegF && windDegF < 293){
                windDegW = "서풍";
            }else if(293 < windDegF && windDegF < 337){
                windDegW = "북서풍";
            }

            windDeg.setText("" + windDegW + "("+windDegF+"′)");
            //windDeg.setText("" + weather.wind.getDeg() + "도");
        }
    }

    private class GetApiTask extends AsyncTask<String, Void, String[]>{
        private final String LOG_TAG = "GetApiTask";

        private String[] GetApiJSON(String bufferStr) throws JSONException {
            String LOG_TAG = "GetApiJSON";
            Log.d(LOG_TAG, bufferStr);

            String[] resultStrs = new String[7];

            JSONObject castJson = new JSONObject(bufferStr);

            Log.d(LOG_TAG, castJson.getJSONObject("3804945").getString("name"));

            /*
            JSONArray jsonArray = castJson.getJSONArray("list");

            for (int i=0; i<jsonArray.length(); i++) {
                String id;

                JSONObject api = jsonArray.getJSONObject(i);
                id = api.getJSONObject("")


                resultStrs[i] = i + " 번째 value : " +

            }
            */






            for (String s : resultStrs) {
                Log.v(LOG_TAG, "resultStrs : " + s);
            }

            return resultStrs;

        }

        @Override
        protected String[] doInBackground(String... params) {
            TextView textView = (TextView) findViewById(R.id.rest_view);

            if (params.length == 0) {
                return null;
            }else{
                //textView.setText(params[0]);
            }


            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String forecastJsonStr = null;

            String format = "json";
            String units = "metric";
            int numDays = 7;
            String language = "zh_cn";

            try {
                final String URL =
                        "https://kr.api.pvp.net/api/lol/kr/v1.4/summoner/3804945?";

                Uri builtUri = Uri.parse(URL).buildUpon()
                        .appendQueryParameter("api_key", "ace2b701-4682-4dd7-a78b-322a8de6be5b")
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null; // 아무것도 하지 않음
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                forecastJsonStr = buffer.toString();

            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Error", e);
                e.printStackTrace();
            } catch (ProtocolException e) {
                Log.e(LOG_TAG, "Error", e);
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error", e);
                e.printStackTrace();
            }finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return GetApiJSON(forecastJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return new String[0];
        }
    }






    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(mMediaPlayer != null){
            if(mMediaPlayer.isPlaying()) mMediaPlayer.stop();
        }
    }

    private void musicStart(){
        if(mMediaPlayer != null){
           if(mMediaPlayer.isPlaying()) mMediaPlayer.stop();
        }

        mLinearLayout.removeAllViews();

        mMediaPlayer = MediaPlayer.create(this, R.raw.dearest);

        mVisualizerView = new VisualizerView(this);

        mVisualizerView.setLayoutParams(
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )
        );

        mLinearLayout.addView(mVisualizerView);

        mVisualizer = new Visualizer(mMediaPlayer.getAudioSessionId());

        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener(){
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate){
                mVisualizerView.updateVisualizer(bytes);
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {}

        }, Visualizer.getMaxCaptureRate() / 2, true, false);

        mVisualizer.setEnabled(true);

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mVisualizer.setEnabled(false);
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        });

        //mMediaPlayer = MediaPlayer.create(this, R.raw.laputa_org);
        mMediaPlayer.start();


    }

    private void musicStop(){
        if(mMediaPlayer != null || mVisualizer != null){
            mVisualizer.setEnabled(false);
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        if(mLinearLayout != null){
            mLinearLayout.removeAllViews();
        }
    }


    class myWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;
        }
    }

    // Custom Spinner
    private ArrayList<CustomSpinner> populateReindeer(){

        final ArrayList<CustomSpinner> deer = new ArrayList<CustomSpinner>();

        deer.add(new CustomSpinner("soccer ball", R.drawable.spinner1));
        deer.add(new CustomSpinner("speaker", R.drawable.spinner2));
        deer.add(new CustomSpinner("battery", R.drawable.spinner3));
        deer.add(new CustomSpinner("camera", R.drawable.spinner4));

        return deer;
    }

    private class CustomSpinner {
        private String name;
        private int resourceId;

        CustomSpinner(String name, int resourceId){
            this.name = name;
            this.resourceId = resourceId;
        }

        public String getName(){
            return name;
        }

        public int getResourceId(){
            return resourceId;
        }

        @Override
        public String toString() {
            return getName();
        }
    }

    public class CustomSpinnerAdapter extends ArrayAdapter<CustomSpinner>{
        private Activity context;
        ArrayList<CustomSpinner> arrayList;

        public CustomSpinnerAdapter(Activity context, int resource, ArrayList<CustomSpinner> arrayList) {
            super(context, resource, arrayList);
            this.context = context;
            this.arrayList = arrayList;

        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View row = convertView;

            if (row == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                row = inflater.inflate(R.layout.spinner_row, parent, false);
            }

            CustomSpinner customSpinner = arrayList.get(position);

            ImageView profile = (ImageView) row.findViewById(R.id.profile);
            profile.setBackgroundResource(customSpinner.getResourceId());

            TextView textView = (TextView) row.findViewById(R.id.spinner_text);
            textView.setText(customSpinner.getName());

            return row;
        }
    }
}
