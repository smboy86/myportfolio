package teambutterflower.co.kr.myportfolio.utils.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import teambutterflower.co.kr.myportfolio.R;


public class ImageAdapter extends PagerAdapter {
    Context context;
    Bitmap galImage;
    BitmapFactory.Options options;

    public final int[] galImages = new int[] {
            R.drawable.main_0,
            R.drawable.main_1,
            R.drawable.main_2,
            R.drawable.main_3,
            R.drawable.main_4,
            R.drawable.main_5,
            R.drawable.main_6,
            R.drawable.main_7
    };

    public ImageAdapter(Context context) {
        this.context = context;
        options = new BitmapFactory.Options();
    }

    @Override
    public int getCount() {
        return galImages.length;

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ImageView) object);

    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        options.inSampleSize = 1;

        galImage = BitmapFactory.decodeResource(context.getResources(), galImages[position], options);

        imageView.setImageBitmap(galImage);
        ((ViewPager) container).addView(imageView, 0);

        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((ImageView) object);
    }
}