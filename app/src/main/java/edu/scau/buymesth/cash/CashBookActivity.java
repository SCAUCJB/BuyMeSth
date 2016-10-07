package edu.scau.buymesth.cash;

import android.app.Activity;
import android.content.Intent;

import base.BaseActivity;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.User;

/**
 * Created by Jammy on 2016/10/7.
 */
public class CashBookActivity extends BaseActivity{
    User user;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_cashbook;
    }

    @Override
    public void initView() {
        user = (User) getIntent().getSerializableExtra("user");
        ////TODO:复合查询


    }

    @Override
    public boolean canSwipeBack() {
        return true;
    }

    public static void navigate(Activity activity, User user) {
        Intent intent = new Intent(activity, CashBookActivity.class);
        intent.putExtra("user", user);
        activity.startActivity(intent);
    }
}
