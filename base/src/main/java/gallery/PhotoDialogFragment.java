package gallery;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import edu.scau.base.R;
import photoview.PhotoViewAttacher;
import ui.layout.SmoothImageView;

/**
 * Created by ！ on 2016/8/31.
 */
public class PhotoDialogFragment extends DialogFragment{

    View mRootView;
    MyPagerAdapter mPagerAdapter;
    List<View> mViewList;
    boolean mInited = false;
    SimpleViewTarget[] mSimpleViewTargets;
    HackyViewPager mViewPager;

    @Deprecated
    public static void navigate(Activity activity, View iv ,List<String> urls , int position) {
//        int[] location = new int[2];
//        iv.getLocationOnScreen(location);
        Bundle data = new Bundle();
//        data.putInt("locationX",location[0]);
//        data.putInt("locationY",location[1]);
//        data.putInt("width",iv.getWidth());
//        data.putInt("height",iv.getHeight());
        data.putSerializable("viewTarget",new SimpleViewTarget(iv));
        data.putInt("urlposition",position);
        iv.setDrawingCacheEnabled(true);
        data.putParcelable("viewShot",iv.getDrawingCache());
        data.putSerializable("urls",urls.toArray());
        PhotoDialogFragment photoDialogFragment = new PhotoDialogFragment();
        photoDialogFragment.setArguments(data);
        photoDialogFragment.show(activity.getFragmentManager(), "PhotoDialogFragment");
    }

    public static void navigate(Activity activity, View iv ,String url , int position) {
        Bundle data = new Bundle();
        SimpleViewTarget[] simpleViewTargets = {new SimpleViewTarget(iv)};
        data.putSerializable("viewTargetList",simpleViewTargets);
        data.putInt("urlposition",position);
        iv.setDrawingCacheEnabled(true);
        data.putParcelable("viewShot",iv.getDrawingCache());
        String[] urls = {url};
        data.putSerializable("urls",urls);
        PhotoDialogFragment photoDialogFragment = new PhotoDialogFragment();
        photoDialogFragment.setArguments(data);
        photoDialogFragment.show(activity.getFragmentManager(), "PhotoDialogFragment");
    }

    public static void navigate(Activity activity, List<View> ivs ,List<String> urls , int position) {
        Bundle data = new Bundle();
        SimpleViewTarget[] simpleViewTargets = new SimpleViewTarget[ivs.size()];
        for(int i = 0;i<ivs.size();i++){
            simpleViewTargets[i] = new SimpleViewTarget(ivs.get(i));
        }
        data.putSerializable("viewTargetList",simpleViewTargets);

        data.putInt("urlposition",position);
        ivs.get(position).setDrawingCacheEnabled(true);
        data.putParcelable("viewShot",ivs.get(position).getDrawingCache());
        data.putSerializable("urls",urls.toArray());
        PhotoDialogFragment photoDialogFragment = new PhotoDialogFragment();
        photoDialogFragment.setArguments(data);
        photoDialogFragment.show(activity.getFragmentManager(), "PhotoDialogFragment");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
//        setStyle(DialogFragment.STYLE_NORMAL, R.style.style_dialog);
        mSimpleViewTargets = (SimpleViewTarget[]) getArguments().getSerializable("viewTargetList");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        mRootView = inflater.inflate(R.layout.fragment_photo, container);

        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            Boolean backPress = false;
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK&&!backPress){
                    backPress=true;
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
                            getDialog().cancel();
                        }

                        @Override
                        public void onTransformStart() {

                        }
                    });
                    smoothImageView.transformOut();
                    return true;
                }
                return false;
            }
        });

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
//            super.destroyItem(container, position, object);
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

                Bitmap bitmap = getArguments().getParcelable("viewShot");

                imageView.setOriginalInfo(mWidth, mHeight, mLocationX, mLocationY);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                container.addView(imageView,0);
                imageView.setImageBitmap(bitmap);
                Glide.with(getActivity()).load((String) murls[position])
                        .asBitmap()
                        .into(imageView);
                imageView.transformIn();
            }
            else {
                viewList.add(imageView);
                container.addView(imageView,0);
                Glide.with(getActivity()).load((String) murls[position]).into(imageView);
            }
            imageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                Boolean tap = false;
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    if(!tap){
                        tap=true;
                        int position = mViewPager.getCurrentItem();
                        SmoothImageView smoothImageView = (SmoothImageView) view;
                        int mLocationX = mSimpleViewTargets[position].getLocationX();
                        int mLocationY = mSimpleViewTargets[position].getLocationY();
                        int mWidth = mSimpleViewTargets[position].getWidth();
                        int mHeight = mSimpleViewTargets[position].getHeight();
                        smoothImageView.setOriginalInfo(mWidth, mHeight, mLocationX, mLocationY);
                        smoothImageView.setOnTransformListener(new SmoothImageView.TransformListener() {
                            @Override
                            public void onTransformComplete(int mode) {
                                getDialog().cancel();
                            }

                            @Override
                            public void onTransformStart() {

                            }
                        });
                        smoothImageView.transformOut();
                    }
                }

                @Override
                public void onOutsidePhotoTap() {
                    if(!tap){
                        tap=true;
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
                                getDialog().cancel();
                            }

                            @Override
                            public void onTransformStart() {

                            }
                        });
                        smoothImageView.transformOut();
                    }
                }
            });
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
