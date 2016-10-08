package edu.scau.buymesth.notice;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import adpater.BaseQuickAdapter;
import adpater.BaseViewHolder;
import edu.scau.Constant;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Order;
import edu.scau.buymesth.util.ColorChangeHelper;

/**
 * Created by Jammy on 2016/9/6.
 */
public class NoticeAdapter extends BaseQuickAdapter<Order> {
    private SparseArray<Drawable> mLevelDrawableCache = new SparseArray<>();

    public NoticeAdapter(List<Order> data) {
        super(R.layout.buyer_create, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Order item) {
        switch (item.getItemType()) {
            case Constant.BUYER_STATUS_CREATE:
                helper.setText(R.id.tv_detail, "已有卖家接收了你的订单，等待你的确认");
                helper.setText(R.id.tv_time, item.getUpdatedAt());
                helper.setText(R.id.tv_chat_name, item.getSeller().getNickname());
                setLevel(helper.getView(R.id.tv_level),item.getSeller().getExp());
                if (item.getSeller().getAvatar() != null) {
                    Glide.with(mContext).load(item.getSeller().getAvatar())
                            .into((ImageView) helper.getView(R.id.chat_icon));
                }
                break;

            case Constant.SELLER_STATUS_CREATE:
                helper.setText(R.id.tv_detail, "你已向买家发出订单，等待买家确认");
                helper.setText(R.id.tv_time, item.getUpdatedAt());
                helper.setText(R.id.tv_chat_name, item.getBuyer().getNickname());
                setLevel(helper.getView(R.id.tv_level),item.getBuyer().getExp());
                if (item.getBuyer().getAvatar() != null) {
                    Glide.with(mContext).load(item.getBuyer().getAvatar())
                            .into((ImageView) helper.getView(R.id.chat_icon));
                }
                break;

            case Constant.BUYER_STATUS_REJECT:
                helper.setText(R.id.tv_detail, "你已拒绝了卖家提出的订单");
                helper.setText(R.id.tv_time, item.getUpdatedAt());
                helper.setText(R.id.tv_chat_name, item.getSeller().getNickname());
                setLevel(helper.getView(R.id.tv_level),item.getSeller().getExp());
                if (item.getSeller().getAvatar() != null) {
                    Glide.with(mContext).load(item.getSeller().getAvatar())
                            .into((ImageView) helper.getView(R.id.chat_icon));
                }
                break;

            case Constant.SELLER_STATUS_REJECT:
                helper.setText(R.id.tv_detail, "你的订单已被买家拒绝");
                helper.setText(R.id.tv_time, item.getUpdatedAt());
                helper.setText(R.id.tv_chat_name, item.getBuyer().getNickname());
                setLevel(helper.getView(R.id.tv_level),item.getBuyer().getExp());
                if (item.getBuyer().getAvatar() != null) {
                    Glide.with(mContext).load(item.getBuyer().getAvatar())
                            .into((ImageView) helper.getView(R.id.chat_icon));
                }
                break;

            case Constant.BUYER_STATUS_ACCEPT:
                helper.setText(R.id.tv_detail, "你已接收了卖家的要价，等待卖家发货");
                helper.setText(R.id.tv_time, item.getUpdatedAt());
                helper.setText(R.id.tv_chat_name, item.getSeller().getNickname());
                setLevel(helper.getView(R.id.tv_level),item.getSeller().getExp());
                if (item.getSeller().getAvatar() != null) {
                    Glide.with(mContext).load(item.getSeller().getAvatar())
                            .into((ImageView) helper.getView(R.id.chat_icon));
                }
                break;

            case Constant.SELLER_STATUS_ACCEPT:
                helper.setText(R.id.tv_detail, "你的订单已被接收，可以开始发货了");
                helper.setText(R.id.tv_time, item.getUpdatedAt());
                helper.setText(R.id.tv_chat_name, item.getBuyer().getNickname());
                setLevel(helper.getView(R.id.tv_level),item.getBuyer().getExp());
                if (item.getBuyer().getAvatar() != null) {
                    Glide.with(mContext).load(item.getBuyer().getAvatar())
                            .into((ImageView) helper.getView(R.id.chat_icon));
                }
                break;

            case Constant.BUYER_STATUS_DELIVERING:
                helper.setText(R.id.tv_detail, "卖家已发货，请注意接收和确认收货");
                helper.setText(R.id.tv_time, item.getUpdatedAt());
                helper.setText(R.id.tv_chat_name, item.getSeller().getNickname());
                setLevel(helper.getView(R.id.tv_level),item.getSeller().getExp());
                if (item.getSeller().getAvatar() != null) {
                    Glide.with(mContext).load(item.getSeller().getAvatar())
                            .into((ImageView) helper.getView(R.id.chat_icon));
                }
                break;

            case Constant.SELLER_STATUS_DELIVERING:
                helper.setText(R.id.tv_detail, "你已发货，等待买家确认");
                helper.setText(R.id.tv_time, item.getUpdatedAt());
                helper.setText(R.id.tv_chat_name, item.getBuyer().getNickname());
                setLevel(helper.getView(R.id.tv_level),item.getBuyer().getExp());
                if (item.getBuyer().getAvatar() != null) {
                    Glide.with(mContext).load(item.getBuyer().getAvatar())
                            .into((ImageView) helper.getView(R.id.chat_icon));
                }
                break;

            case Constant.BUYER_STATUS_FINISH:
                helper.setText(R.id.tv_detail, "交易已完成，请为卖家打分");
                helper.setText(R.id.tv_time, item.getUpdatedAt());
                helper.setText(R.id.tv_chat_name, item.getSeller().getNickname());
                setLevel(helper.getView(R.id.tv_level),item.getSeller().getExp());
                if (item.getSeller().getAvatar() != null) {
                    Glide.with(mContext).load(item.getSeller().getAvatar())
                            .into((ImageView) helper.getView(R.id.chat_icon));
                }
                break;

            case Constant.SELLER_STATUS_FINISH:
                helper.setText(R.id.tv_detail, "交易已完成");
                helper.setText(R.id.tv_time, item.getUpdatedAt());
                helper.setText(R.id.tv_chat_name, item.getBuyer().getNickname());
                setLevel(helper.getView(R.id.tv_level),item.getBuyer().getExp());
                if (item.getBuyer().getAvatar() != null) {
                    Glide.with(mContext).load(item.getBuyer().getAvatar())
                            .into((ImageView) helper.getView(R.id.chat_icon));
                }
                break;
            case Constant.BUYER_STATUS_SELLER_REJECT:
                helper.setText(R.id.tv_detail, "卖家已取消订单");
                helper.setText(R.id.tv_time, item.getUpdatedAt());
                helper.setText(R.id.tv_chat_name, item.getSeller().getNickname());
                setLevel(helper.getView(R.id.tv_level),item.getSeller().getExp());
                if (item.getSeller().getAvatar() != null) {
                    Glide.with(mContext).load(item.getSeller().getAvatar())
                            .into((ImageView) helper.getView(R.id.chat_icon));
                }
                break;

            case Constant.SELLER_STATUS_SELLER_REJECT:
                helper.setText(R.id.tv_detail, "你已取消订单");
                helper.setText(R.id.tv_time, item.getUpdatedAt());
                helper.setText(R.id.tv_chat_name, item.getBuyer().getNickname());
                setLevel(helper.getView(R.id.tv_level),item.getBuyer().getExp());
                if (item.getBuyer().getAvatar() != null) {
                    Glide.with(mContext).load(item.getBuyer().getAvatar())
                            .into((ImageView) helper.getView(R.id.chat_icon));
                }
                break;
        }
    }

    @Override
    public Order getItem(int position) {
        return super.getItem(position);
    }

    public void setLevel(TextView view, Integer exp) {
        Drawable levelBg = mLevelDrawableCache.get(exp / 10 * 10);
        if (levelBg == null) {
            levelBg = ColorChangeHelper.tintDrawable(mContext.getResources().getDrawable(R.drawable.rect_black),
                    ColorStateList.valueOf(ColorChangeHelper.IntToColorValue(exp / 10 * 10)));
            mLevelDrawableCache.put(exp / 10 * 10, levelBg);
        }
        view.setBackground(levelBg);
        view.setText("LV" + exp / 10);
    }


}
