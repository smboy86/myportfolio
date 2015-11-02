package teambutterflower.co.kr.myportfolio.views.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.hb.views.PinnedSectionListView;

import java.util.concurrent.atomic.AtomicInteger;

import teambutterflower.co.kr.myportfolio.R;
import teambutterflower.co.kr.myportfolio.service.MyService;
import teambutterflower.co.kr.myportfolio.utils.BackPressCloseHandler;
import teambutterflower.co.kr.myportfolio.utils.model.ImageAdapter;

public class MainActivity extends ActionBarActivity implements SensorEventListener{
    // viewPager
    private ImageView[] imageViews = null;
    private ImageView imageView = null;
    private ViewPager advPager = null;
    private AtomicInteger what = new AtomicInteger(0);
    private boolean isContinue = true;

    Toolbar toolbar;
    DrawerLayout dlDrawer;
    ActionBarDrawerToggle dtToggle;

    private ListView mListView;

    private boolean hasHeaderAndFooter;
    private boolean isFastScroll;

    // 센서 관련 변수
    SensorManager manager;
    Sensor accelero_sensor;
    Sensor magnetic_sensor;

    //Float azimut;
    float[] mGravity;
    float[] mGeomagnetic;

    private long viewFlipperNextTime = 0;
    private long viewFlipperPreviousTime = 0;

    // back key 종료 관련 변수
    private BackPressCloseHandler backPressCloseHandler;



    @Override
    public void onSensorChanged(final SensorEvent event) {
        /*
        final ViewFlipper viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);

        switch (event.sensor.getType()){
                case Sensor.TYPE_ACCELEROMETER:
                    mGravity = event.values.clone();
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    mGeomagnetic = event.values.clone();
                    break;
            }

            if (mGravity != null && mGeomagnetic != null) { // 둘 다 조사가 되었을 때만 실행
                float R[] = new float[9];
                float I[] = new float[9];
                boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
                if (success) {
                    float orientation[] = new float[3];
                    SensorManager.getOrientation(R, orientation);

//                azimut = orientation[0]; // orientation contains: azimut, pitch and roll

                    float headding = (float) (orientation[0]*(180/Math.PI));

                    if(headding < 0){
                        headding = 360 + headding;
                    }

                    float pitch = (float) (orientation[1]*(180/Math.PI));
                    float rolling = (float) (orientation[2]*(180/Math.PI));

                    if(rolling > 45){
                        if(System.currentTimeMillis() > viewFlipperNextTime + 3000){
                            viewFlipperNextTime = System.currentTimeMillis();
                            viewFlipper.showNext();
                        }
                    }else if (rolling < -45){
                        if(System.currentTimeMillis() > viewFlipperPreviousTime + 3000){
                            viewFlipperPreviousTime = System.currentTimeMillis();
                            viewFlipper.showPrevious();
                        }
                    }
                }
            }
            */
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onStart() {
        super.onStart();
        manager.registerListener(this, accelero_sensor, 1000);
        manager.registerListener(this, magnetic_sensor, 1000);
    }

    @Override
    protected void onPause() {
        super.onStop();
        manager.unregisterListener(this);
    }

    static class Item {
        public static final int ITEM = 0;
        public static final int SECTION = 1;

        public final int type;
        public final String text;

        public int sectionPosition;
        public int listPosition;

        public Item() {
            type = 0;
            text = null;
        }

        public Item(int type, String text) {
            this.type = type;
            this.text = text;
        }

        @Override public String toString() {
            return text;
        }
    }

    public void btnClick(View view) {
        switch (view.getId()){
            case R.id.floating_button:
                Intent intent = new Intent(MainActivity.this, Resume.class);
                startActivity(intent);
                break;
        }
    }

    static class ContentAdapter extends ArrayAdapter<Item> implements PinnedSectionListView.PinnedSectionListAdapter{

        private static final int[] COLORS = new int[] {
                R.color.green_light, R.color.orange_light, R.color.blue_light, R.color.red_light };

        public ContentAdapter(Context context, int resource, int textViewResourceId) {
            super(context, resource, textViewResourceId);
            makeContentList(false);
        }

        public void makeContentList(boolean clear) {
            if (clear) clear();
            prepareSections(2);

            int listPosition = 0;
            int sectionPosition = 0;

            Item section;
            Item item;

            String[] sectionList = {
                    "View and ViewGroup"
                    , "Web"
                    , "Device"
                    , "Media"
                    , "Sensor"
                    , "Widget"
                    , "Mini Game"
                    //, "Development plan"};
            };

            String[][] itemList = {
                    {"LinearLayout - button", "RelativeLayout - login sample", "System Widget"}
                    ,{"WebView", "HttpCache", "Weather API"}
                    ,{"Battery Status", "SMS send"}
                    ,{"Music Visualizer", "Ringtone"}
                    ,{"(temp) Main image rolling on/off"}
                    ,{"(temp) short cut"}
                    ,{"1 to 9"}
            //                        ,{"Sec 2 - Activity", "Sec 1 - Custom Toast Message", "Sec 4 - Flashlight", "Sec 1 - Image worldcup", "Sec 1 - Chart", "Sec 1 - font" }
            };

            for(int i = 0 ;i < sectionList.length; i++ ){
                section = new Item(Item.SECTION, sectionList[i]);
                section.sectionPosition = i;
                section.listPosition = listPosition;
                onSectionAdded(section, sectionPosition);
                add(section);

                for(int j = 0 ; j < itemList[i].length ; j++){
                    item = new Item(Item.ITEM, itemList[i][j]);
                    item.sectionPosition = sectionPosition;
                    item.listPosition = listPosition+1;
                    add(item);
                }
            }
        }

        protected void prepareSections(int sectionsNumber) { }

        protected void onSectionAdded(Item section, int sectionPosition) { }

        @Override public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            view.setTextColor(Color.DKGRAY);
            view.setTag("" + position);
            Item item = getItem(position);
            if (item.type == Item.SECTION) {
                // 15.3.3 API 19버전에서 에러가 나서 바꿈
                //view.setBackgroundColor(parent.getResources().getColor(COLORS[item.sectionPosition % COLORS.length]));
                String strColor = "";
                switch (item.sectionPosition % COLORS.length){
                    case 0:
                        strColor = "#ff99cc00";
                        view.setBackgroundColor(Color.parseColor(strColor));
                        break;
                    case 1:
                        strColor = "#ffffbb33";
                        view.setBackgroundColor(Color.parseColor(strColor));
                        break;
                    case 2:
                        strColor = "#ffff4444";
                        view.setBackgroundColor(Color.parseColor(strColor));
                        break;
                    case 3:
                        strColor = "#ff33b5e5";
                        view.setBackgroundColor(Color.parseColor(strColor));
                        break;
                    default:
                        strColor = "#FFB4ECFF";
                        view.setBackgroundColor(Color.parseColor(strColor));
                        break;
                }

            }
            return view;
        }

        @Override public int getViewTypeCount() {
            return 2;
        }

        @Override public int getItemViewType(int position) {
            return getItem(position).type;
        }

        @Override
        public boolean isItemViewTypePinned(int viewType) {
            return viewType == Item.SECTION;
        }

    }

