package edu.scau.buymesth.chat.detail;

import base.BaseActivity;
import edu.scau.buymesth.R;

/**
 * Created by Jammy on 2016/9/30.
 */
public class BuyerRejectActivity extends BaseActivity {
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

    @Override
    protected int getToolBarId() {
        return R.id.toolbar;
    }
}
