package edu.scau.buymesth.notice.detail;

import android.widget.TextView;

import base.BaseActivity;
import butterknife.Bind;
import edu.scau.buymesth.R;

/**
 * Created by Jammy on 2016/9/30.
 */
public class SellerRejectActivity extends BaseActivity {
    @Bind(R.id.tv)
    TextView tv;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_seller_reject;
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
