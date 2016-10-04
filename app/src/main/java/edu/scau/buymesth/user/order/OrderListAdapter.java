package edu.scau.buymesth.user.order;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import adpater.BaseQuickAdapter;
import adpater.BaseViewHolder;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Order;

/**
 * Created by Jammy on 2016/10/3.
 */
public class OrderListAdapter extends BaseQuickAdapter<Order>{
    public OrderListAdapter(List<Order> data) {
        super(R.layout.order_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Order item) {
        switch (item.getStatus()){
            case 0:
                helper.setText(R.id.tv_status,"当前状态：等待确认");
                break;
            case 1:
                helper.setText(R.id.tv_status,"当前状态：订单已确认");
                break;
            case 2:
                helper.setText(R.id.tv_status,"当前状态：已拒绝");
                break;
            case 3:
                helper.setText(R.id.tv_status,"当前状态：已发货");
                break;
            case 4:
                helper.setText(R.id.tv_status,"当前状态：交易完成");
                break;
        }
        helper.setText(R.id.tv_price,"商品总价 = "+item.getPriceType()+item.getPrice()+" + 小费("+item.getTip()+item.getTipType()+")");
        helper.setText(R.id.request_title,item.getRequest().getTitle());
        if(item.getRequest().getUrls()!=null&&item.getRequest().getUrls().size()>0)
        Glide.with(mContext).load(item.getRequest().getUrls().get(0)).into((ImageView) helper.getView(R.id.request_icon));
        Glide.with(mContext).load(item.getSeller().getAvatar()).into((ImageView)helper.getView(R.id.iv_avatar_author));
        helper.setText(R.id.tv_name,item.getSeller().getNickname());
        helper.setText(R.id.tv_level,"LV"+item.getSeller().getExp()/10);
    }

    @Override
    public Order getItem(int position) {
        return super.getItem(position);
    }
}
