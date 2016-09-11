package gallery;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import base.BaseActivity;
import edu.scau.base.R;

/**
 * Created by ！ on 2016/9/11.
 */
public class PhotoActivity extends BaseActivity{

    private PhotoFragment mPhotoFragment;

    public static void navigate(Activity activity, ViewGroup imagesParent, List<String> urls , int position) {
        List<View> ivs = new ArrayList<>();
        for(int i = imagesParent.getChildCount()-1;i>=0;i--){
            ivs.add(0,imagesParent.getChildAt(i));
        }
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

        Intent intent = new Intent(activity,PhotoActivity.class);
        intent.putExtra("data",data);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        activity.startActivity(intent);
    }

    public static void navigate(Activity activity, View iv , String url , int position) {
        Bundle data = new Bundle();
        SimpleViewTarget[] simpleViewTargets = {new SimpleViewTarget(iv)};
        data.putSerializable("viewTargetList",simpleViewTargets);
        data.putInt("urlposition",position);
        iv.setDrawingCacheEnabled(true);
        data.putParcelable("viewShot",iv.getDrawingCache());
        String[] urls = {url};
        data.putSerializable("urls",urls);

        Intent intent = new Intent(activity,PhotoActivity.class);
        intent.putExtra("data",data);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        activity.startActivity(intent);
    }

    public static void navigate(Activity activity, List<View> ivs , List<String> urls , int position) {
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

        Intent intent = new Intent(activity,PhotoActivity.class);
        intent.putExtra("data",data);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        activity.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_photo;
    }

    @Override
    public void initView() {
        setTranslucentStatus();
        FragmentManager fm = getFragmentManager();
        // 开启Fragment事务
        FragmentTransaction transaction = fm.beginTransaction();

        Bundle data = getIntent().getBundleExtra("data");

        mPhotoFragment = new PhotoFragment();
        mPhotoFragment.setArguments(data);
        mPhotoFragment.setOnTransformListener(new PhotoFragment.OnTransformListener() {
            @Override
            public void onTransformStart(int type) {

            }

            @Override
            public void onTransformCompete(int type) {
                if(type== PhotoFragment.OnTransformListener.TRANSFORM_OUT){
                    finish();
                    overridePendingTransition(0, 0);
                }
            }
        });

        transaction.replace(R.id.fragment, mPhotoFragment);
        // transaction.addToBackStack();
        // 事务提交
        transaction.commit();
    }

    @Override
    public boolean canSwipeBack() {
        return false;
    }

    @Override
    public boolean showColorStatusBar() {
        return false;
    }

    @Override
    public void onBackPressed() {
        mPhotoFragment.transformOut();
    }
}
