package edu.scau.buymesth.notice.detail;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import base.BaseActivity;
import butterknife.Bind;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Order;
import edu.scau.buymesth.userinfo.UserInfoActivity;

/**
 * Created by Jammy on 2016/9/29.
 */
public class BuyOrderDetailActivity extends BaseActivity {
    Order order;
    @Bind(R.id.seller_icon)
    ImageView sellerIcon;
    @Bind(R.id.seller_name)
    TextView sellerName;
    @Bind(R.id.seller_level)
    TextView sellerLevel;
    @Bind(R.id.person_info)
    RelativeLayout personInfo;
    @Bind(R.id.request_icon)
    ImageView requestIcon;
    @Bind(R.id.request_title)
    TextView requestTitle;
    @Bind(R.id.btn_cancle)
    Button btnCancle;
    @Bind(R.id.btn_ok)
    Button btnOk;
    @Bind(R.id.tv_want)
    TextView tvWant;
    @Bind(R.id.tv_seller_price)
    TextView tvSellerPrice;
    @Bind(R.id.tv_seller_tip)
    TextView tvSellerTip;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_buyer_order;
    }

    @Override
    public void initView() {
        personInfo.setOnClickListener(v -> {
            UserInfoActivity.navigate(this,order.getSeller());
        });
        order = (Order) getIntent().getExtras().getSerializable("order");
        Glide.with(this).load(order.getSeller().getAvatar()).placeholder(R.mipmap.def_head).into(sellerIcon);
        sellerName.setText(order.getSeller().getNickname());
        sellerLevel.setText("LV" + String.valueOf(order.getSeller().getExp() / 10));
        if (order.getRequest().getUrls() != null) {
            Glide.with(mContext).load(order.getRequest().getUrls().get(0)).placeholder(R.mipmap.def_head).into(requestIcon);
        }
        requestTitle.setText(order.getRequest().getTitle());
        tvSellerPrice.setText("卖家出价："+order.getPrice()+order.getPriceType());
        tvSellerTip.setText("卖家索要的小费："+order.getTip()+order.getTipType());
        if(order.getRequest().getMinPrice()==null)
        tvWant.setText("你的期望价格："+order.getRequest().getMaxPrice()+"￥");
        else
            tvWant.setText("你的期望价格："+order.getRequest().getMinPrice()+"~"+order.getRequest().getMaxPrice());
        btnOk.setOnClickListener(v -> {
            order.setStatus(Order.STATUS_ACCEPTED);
            order.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {

                }
            });
        });

        btnCancle.setOnClickListener(v -> {
            order.setStatus(Order.STATUS_ACCEPTED);
            order.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {

                }
            });
        });
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
