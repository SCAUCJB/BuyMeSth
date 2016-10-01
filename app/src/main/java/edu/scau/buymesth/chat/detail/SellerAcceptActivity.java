package edu.scau.buymesth.chat.detail;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import base.BaseActivity;
import butterknife.Bind;
import butterknife.ButterKnife;
import edu.scau.buymesth.R;

/**
 * Created by Jammy on 2016/9/30.
 */
public class SellerAcceptActivity extends BaseActivity {
    @Bind(R.id.image_list)
    LinearLayout imageList;
    @Bind(R.id.iv_camera)
    ImageView ivCamera;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_seller_accept;
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
