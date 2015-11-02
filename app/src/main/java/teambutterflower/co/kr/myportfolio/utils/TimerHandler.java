package teambutterflower.co.kr.myportfolio.utils;

import android.os.Handler;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class TimerHandler {
	private String TIME_FORMAT = "aa hh:mm:ss";
	private SimpleDateFormat sdf;
	private TextView clock;
	private Handler handler = new Handler();
	private Runnable updater;

	// define update time as millisecond unit
	private long updateTime = UP_SEC;
	
	private static final long THOUSAND = 1000;
	public static final long UP_SEC  = THOUSAND * 1;
	public static final long UP_MIN  = UP_SEC * 60;
	public static final long UP_HOUR = UP_MIN * 60;
	public static final long UP_H_DAY = UP_HOUR * 12;
	public static final long UP_A_DAY = UP_H_DAY * 2;


    double start_time = 0;
    double end_time;

    public TimerHandler(){}
	
	public TimerHandler(TextView clock){
		this.clock = clock;
		sdf = new SimpleDateFormat(TIME_FORMAT, Locale.KOREA);
	}
	
	public TimerHandler(TextView clock, String format){
		TIME_FORMAT = format;
		this.clock = clock;
		sdf = new SimpleDateFormat(TIME_FORMAT, Locale.KOREA);
	}
	
	public TimerHandler(TextView clock, String format, long updateTime){
		this(clock, format);
		this.updateTime = updateTime;
	}
	
	// timer starting(update the time)
	public void start(){
		Timer timer = new Timer();

		TimerTask tt = new TimerTask() {
			public void run() {
				update();
			}
		};
		timer.schedule(tt, 0, updateTime);
	}

    public void start2(){
        Timer timer = new Timer();
        start_time =System.currentTimeMillis();

        TimerTask tt = new TimerTask() {
            public void run() {
                update2();
            }
        };
        timer.schedule(tt, 0, 50);
    }
	
	private void update(){
		updater = new Runnable(){
			public void run(){
				clock.setText(sdf.format(new Date()));
			}
		};
		handler.post(updater);
	}

    private void update2(){
        updater = new Runnable(){
            public void run(){
                double timer = (System.currentTimeMillis() - start_time) / 1000;
                clock.setText(String.format("%.2f", timer));
            }
        };

        handler.post(updater);
    }

    public void stop(){
        handler.removeCallbacks(updater);
    }
}
