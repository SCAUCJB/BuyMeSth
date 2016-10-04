package edu.scau.buymesth.notice.detail;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;

import java.util.ArrayList;

import base.BaseActivity;
import base.util.SpaceItemDecoration;
import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Order;

/**
 * Created by Jammy on 2016/10/3.
 */
public class BuyerDeliverActivity extends BaseActivity {

    @Bind(R.id.image_list)
    RecyclerView imageList;
    @Bind(R.id.btn_get)
    Button btnGet;
    OrderMomentAdapter orderMomentAdapter;
    Order order;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_buyer_deliver;
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

        ////TODO:判断状态来改变按钮的样式
        btnGet.setOnClickListener(v -> {
            Order order = (Order) getIntent().getSerializableExtra("order");
            order.setStatus(Order.STATUS_FINISH);
            order.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        Log.i("bmob", "更新成功");
                    } else {
                        Log.i("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
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
    protected int getToolBarId() {
        return R.id.toolbar;
    }

}