    static class FastScrollAdapter extends ContentAdapter implements SectionIndexer {

        private Item[] sections;

        public FastScrollAdapter(Context context, int resource, int textViewResourceId) {
            super(context, resource, textViewResourceId);
        }

        @Override protected void prepareSections(int sectionsNumber) {
            sections = new Item[sectionsNumber];
        }

        @Override protected void onSectionAdded(Item section, int sectionPosition) {
            sections[sectionPosition] = section;
        }

        @Override public Item[] getSections() {
            return sections;
        }

        @Override public int getPositionForSection(int section) {
            if (section >= sections.length) {
                section = sections.length - 1;
            }
            return sections[section].listPosition;
        }

        @Override public int getSectionForPosition(int position) {
            if (position >= getCount()) {
                position = getCount() - 1;
            }
            return getItem(position).sectionPosition;
        }

    }

    /////////// Adapter 초기화 시작 //////////
    private void initializePadding(ListView mListView) {
        float density = getResources().getDisplayMetrics().density;
        int padding = (int) (16 * density);
        mListView.setPadding(padding, padding, padding, padding);
    }

    private void initializeHeaderAndFooter(ListView mListView) {
        mListView.setAdapter(null);
        if (hasHeaderAndFooter) {
            ListView list = (ListView) findViewById(R.id.list_view);

            LayoutInflater inflater = LayoutInflater.from(this);
            TextView header1 = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, list, false);
            header1.setText("First header");
            list.addHeaderView(header1);

            TextView header2 = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, list, false);
            header2.setText("Second header");
            list.addHeaderView(header2);

