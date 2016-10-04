package edu.scau.buymesth.notice.detail;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import base.BaseActivity;
import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Address;
import edu.scau.buymesth.data.bean.Order;
import edu.scau.buymesth.user.address.AddressActivity;
import edu.scau.buymesth.userinfo.UserInfoActivity;

/**
 * Created by Jammy on 2016/9/29.
 */
public class BuyerCreateActivity extends BaseActivity {
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
    @Bind(R.id.ll_address)
    LinearLayout llAddress;
    @Bind(R.id.tv_address)
    TextView tvAddress;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_buyer_order;
    }

    @Override
    public void initView() {
        personInfo.setOnClickListener(v -> {
            UserInfoActivity.navigate(this, order.getSeller());
        });
        order = (Order) getIntent().getExtras().getSerializable("order");
        Glide.with(this).load(order.getSeller().getAvatar()).placeholder(R.mipmap.def_head).into(sellerIcon);
        sellerName.setText(order.getSeller().getNickname());
        sellerLevel.setText("LV" + String.valueOf(order.getSeller().getExp() / 10));
        if (order.getRequest().getUrls() != null) {
            Glide.with(mContext).load(order.getRequest().getUrls().get(0)).placeholder(R.mipmap.def_head).into(requestIcon);
        }
        requestTitle.setText(order.getRequest().getTitle());
        tvSellerPrice.setText("卖家出价：" + order.getPrice() + order.getPriceType());
        tvSellerTip.setText("卖家索要的小费：" + order.getTip() + order.getTipType());
        if (order.getRequest().getMinPrice() == null)
            tvWant.setText("你的期望价格：" + order.getRequest().getMaxPrice() + "￥");
        else
            tvWant.setText("你的期望价格：" + order.getRequest().getMinPrice() + "~" + order.getRequest().getMaxPrice());

        llAddress.setOnClickListener(v -> {
            AddressActivity.navigateForResult(this);
        });

        btnOk.setOnClickListener(v -> {
            if (order.getAddress() == null) {
                Toast.makeText(this, "请选择收货地址", Toast.LENGTH_LONG).show();
                return;
            }
            order.setStatus(Order.STATUS_ACCEPTED);
            order.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        btnOk.setText("已接收");
                        btnOk.setClickable(false);
                        btnCancle.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(BuyerCreateActivity.this, "请重试", Toast.LENGTH_LONG).show();
                    }
                }
            });
        });

        btnCancle.setOnClickListener(v -> {
            order.setStatus(Order.STATUS_ACCEPTED);
            order.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        btnCancle.setText("已取消");
                        btnCancle.setClickable(false);
                        btnOk.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(BuyerCreateActivity.this, "请重试", Toast.LENGTH_LONG).show();
                    }
                }
            });
        });
    }

    @Override
    public boolean canSwipeBack() {
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AddressActivity.REQUEST_PICK_ADDRESS) {
            Address address = (Address) data.getSerializableExtra(AddressActivity.RESULT_ADDRESS);
            order.setAddress(address);
            tvAddress.setText("收货人："+address.getRecipient()+"\n手机号码："+address.getPhone()+"\n地址："+address.getRegion()+address.getSpecific());
        }
    }

    @Override
    protected int getToolBarId() {
        return R.id.toolbar;
    }

}
