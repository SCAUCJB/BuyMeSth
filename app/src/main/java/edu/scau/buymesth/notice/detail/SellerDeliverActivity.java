package edu.scau.buymesth.notice.detail;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import base.BaseActivity;
import base.util.SpaceItemDecoration;
import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Order;
import edu.scau.buymesth.data.bean.OrderMoment;

/**
 * Created by Jammy on 2016/10/3.
 */
public class SellerDeliverActivity extends BaseActivity {

    Order order;
    @Bind(R.id.image_list)
    RecyclerView imageList;
    OrderMomentAdapter orderMomentAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_seller_deliver;
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
                    Toast.makeText(SellerDeliverActivity.this, "网络有问题，请重试", Toast.LENGTH_LONG).show();
                    closeLoadingDialog();
                }
            }
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
