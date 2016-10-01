package gallery;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.List;

import edu.scau.base.R;
import photoview.PhotoViewAttacher;
import ui.layout.SmoothImageView;

/**
 * Created by ！ on 2016/8/31.
 */
public class PhotoFragment extends Fragment {

    private View mRootView;
    private MyPagerAdapter mPagerAdapter;
    private List<View> mViewList;
    private boolean mInited = false;
    private SimpleViewTarget[] mSimpleViewTargets;
    private HackyViewPager mViewPager;
    private boolean transforming = false;
    private OnTransformListener onTransformListener;
    private PhotoViewAttacher.OnPhotoTapListener onPhotoTapListener;

    public PhotoFragment() {
        super();
        onPhotoTapListener = new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                transformOut();
            }

            @Override
            public void onOutsidePhotoTap() {
                transformOut();
            }
        };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Object[] args = (Object[]) getArguments().getSerializable("viewTargetList");
        SimpleViewTarget[] viewTargetList = new SimpleViewTarget[args.length];
        for(int i = 0;i<args.length;i++){
            viewTargetList[i] = (SimpleViewTarget) args[i];
        }
        mSimpleViewTargets = viewTargetList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        mRootView = inflater.inflate(R.layout.fragment_photo, container,false);
        return mRootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle data = getArguments();

        mViewList = new ArrayList<>();
        Object[] urls = (Object[]) data.getSerializable("urls");
        int posi = data.getInt("urlposition",0);

        mPagerAdapter = new MyPagerAdapter(urls,posi);
        mViewPager = (HackyViewPager) mRootView.findViewById(R.id.view_pager);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(posi);

        IndicatorView indicatorView = (IndicatorView) mRootView.findViewById(R.id.id_indicator);
        indicatorView.setViewPager(mViewPager);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void transformOut(){
        if(!isTransforming()){
            transforming =true;
            int position = mViewPager.getCurrentItem();
            SmoothImageView smoothImageView = (SmoothImageView) mPagerAdapter.getPrimaryItem();
            int mLocationX = mSimpleViewTargets[position].getLocationX();
            int mLocationY = mSimpleViewTargets[position].getLocationY();
            int mWidth = mSimpleViewTargets[position].getWidth();
            int mHeight = mSimpleViewTargets[position].getHeight();
            smoothImageView.setOriginalInfo(mWidth, mHeight, mLocationX, mLocationY);
            smoothImageView.setOnTransformListener(new SmoothImageView.TransformListener() {
                @Override
                public void onTransformComplete(int mode) {
                    if(getOnTransformListener() !=null)
                        getOnTransformListener().onTransformCompete(OnTransformListener.TRANSFORM_OUT);
                    transforming = false;
                }

                @Override
                public void onTransformStart() {
                    if(getOnTransformListener() !=null)
                        getOnTransformListener().onTransformStart(OnTransformListener.TRANSFORM_OUT);
                    transforming = true;
                }
            });
            smoothImageView.transformOut();
        }
    }

    public boolean isTransforming() {
        return transforming;
    }

    public OnTransformListener getOnTransformListener() {
        return onTransformListener;
    }

    public void setOnTransformListener(OnTransformListener onTransformListener) {
        this.onTransformListener = onTransformListener;
    }

    public PhotoViewAttacher.OnPhotoTapListener getOnPhotoTapListener() {
        return onPhotoTapListener;
    }

    public void setOnPhotoTapListener(PhotoViewAttacher.OnPhotoTapListener onPhotoTapListener) {
        this.onPhotoTapListener = onPhotoTapListener;
    }

    public interface OnTransformListener{
        int TRANSFORM_OUT = 1;
        int TRANSFORM_IN = 0;
        void onTransformStart(int type);
        void onTransformCompete(int type);
    }

    public class MyPagerAdapter extends PagerAdapter {

        private List<View> viewList;
        private Object[] murls;
        private int position;

        private View mCurrentView;

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            mCurrentView = (View)object;
        }

        public View getPrimaryItem() {
            return mCurrentView;
        }

        public MyPagerAdapter(Object[] urls,int position) {
            viewList = new ArrayList<>();

            murls = urls;
            this.position = position;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            SmoothImageView imageView = new SmoothImageView(getActivity());
            imageView.setId(0);
            if(position == this.position&& mInited ==false){
                mInited =true;

                int mLocationX = mSimpleViewTargets[position].getLocationX();
                int mLocationY = mSimpleViewTargets[position].getLocationY();
                int mWidth = mSimpleViewTargets[position].getWidth();
                int mHeight = mSimpleViewTargets[position].getHeight();

                imageView.setOriginalInfo(mWidth, mHeight, mLocationX, mLocationY);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                container.addView(imageView,0);

                Glide.with(getActivity()).load((String) murls[position])
                        .asBitmap()
                        .thumbnail(0.4f)
                        .into(new SimpleTarget<Bitmap>(){
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                imageView.setImageBitmap(resource);
                            }
                        });
                imageView.setOnTransformListener(new SmoothImageView.TransformListener() {
                    @Override
                    public void onTransformComplete(int mode) {
                        transforming = false;
                    }

                    @Override
                    public void onTransformStart() {
                        transforming = true;
                    }
                });
                imageView.transformIn();
            }
            else {
                viewList.add(imageView);
                container.addView(imageView,0);
                Glide.with(getActivity()).load((String) murls[position])
                        .asBitmap()
                        .thumbnail(0.4f)
                        .into(new SimpleTarget<Bitmap>(){
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                imageView.setImageBitmap(resource);
                            }
                        });
            }
            imageView.setOnPhotoTapListener(onPhotoTapListener);
            return imageView;
        }

        @Override
        public int getCount() {
            return murls.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }
    }
}
