package edu.scau.buymesth.createorder;

import base.BaseActivity;
import edu.scau.buymesth.R;

/**
 * Created by John on 2016/8/29.
 */

public class CreateOrderActivity extends BaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_order;
    }

    @Override
    public void initView() {
    }
    protected  int getToolBarId(){
        return  R.id.toolbar;
    }

    @Override
    public boolean canSwipeBack() {
        return true;
    }
}
