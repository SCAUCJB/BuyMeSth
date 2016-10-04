package edu.scau.buymesth.notice.detail;

import base.BaseActivity;
import edu.scau.buymesth.R;

/**
 * Created by Jammy on 2016/9/30.
 */
public class BuyerRejectActivity extends BaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_buyer_reject;
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
