package edu.scau.buymesth.chat.detail;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import base.BaseActivity;
import butterknife.Bind;
import butterknife.ButterKnife;
import edu.scau.buymesth.R;

/**
 * Created by Jammy on 2016/10/3.
 */
public class SellerDeliverActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_seller_deliver;
    }

    @Override
    public void initView() {

    }

    @Override
    public boolean canSwipeBack() {
        return true;
    }

    @Override
    protected int getToolBarId() {
        return R.id.toolbar;
    }

}
