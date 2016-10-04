package edu.scau.buymesth.notice.detail;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import base.BaseActivity;
import base.util.SpaceItemDecoration;
import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Order;
import edu.scau.buymesth.data.bean.OrderMoment;
import edu.scau.buymesth.request.requestdetail.RequestDetailActivity;

/**
 * Created by Jammy on 2016/9/30.
 */
public class BuyerAcceptActivity extends BaseActivity {
    OrderMomentAdapter orderMomentAdapter;
    Order order;
    @Bind(R.id.request_icon)
    ImageView requestIcon;
    @Bind(R.id.request_title)
    TextView requestTitle;
    @Bind(R.id.rv)
    RecyclerView rv;
    @Bind(R.id.ll_request)
    LinearLayout llRequest;
    @Bind(R.id.tv_address)
    TextView tvAddress;

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
            RequestDetailActivity.navigate(this, order.getRequest());
        });

        orderMomentAdapter = new OrderMomentAdapter(new ArrayList<>());
        tvAddress.setText("买家地址是：收货人：" + order.getAddress().getRecipient() + "\n手机号码：" + order.getAddress().getPhone() + "\n地址：" + order.getAddress().getRegion() + order.getAddress().getSpecific());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(linearLayoutManager);
        rv.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.dp_6)));
        rv.setNestedScrollingEnabled(false);
        rv.setAdapter(orderMomentAdapter);

        BmobQuery<OrderMoment> query = new BmobQuery<>();
        query.addWhereEqualTo("order", order);
        showLoadingDialog();
        query.findObjects(new FindListener<OrderMoment>() {
            @Override
            public void done(List<OrderMoment> list, BmobException e) {
                if (e == null) {
                    orderMomentAdapter.setNewData(list);
                    closeLoadingDialog();
                } else {
                    Toast.makeText(BuyerAcceptActivity.this, "网络有问题，请重试", Toast.LENGTH_LONG).show();
                    closeLoadingDialog();
                }
            }
        });

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
