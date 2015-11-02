package teambutterflower.co.kr.myportfolio.views.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import teambutterflower.co.kr.myportfolio.R;


/**
 *
 */
public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        ImageView imageView = (ImageView) findViewById(R.id.splash_image);
/*
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.setInterpolator(new AccelerateInterpolator());

        Animation ani02 = new RotateAnimation(0, 45);
        ani02.setDuration(1500);
        animationSet.addAnimation(ani02);

        imageView.setAnimation(animationSet);

*/

        // xml 애니메이션
        //Animation animation = AnimationUtils.loadAnimation(this, R.anim.spin2);
        //imageView.startAnimation(animation);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 3000);

    }
}
