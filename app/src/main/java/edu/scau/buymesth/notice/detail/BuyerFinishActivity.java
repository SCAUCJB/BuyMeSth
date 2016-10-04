package edu.scau.buymesth.notice.detail;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import base.BaseActivity;
import base.util.SpaceItemDecoration;
import butterknife.Bind;
import butterknife.ButterKnife;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Order;

/**
 * Created by Jammy on 2016/10/3.
 */
public class BuyerFinishActivity extends BaseActivity {
    @Bind(R.id.image_list)
    RecyclerView imageList;
    OrderMomentAdapter orderMomentAdapter;
    Order order;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_buyer_finish;
    }

    @Override
    public void initView() {
        order = (Order) getIntent().getSerializableExtra("order");
        orderMomentAdapter = new OrderMomentAdapter(new ArrayList<>());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        imageList.setLayoutManager(linearLayoutManager);
        imageList.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.dp_6)));
        imageList.setNestedScrollingEnabled(false);
        imageList.setAdapter(orderMomentAdapter);
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
