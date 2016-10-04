package edu.scau.buymesth.chat.detail;

import base.BaseActivity;
import edu.scau.buymesth.R;

/**
 * Created by Jammy on 2016/10/3.
 */
public class BuyerFinishActivity extends BaseActivity{
    @Override
    protected int getLayoutId() {
        return R.layout.activity_buyer_finish;
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
