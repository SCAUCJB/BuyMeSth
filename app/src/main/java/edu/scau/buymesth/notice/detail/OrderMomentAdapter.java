package edu.scau.buymesth.notice.detail;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import adpater.BaseQuickAdapter;
import adpater.BaseViewHolder;
import base.util.SpaceItemDecoration;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.OrderMoment;

/**
 * Created by Jammy on 2016/10/2.
 */
public class OrderMomentAdapter extends BaseQuickAdapter<OrderMoment>{
    public OrderMomentAdapter(List<OrderMoment> data) {
        super(R.layout.item_ordermoment,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, OrderMoment item) {
        helper.setText(R.id.time,item.getCreatedAt());
        helper.setText(R.id.text,item.getText());
        RecyclerView rv = helper.getView(R.id.rv);
        DetailAdapter adapter = new DetailAdapter(item.getPicList());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext,3);
        rv.setLayoutManager(gridLayoutManager);
        rv.addItemDecoration(new SpaceItemDecoration(mContext.getResources().getDimensionPixelSize(R.dimen.dp_6)));
        rv.setNestedScrollingEnabled(false);
        rv.setAdapter(adapter);
    }
}
