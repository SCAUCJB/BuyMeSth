package gallery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.Locale;

import edu.scau.base.R;
import photoview.PhotoView;

/**
 * Created by John on 2016/8/1.
 * updated by John on 2016/8/2.
 */
public class GalleryActivity extends Activity {
    public static final String URL_KEY = "Gallery_url";
    public static final String URL_INDEX = "Gallery_index";
    private TextView textView;
    private String[] imageUrls;
    private int index;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_gallery);
        imageUrls = getIntent().getStringArrayExtra(URL_KEY);
        index = getIntent().getIntExtra(URL_INDEX, 0);
        initWidget();
    }
    public static void toGalleryActivity(String[] imageUrls,int index,Context context){
        Intent intent=new Intent(context,GalleryActivity.class);
        intent.putExtra(URL_KEY,imageUrls);
        intent.putExtra(URL_INDEX,index);
        context.startActivity(intent);
    }
    private void initWidget() {
        textView = (TextView) findViewById(R.id.page_title);
        if (imageUrls.length < 2) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setText(String.format(Locale.CHINA+"%d/%d", index + 1, imageUrls.length));
        }

        HackyViewPager mViewPager = (HackyViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(new SamplePagerAdapter(this, imageUrls));
        mViewPager.setCurrentItem(index);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                textView.setText(String.format(Locale.CHINA+"%d/%d", position + 1, imageUrls.length));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    static class SamplePagerAdapter extends PagerAdapter {

        private String[] urls;
        private Activity activity;

        public SamplePagerAdapter(Activity activity, String[] urls) {
            this.urls = urls;
            this.activity = activity;

        }

        @Override
        public int getCount() {
            return urls.length;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            Glide.with(activity).load(urls[position]).diskCacheStrategy(DiskCacheStrategy.ALL).into(photoView);
            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            photoView.setOnViewTapListener(( view,  x,  y)-> activity.finish());
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }
}