            TextView footer = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, list, false);
            footer.setText("Single footer");
            list.addFooterView(footer);
        }
        initializeAdapter(mListView);
    }

    @SuppressLint("NewApi")
    private void initializeAdapter(ListView mListView) {
        if (isFastScroll) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mListView.setFastScrollAlwaysVisible(true);
            }
            mListView.setAdapter(new FastScrollAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1));
        } else {
            mListView.setAdapter(new ContentAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1));
        }
    }

    /////////// Adapter 초기화 끝 //////////


    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, teambutterflower.co.kr.myportfolio.views.activities.SplashActivity.class));
        setContentView(R.layout.activity_main);

        //initViewPager();
        initViewPagerSimple();


        // 백키 두번 종료
        backPressCloseHandler = new BackPressCloseHandler(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        dlDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        dtToggle = new ActionBarDrawerToggle(this, dlDrawer, R.string.app_name, R.string.app_name);
        dlDrawer.setDrawerListener(dtToggle);

        /// 리스트
        if (savedInstanceState != null) {
            isFastScroll = savedInstanceState.getBoolean("isFastScroll");
            hasHeaderAndFooter = savedInstanceState.getBoolean("hasHeaderAndFooter");
        }

        mListView = (ListView) findViewById(R.id.list_view);

        initializeHeaderAndFooter(mListView);
        initializeAdapter(mListView);
        initializePadding(mListView);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item item = (Item) mListView.getAdapter().getItem(position);
                if (item != null) {
                    Toast.makeText(MainActivity.this, "Total List Number " + position + ": " + item.text + "\n"
                            + "Detail Number in Section " + item.listPosition, Toast.LENGTH_SHORT).show();

                    switch (item.text){
                        case "LinearLayout - button":
                            Intent intent = new Intent(MainActivity.this, teambutterflower.co.kr.myportfolio.views.activities.PopupActivity.class);
                            intent.putExtra("viewName", "LinearLayout");
                            startActivity(intent);
                            break;

                        case "RelativeLayout - login sample":
                            intent = new Intent(MainActivity.this, teambutterflower.co.kr.myportfolio.views.activities.PopupActivity.class);
                            intent.putExtra("viewName", "RelativeLayout");
                            startActivity(intent);
                            break;

                        case "(temp) Custom Toast Message":
                            Toast.makeText(MainActivity.this, "smPark : " + mSearchItem.getTitle() , Toast.LENGTH_SHORT).show();
                            mSearchView.clearFocus();
                            mSearchView.setIconified(true);
                            break;


                        case "WebView":
                            intent = new Intent(MainActivity.this, teambutterflower.co.kr.myportfolio.views.activities.PopupActivity.class);
                            intent.putExtra("viewName", "WebView");
                            startActivity(intent);

                            break;

                        case "Music Visualizer":
                            intent = new Intent(MainActivity.this, teambutterflower.co.kr.myportfolio.views.activities.PopupActivity.class);
                            intent.putExtra("viewName", "Music Visualizer");
                            startActivity(intent);
                            break;

                        case "Ringtone":
                            intent = new Intent(MainActivity.this, teambutterflower.co.kr.myportfolio.views.activities.PopupActivity.class);
                            intent.putExtra("viewName", "Ringtone");
                            startActivity(intent);
                            break;

                        case "Battery Status": // 15.3.20 Add
                            intent = new Intent(MainActivity.this, teambutterflower.co.kr.myportfolio.views.activities.PopupActivity.class);
                            intent.putExtra("viewName", "Battery Status");
                            startActivity(intent);
                            break;

                        case "HttpCache": // 15.3.24 Add
                            intent = new Intent(MainActivity.this, teambutterflower.co.kr.myportfolio.views.activities.PopupActivity.class);
                            intent.putExtra("viewName", "HttpCache");
                            startActivity(intent);
                            break;

                        case "REST API": // 15.3.24 Add
                            intent = new Intent(MainActivity.this, teambutterflower.co.kr.myportfolio.views.activities.PopupActivity.class);
                            intent.putExtra("viewName", "REST API");
                            startActivity(intent);
                            break;

                        case "Weather API": // 15.3.25 Add
                            intent = new Intent(MainActivity.this, teambutterflower.co.kr.myportfolio.views.activities.PopupActivity.class);
                            intent.putExtra("viewName", "Weather API");
                            startActivity(intent);
                            break;

                        case "System Widget": // 15.3.26 Add
                            intent = new Intent(MainActivity.this, teambutterflower.co.kr.myportfolio.views.activities.PopupActivity.class);
                            intent.putExtra("viewName", "System Widget");
                            startActivity(intent);
                            break;

                        case "1 to 9": // 15.3.26 Add
                            intent = new Intent(MainActivity.this, teambutterflower.co.kr.myportfolio.views.activities.PopupActivity.class);
                            intent.putExtra("viewName", "1 to 9");
                            startActivity(intent);
                            break;

                        case "SMS send": // 15.3.26 Add
                            intent = new Intent(MainActivity.this, teambutterflower.co.kr.myportfolio.views.activities.PopupActivity.class);
                            intent.putExtra("viewName", "SMS send");
                            startActivity(intent);
                            break;

                        default:
                            break;

                    }
                } else {
                    Toast.makeText(MainActivity.this, "Item " + position, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 메뉴바 클릭 이벤트
        TextView email = (TextView) findViewById(R.id.menu_email);
        email.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:smboy86@naver.com"));
                startActivity(phoneIntent);
            }
        });

        TextView phone = (TextView) findViewById(R.id.menu_phone);
        phone.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:01071230816"));
                startActivity(phoneIntent);
            }
        });

        TextView sms = (TextView) findViewById(R.id.menu_sms);
        sms.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:01071230816"));
                startActivity(smsIntent);
            }
        });

        TextView naver = (TextView) findViewById(R.id.menu_naver);
        naver.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent naverIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://section.blog.naver.com/connect/PopConnectBuddyAddForm.nhn?blogId=smboy86"));
                startActivity(naverIntent);
            }
        });

        TextView faceBook = (TextView) findViewById(R.id.menu_facebook);
        faceBook.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "facebook button", Toast.LENGTH_SHORT).show();
            }
        });

        // 접근 센서
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelero_sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetic_sensor = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // 검색바

    }

    SearchView mSearchView;
    MenuItem mSearchItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);

        /*
        mSearchView = new SearchView(getSupportActionBar().getThemedContext());
        mSearchView.setQueryHint("Content Search");

        MenuItem searchItem = menu.findItem(R.id.action_search);

        return true;
        */

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뭐하는 놈인가? 화살표 생김

        mSearchView = new SearchView(getSupportActionBar().getThemedContext());

        // 1. Seach bar
        mSearchView.setQueryHint("Content Search");

        menu.add("Search")
                .setIcon(R.drawable.ic_action_search)
                .setTitle("search")
                .setActionView(mSearchView)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        mSearchItem = menu.getItem(1);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(dtToggle.onOptionsItemSelected(item)){
            return true;
        }

        //Toast.makeText(this, item.getItemId(), Toast.LENGTH_SHORT).show();

        switch (item.getItemId()){
            case R.id.action_settings:
                Toast.makeText(this, "action_settings", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        dtToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        dtToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        // 임시방편, 서비스 실행시 앱을 강제종료하고 앱 재실행시 서비스가 자동 실행되는데
        // 이를 방지하기 위한 명시적인 서비스 스탑 (다른 깔끔한 방법이 있을 것 같다.)
        Intent serivceIntent = new Intent(this, MyService.class);
        stopService(serivceIntent);

        super.onDestroy();
    }

    // http://blog.csdn.net/dlutbrucezhang/article/details/8736807
    private void initViewPagerSimple() {
        advPager = (ViewPager) findViewById(R.id.viewPager);
        ImageAdapter imageAdapter = new ImageAdapter(this);
        advPager.setAdapter(imageAdapter);

        ViewGroup group = (ViewGroup) findViewById(R.id.viewGroup);

        imageViews = new ImageView[imageAdapter.galImages.length];

        for (int i = 0; i < imageAdapter.galImages.length; i++) {
            imageView = new ImageView(this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(25, 25));
            imageView.setPadding(10, 10, 10, 10);
            imageView.setBackgroundResource(imageAdapter.galImages[i]);
            imageViews[i] = imageView;
            if (i == 0) {
                imageViews[i]
                        .setBackgroundResource(R.drawable.main_view_group_sel);
            } else {
                imageViews[i]
                        .setBackgroundResource(R.drawable.main_view_group_desel);
            }
            group.addView(imageViews[i]);
        }

        advPager.setOnPageChangeListener(new GuidePageChangeListener());
        advPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        isContinue = false;
                        break;
                    case MotionEvent.ACTION_UP:
                        isContinue = true;
                        break;
                    default:
                        isContinue = true;
                        break;
                }
                return false;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (isContinue) {
                        viewHandler.sendEmptyMessage(what.get());
                        whatOption();
                    }
                }
            }
        }).start();
    }

    private final class GuidePageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {
            what.getAndSet(arg0);
            for (int i = 0; i < imageViews.length; i++) {
                imageViews[arg0]
                        .setBackgroundResource(R.drawable.main_view_group_sel);
                if (arg0 != i) {
                    imageViews[i]
                            .setBackgroundResource(R.drawable.main_view_group_desel);
                }
            }
        }
    }

    private final Handler viewHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            advPager.setCurrentItem(msg.what);
            super.handleMessage(msg);
        }
    };

    // window git clone �뿉�윭�떆
    //http://blog.naver.com/PostView.nhn?blogId=hursh1225&logNo=40192159662&redirect=Dlog&widgetTypeCall=true
    private void whatOption() {
        what.incrementAndGet();
        if (what.get() > imageViews.length - 1) {
            what.getAndAdd(-8);
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {

        }
    }

    // 다이얼로그에서 이렇게 보내도 에러
    public void market(){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=teambutterflower.co.kr.MyPortfolio")));
    }

}
