package edu.scau.buymesth.notice.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import base.BaseActivity;
import base.util.SpaceItemDecoration;
import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Order;
import edu.scau.buymesth.data.bean.OrderMoment;

/**
 * Created by Jammy on 2016/9/30.
 */
public class SellerAcceptActivity extends BaseActivity {
    @Bind(R.id.btn_camera)
    FloatingActionButton btnCamera;
    OrderMomentAdapter orderMomentAdapter;
    Order order;
    @Bind(R.id.image_list)
    RecyclerView imageList;
    @Bind(R.id.btn_go)
    Button btnGo;
    @Bind(R.id.tv_address)
    TextView tvAddress;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_seller_accept;
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

        tvAddress.setText("买家地址是：收货人："+order.getAddress().getRecipient()+"\n手机号码："+order.getAddress().getPhone()+"\n地址："+order.getAddress().getRegion()+order.getAddress().getSpecific());

        btnCamera.setOnClickListener(v -> {
            Intent intent = new Intent(this, PicPublishActivity.class);
            intent.putExtra("order", order);
            startActivity(intent);
        });
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
                    Toast.makeText(SellerAcceptActivity.this, "网络有问题，请重试", Toast.LENGTH_LONG).show();
                    closeLoadingDialog();
                }
            }
        });
        btnGo.setOnClickListener(v -> {
            order.setStatus(Order.STATUS_DELIVERING);
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
