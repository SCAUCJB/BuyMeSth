package edu.scau.buymesth.notice;

import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import adpater.BaseMultiItemQuickAdapter;
import adpater.BaseViewHolder;
import base.util.GlideCircleTransform;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import edu.scau.Constant;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Order;

/**
 * Created by Jammy on 2016/9/6.
 */
public class NoticeAdapter extends BaseMultiItemQuickAdapter<Order> {

    public NoticeAdapter(List<Order> data) {
        super(data);
        addItemType(Constant.BUYER_STATUS_CREATE, R.layout.buyer_create);
        addItemType(Constant.SELLER_STATUS_CREATE, R.layout.seller_create);
        addItemType(Constant.BUYER_STATUS_REJECT, R.layout.buyer_reject);
        addItemType(Constant.SELLER_STATUS_REJECT, R.layout.seller_reject);
        addItemType(Constant.BUYER_STATUS_ACCEPT, R.layout.buyer_accept);
        addItemType(Constant.SELLER_STATUS_ACCEPT, R.layout.seller_accept);
        addItemType(Constant.BUYER_STATUS_DELIVERING, R.layout.buyer_delivering);
        addItemType(Constant.SELLER_STATUS_DELIVERING, R.layout.seller_delivering);
        addItemType(Constant.BUYER_STATUS_FINISH, R.layout.buyer_finish);
        addItemType(Constant.SELLER_STATUS_FINISH, R.layout.seller_finish);
        addItemType(Constant.BUYER_STATUS_SELLER_REJECT, R.layout.reject_by_seller1);
        addItemType(Constant.SELLER_STATUS_SELLER_REJECT, R.layout.reject_by_seller2);
    }

    @Override
    protected void convert(BaseViewHolder helper, Order item) {
        switch (helper.getItemViewType()) {
            case Constant.BUYER_STATUS_CREATE:
                helper.setText(R.id.tv_chat_name, item.getSeller().getNickname());
                helper.setText(R.id.tv_level, "LV" + (item.getSeller().getExp() == null ? 0 : item.getSeller().getExp()) / 10);
                Glide.with(mContext).load(item.getSeller().getAvatar()).placeholder(R.mipmap.def_head).transform(new GlideCircleTransform(mContext)).into((ImageView) helper.getView(R.id.chat_icon));
                helper.setText(R.id.tv_chat_time, "用户于" + item.getCreatedAt() + "接收了你的订单");
                helper.setText(R.id.thing_price, "物品价格:" + item.getPrice() + item.getPriceType());
                helper.setText(R.id.tv_tip, "索要小费:" + item.getTip() + item.getTipType());
                if (item.getRequest().getUrls() != null) {
                    Glide.with(mContext).load(item.getRequest().getUrls().get(0)).placeholder(R.mipmap.def_head).into((ImageView) helper.getView(R.id.iv_main_pic));
                }
                helper.getView(R.id.btn_chat_ok).setOnClickListener(v -> {
                    item.setStatus(Order.STATUS_ACCEPTED);
                    item.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Log.i("bmob", "更新成功");
                                item.setStatus(Order.STATUS_CREATED);
                            } else {
                                Log.i("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                            }
                        }
                    });
                });
                helper.getView(R.id.btn_chat_cancle).setOnClickListener(v -> {
                    item.setStatus(Order.STATUS_REJECTED);
                    item.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Log.i("bmob", "更新成功");
                                item.setStatus(Order.STATUS_CREATED);
                            } else {
                                Log.i("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                            }
                        }
                    });
                });
                break;

            case Constant.SELLER_STATUS_CREATE:
                helper.setText(R.id.tv_text, "你于" + item.getUpdatedAt() + "接收了订单,等待买家确认");
                if (item.getRequest() != null) {
                    helper.setText(R.id.request_title, item.getRequest().getTitle());
                    if (item.getRequest().getUrls() != null && item.getRequest().getUrls().size() > 0) {
                        Glide.with(mContext).load(item.getRequest().getUrls().get(0))
                                .into((ImageView) helper.getView(R.id.request_icon));
                    }
                }
                helper.setText(R.id.tv_price, "你的出价：" + item.getPrice() + item.getPriceType());
                helper.setText(R.id.tv_tip, "索要小费：" + item.getTip() + item.getTipType());
                break;

            case Constant.BUYER_STATUS_REJECT:
                helper.setText(R.id.tv_reject, "你已于" + item.getUpdatedAt() + "拒绝了订单");
                break;

            case Constant.SELLER_STATUS_REJECT:

                break;

            case Constant.BUYER_STATUS_ACCEPT:
                helper.setText(R.id.tv_accept, "你已于" + item.getUpdatedAt() + "接收了订单");
                break;

            case Constant.SELLER_STATUS_ACCEPT:

                break;

            case Constant.BUYER_STATUS_DELIVERING:

                break;

            case Constant.SELLER_STATUS_DELIVERING:

                break;

            case Constant.BUYER_STATUS_FINISH:

                break;

            case Constant.SELLER_STATUS_FINISH:

                break;
            case Constant.BUYER_STATUS_SELLER_REJECT:

                break;

            case Constant.SELLER_STATUS_SELLER_REJECT:

                break;
        }
    }

    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }

}
