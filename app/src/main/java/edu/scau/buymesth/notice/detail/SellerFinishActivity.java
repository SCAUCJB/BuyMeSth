package edu.scau.buymesth.notice.detail;

import base.BaseActivity;
import edu.scau.buymesth.R;

/**
 * Created by Jammy on 2016/10/3.
 */
public class SellerFinishActivity extends BaseActivity{
    @Override
    protected int getLayoutId() {
        return R.layout.activity_seller_finish;
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
