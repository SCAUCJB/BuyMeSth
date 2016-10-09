package edu.scau.buymesth.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

import base.BaseActivity;
import edu.scau.buymesth.R;

/**
 * Created by ！ on 2016/9/22.
 */

public class EmptyActivity extends BaseActivity {

    private String mTitle = " ";
    private FragmentManager mFragmentManager;
    private Fragment mMainFragment;

    public static void navigate(Activity activity, String className, Bundle arg, String title){
        Intent intent = new Intent(activity,EmptyActivity.class);
        intent.putExtra("fragmentClass",className);
        intent.putExtra("arg",arg);
        intent.putExtra("activityTitle",title);
        activity.startActivity(intent);
    }

    public static void navigate(Activity activity, String className, Bundle arg, int requestCode){
        Intent intent = new Intent(activity,EmptyActivity.class);
        intent.putExtra("fragmentClass",className);
        intent.putExtra("arg",arg);
        activity.startActivityForResult(intent,requestCode);
    }

    public static void navigateForResult(Activity activity, String className, Bundle arg, int requestCode, String title){
        Intent intent = new Intent(activity,EmptyActivity.class);
        intent.putExtra("fragmentClass",className);
        intent.putExtra("arg",arg);
        intent.putExtra("activityTitle",title);
        activity.startActivityForResult(intent,requestCode);
    }

    public static void navigate(Activity activity, String className,Bundle arg){
        Intent intent = new Intent(activity,EmptyActivity.class);
        intent.putExtra("fragmentClass",className);
        intent.putExtra("arg",arg);
        activity.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select;
    }

    @Override
    protected int getToolBarId() {
        return R.id.toolbar;
    }

    @Override
    public void initView() {
        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        String className = getIntent().getStringExtra("fragmentClass");
        try {
            mMainFragment = (Fragment) Class.forName(className).newInstance();
            if(mMainFragment instanceof ButtonOnToolbar){
                ((Button)findViewById(R.id.bt_on_toolbar)).setText(((ButtonOnToolbar)mMainFragment).toolbarsButtonText());
                findViewById(R.id.bt_on_toolbar).setOnClickListener(v -> ((ButtonOnToolbar)mMainFragment).onToolbarButtonClick(v));
            }else {
                findViewById(R.id.bt_on_toolbar).setVisibility(View.GONE);
            }
            mMainFragment.setArguments(getIntent().getBundleExtra("arg"));
            transaction.replace(R.id.content_fragment, mMainFragment);
            transaction.commit();
            if(getIntent().getStringExtra("activityTitle")!=null) this.setTitle(getIntent().getStringExtra("activityTitle"));
//            if(getIntent().getBundleExtra("arg")!=null)
//                this.setTitle(
//                    getIntent().getBundleExtra("arg").getString("activityTitle")==null?
//                            "":getIntent().getBundleExtra("arg").getString("activityTitle"));
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean canSwipeBack() {
        return true;
    }

    @Override
    public int getStatusColorResources() {
        return R.color.colorPrimaryDark;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(mFragmentManager.getFragments()==null || mFragmentManager.getFragments().size()<1)
            return;
        for(Fragment frag: mFragmentManager.getFragments()){
            frag.onActivityResult(requestCode,resultCode,data);
        }
    }

    @Override
    public void onBackPressed() {
        if(mMainFragment instanceof BackPressHandle){
            if(((BackPressHandle)mMainFragment).onBackPressed())return;
            else super.onBackPressed();
        }
        super.onBackPressed();
    }

    public interface ButtonOnToolbar{
        void onToolbarButtonClick(View v);
        String toolbarsButtonText();
    }
}
