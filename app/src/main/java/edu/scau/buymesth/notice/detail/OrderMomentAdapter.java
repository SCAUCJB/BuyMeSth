package edu.scau.buymesth.notice.detail;

import android.app.Activity;
import android.view.View;

import java.util.List;

import adpater.BaseQuickAdapter;
import adpater.BaseViewHolder;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.OrderMoment;
import gallery.PhotoActivity;
import ui.layout.NineGridLayout;

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
        NineGridLayout nineGridLayout = helper.getView(R.id.nine_grid_layout);
        nineGridLayout.setVisibility(View.VISIBLE);
        nineGridLayout.setUrlList(item.getPicList());
        nineGridLayout.setmMaxColumn(9);
        ((NineGridLayout) helper.getView(R.id.nine_grid_layout)).setOnItemClickListener((view, position, urls, itemType) ->
                PhotoActivity.navigate((Activity) mContext, (NineGridLayout) helper.getView(R.id.nine_grid_layout), item.getPicList(), position));

//        DetailAdapter adapter = new DetailAdapter(item.getPicList());
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext,3);
//        rv.setLayoutManager(gridLayoutManager);
//        rv.addItemDecoration(new SpaceItemDecoration(mContext.getResources().getDimensionPixelSize(R.dimen.dp_6)));
//        rv.setNestedScrollingEnabled(false);
//        rv.setAdapter(adapter);

    }
}
