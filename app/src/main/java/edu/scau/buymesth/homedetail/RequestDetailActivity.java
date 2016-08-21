package edu.scau.buymesth.homedetail;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import base.BaseActivity;
import butterknife.Bind;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Request;

/**
 * Created by John on 2016/8/20.
 */

public class RequestDetailActivity extends BaseActivity {
    public static final String EXTRA_IMAGE = "edu.scau.buymesth.homedetail.extraImage";
    public static final String EXTRA_REQUEST = "edu.scau.buymesth.homedetail.request";
    @Bind(R.id.tv_content)
    TextView mTextView;
    @Bind(R.id.vp_images)
    ViewPager mViewPager;
    private int[] heights;
    private List<ImageView> imageViews;
    private int[] imgIds = {R.mipmap.icon_discover,R.mipmap.default_img_rect,R.mipmap.def_head};
    private PagerAdapter mAdapter;
    private Request mRequest;
    public static void navigate(AppCompatActivity activity, View transitionImage, Request request) {
        Intent intent = new Intent(activity, RequestDetailActivity.class);
        intent.putExtra(EXTRA_REQUEST,request);
//        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, EXTRA_IMAGE);
//        ActivityCompat.startActivity(activity, intent, options.toBundle());
        activity.startActivity(intent);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_requestdetail;
    }

    @Override
    public void initView() {
        initToolBar();
        mRequest= (Request) getIntent().getSerializableExtra(EXTRA_REQUEST);
        mTextView.setText(mRequest.getContent());
        imageViews=new ArrayList<>(imgIds.length);
        heights=new int[imgIds.length];
        if(mRequest.getUrls()==null)
            mViewPager.setVisibility(View.GONE);
//        else{
//            imageViews = new ArrayList<>(mRequest.getUrls().size());
//            heights = new int[ mRequest.getUrls().size()];
//            for(int i=0;i<imageViews.size();++i){
//                ImageView imageView = new ImageView(RequestDetailActivity.this);
//                final int fi=i;
//                Glide.with(mContext).load(mRequest.getUrls().get(i)).listener(new RequestListener<String, GlideDrawable>() {
//                    @Override
//                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                        heights[fi]=mViewPager.getWidth()/resource.getIntrinsicWidth()*resource.getIntrinsicHeight();
//                        Log.d("zhx","height["+fi+"]="+heights[fi]);
//                        return false;
//                    }
//                }).into(imageView);
//                imageViews.add(imageView);
//            }
//            initViewPager();
//        }
        initViewPager();

    }

    private void initViewPager() {
        if(mRequest.getUrls()!=null)
        mViewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                for (int i = 0;i<imgIds.length;i++){
                    ImageView imageView = new ImageView(RequestDetailActivity.this);
                    Drawable iconDrawable = getResources().getDrawable(imgIds[i]);
                 //   先随便设置个初始值，不然显示不行
                   heights[i] = (int) (mViewPager.getWidth()/(float)iconDrawable.getIntrinsicWidth()*iconDrawable.getIntrinsicHeight());

                    if(mRequest.getUrls()!=null){
                        final int fi=i;
                        Glide.with(mContext).load(mRequest.getUrls().get(i)).into(imageView).getSize((width, height) -> {
                            if(fi==2)
                            heights[2]=mViewPager.getWidth()/width*height;
                        });
                    }
                    imageViews.add(imageView);
                }
                mAdapter.notifyDataSetChanged();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mViewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mViewPager.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                //ViewPager的初始高度设置为第一个页面的高度
                ViewGroup.LayoutParams layoutParams = mViewPager.getLayoutParams();
                layoutParams.height = heights[0];
                mViewPager.setLayoutParams(layoutParams);
            }
        });
        mAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return imageViews.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageView imageView = imageViews.get(position);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                container.addView(imageView);
                return imageView;
            }
        };
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position==heights.length-1){
                    return;
                }
                //计算ViewPager现在应该的高度,heights[]表示页面高度的数组。
                int height = (int) (heights[position]*(1-positionOffset)+heights[position+1]*positionOffset);

                //为ViewPager设置高度
                ViewGroup.LayoutParams params = mViewPager.getLayoutParams();
                params.height = height;
                mViewPager.setLayoutParams(params);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener((v)-> onBackPressed());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public int getStatusColorResources() {
        return R.color.colorPrimaryDark;
    }

    @Override
    public boolean showColorStatusBar() {
        return true;
    }

    @Override
    public boolean canSwipeBack() {
        return true;
    }
}
