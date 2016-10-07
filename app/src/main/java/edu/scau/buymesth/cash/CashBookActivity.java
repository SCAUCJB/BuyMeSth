package edu.scau.buymesth.cash;

import android.app.Activity;
import android.content.Intent;

import base.BaseActivity;
import edu.scau.buymesth.data.bean.User;

/**
 * Created by Jammy on 2016/10/7.
 */
public class CashBookActivity extends BaseActivity{
    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    public void initView() {

    }

    @Override
    public boolean canSwipeBack() {
        return false;
    }

    public static void navigate(Activity activity, User user) {
        Intent intent = new Intent(activity, CashBookActivity.class);
        intent.putExtra("user", user);
        activity.startActivity(intent);
    }
}
