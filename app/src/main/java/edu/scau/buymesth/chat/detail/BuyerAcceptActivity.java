package edu.scau.buymesth.chat.detail;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import base.BaseActivity;
import butterknife.Bind;
import butterknife.ButterKnife;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Order;
import edu.scau.buymesth.request.requestdetail.RequestDetailActivity;

/**
 * Created by Jammy on 2016/9/30.
 */
public class BuyerAcceptActivity extends BaseActivity {
    Order order;
    @Bind(R.id.request_icon)
    ImageView requestIcon;
    @Bind(R.id.request_title)
    TextView requestTitle;
    @Bind(R.id.image_list)
    LinearLayout imageList;
    @Bind(R.id.ll_request)
    LinearLayout llRequest;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_buyer_accept;
    }

    @Override
    public void initView() {
        order = (Order) getIntent().getSerializableExtra("order");
        if (order.getRequest().getUrls() != null) {
            Glide.with(mContext).load(order.getRequest().getUrls().get(0)).placeholder(R.mipmap.def_head).into(requestIcon);
        }
        requestTitle.setText(order.getRequest().getTitle());
        llRequest.setOnClickListener(v -> {
            RequestDetailActivity.navigate(this,order.getRequest());
        });
        ////TODO:请求网络来填入订单动态
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
