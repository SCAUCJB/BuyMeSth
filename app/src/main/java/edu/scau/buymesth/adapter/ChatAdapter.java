package edu.scau.buymesth.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import adpater.BaseMultiItemQuickAdapter;
import adpater.BaseQuickAdapter;
import adpater.BaseViewHolder;
import base.util.GlideCircleTransform;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import edu.scau.Constant;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Order;
import edu.scau.buymesth.data.bean.Request;
import edu.scau.buymesth.data.bean.User;
import edu.scau.buymesth.main.TabActivity;
import edu.scau.buymesth.publish.FlowLayout;
import edu.scau.buymesth.request.requestdetail.RequestDetailActivity;

import static edu.scau.Constant.EXTRA_REQUEST;

/**
 * Created by Jammy on 2016/9/6.
 */
public class ChatAdapter extends BaseMultiItemQuickAdapter<Order> {

    public ChatAdapter(List<Order> data) {
        super(data);
        addItemType(Constant.BUYER_STATUS_CREATE,R.layout.buyer_create);
        addItemType(Constant.SELLER_STATUS_CREATE,R.layout.seller_create);
    }

    @Override
    protected void convert(BaseViewHolder helper, Order item) {
        switch (helper.getItemViewType()){
            case Constant.BUYER_STATUS_CREATE:
                helper.setText(R.id.tv_chat_name,item.getSeller().getNickname());
                helper.setText(R.id.tv_level,String.valueOf(item.getSeller().getExp()));
                Glide.with(mContext).load(item.getSeller().getAvatar()).placeholder(R.mipmap.def_head).transform(new GlideCircleTransform(mContext)).into((ImageView) helper.getView(R.id.chat_icon));
                helper.setText(R.id.tv_chat_time,"用户于"+item.getCreatedAt()+"接收了你的订单");
                helper.setText(R.id.thing_price,"物品价格:"+item.getPrice()+item.getPriceType());
                helper.setText(R.id.tv_tip,"索要小费:"+item.getTip()+item.getTipType());
                if(item.getRequest().getUrls()!=null){
                    Glide.with(mContext).load(item.getRequest().getUrls().get(0)).placeholder(R.mipmap.def_head).into((ImageView) helper.getView(R.id.iv_main_pic));
                }
                break;

            case Constant.SELLER_STATUS_CREATE:
                helper.setText(R.id.tv_text,"你于"+item.getUpdatedAt()+"接收了订单,等待买家确认");
                if (item.getRequest() != null) {
                    helper.setText(R.id.request_title, item.getRequest().getTitle());
                    if (item.getRequest().getUrls() != null && item.getRequest().getUrls().size() > 0) {
                        Glide.with(mContext).load(item.getRequest().getUrls().get(0))
                                .into((ImageView) helper.getView(R.id.request_icon));
                    }
                }
                helper.setText(R.id.tv_price,"你的出价："+item.getPrice()+item.getPriceType());
                helper.setText(R.id.tv_tip,"索要小费："+item.getTip()+item.getTipType());
                break;
        }
    }

    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }
}
