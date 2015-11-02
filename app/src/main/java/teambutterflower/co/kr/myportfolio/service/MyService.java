package teambutterflower.co.kr.myportfolio.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import teambutterflower.co.kr.myportfolio.views.activities.PopupActivity;

/**
 * Created by smPark on 2015-03-07.
 */
public class MyService extends Service{

    Context mContext;

    private MediaPlayer mp;

    PopupActivity popupActivity;


    @Override
    public void onCreate() {
        super.onCreate();
/*
        mp = MediaPlayer.create(this, R.raw.laputa_org);
        mp.start();        // 미디어 플레이어 실행

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release(); // 이곳에서 service stop을 call 하는 것은 어떨까?
            }
        });
*/
        //LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //View layoutview = inflater.inflate(R.layout.pop_service, null);


        //mLinearLayout = (LinearLayout) view.findViewById(R.id.parent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!mp.isPlaying()) mp.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if(mp != null) {
            mp.release();
        }

    }
}