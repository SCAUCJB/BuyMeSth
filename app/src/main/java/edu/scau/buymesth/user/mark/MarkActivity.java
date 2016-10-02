package edu.scau.buymesth.user.mark;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import base.BaseActivity;
import edu.scau.buymesth.R;

/**
 * Created by John on 2016/10/2.
 */

public class MarkActivity extends BaseActivity {
    public static void navigate(Context context ){
        Intent intent =new Intent(context,MarkActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_mark;
    }

    @Override
    protected int getToolBarId() {
        return R.id.toolbar;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            return;
        }
        MarkRequestFragment markRequestFragment = new MarkRequestFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.container, markRequestFragment).commit();
    }
    @Override
    public void initView() {

    }

    @Override
    public boolean canSwipeBack() {
        return true;
    }
}
