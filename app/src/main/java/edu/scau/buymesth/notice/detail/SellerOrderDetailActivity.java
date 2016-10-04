package edu.scau.buymesth.notice.detail;

import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import base.BaseActivity;
import butterknife.Bind;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Order;

/**
 * Created by Jammy on 2016/9/30.
 */
public class SellerOrderDetailActivity extends BaseActivity {
    Order order;
    @Bind(R.id.request_icon)
    ImageView requestIcon;
    @Bind(R.id.request_title)
    TextView requestTitle;
    @Bind(R.id.tv_want)
    TextView tvWant;
    @Bind(R.id.tv_seller_price)
    TextView tvSellerPrice;
    @Bind(R.id.tv_seller_tip)
    TextView tvSellerTip;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_seller_order;
    }

    @Override
    public void initView() {
        order = (Order) getIntent().getSerializableExtra("order");
        if (order.getRequest().getUrls() != null) {
            Glide.with(mContext).load(order.getRequest().getUrls().get(0)).placeholder(R.mipmap.def_head).into(requestIcon);
        }
        requestTitle.setText(order.getRequest().getTitle());
        if (order.getRequest().getMinPrice() == null)
            tvWant.setText("买家期望价格：" + order.getRequest().getMaxPrice() + "￥");
        else
            tvWant.setText("买家期望价格：" + order.getRequest().getMinPrice() + "~" + order.getRequest().getMaxPrice());
        tvSellerPrice.setText("你的出价:" + order.getPrice() + order.getPriceType());
        tvSellerTip.setText("你索要的小费" + order.getTip() + order.getTipType());
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
