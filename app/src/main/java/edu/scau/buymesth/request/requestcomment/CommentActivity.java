package edu.scau.buymesth.request.requestcomment;

import android.content.Context;
import android.content.Intent;

import base.BaseActivity;
import edu.scau.Constant;
import edu.scau.buymesth.createorder.CreateOrderActivity;
import edu.scau.buymesth.data.bean.Request;

/**
 * Created by John on 2016/9/6.
 */

public class CommentActivity extends BaseActivity {
    public static void navigateTo(Context context,Request request){
        Intent intent = new Intent(context, CreateOrderActivity.class);
        intent.putExtra(Constant.EXTRA_REQUEST, request);
        context.startActivity(intent);
    }
    @Override
    protected void initPresenter() {
        Request request=(Request) getIntent().getSerializableExtra(Constant.EXTRA_REQUEST);
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    public void initView() {

    }

    @Override
    public boolean canSwipeBack() {
        return true;
    }
}
