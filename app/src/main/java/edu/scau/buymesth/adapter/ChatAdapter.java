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

import adpater.BaseQuickAdapter;
import adpater.BaseViewHolder;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
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
public class ChatAdapter extends BaseQuickAdapter<Order> {

    ////TODO：1.保存订单信息   2.提交订单时传过来的json解析问题


    Activity activity;

    public void setActivity(Activity activity){
        this.activity = activity;
    }

    public ChatAdapter(int layoutResId, List<Order> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Order item) {
        BmobQuery<User> bmobQuery = new BmobQuery<User>();
        bmobQuery.getObject(item.getSeller().getObjectId(), new QueryListener<User>() {
            @Override
            public void done(User object, BmobException e) {
                if (e == null) {
                    Glide.with(mContext).load(object.getAvatar()).centerCrop().into((ImageView) helper.getView(R.id.chat_icon));
                    helper.setText(R.id.tv_chat_name, object.getNickname());
                } else {
                    //TODO：若请求失败？
                }
            }
        });
        BmobQuery<Request> requestBmobQuery = new BmobQuery<>();
        requestBmobQuery.getObject(item.getRequest().getObjectId(), new QueryListener<Request>() {
            @Override
            public void done(Request request, BmobException e) {
                if (e == null) {
                    if (request.getUrls() != null){
                        Glide.with(mContext).load(request.getUrls().get(0)).centerCrop().into((ImageView) helper.getView(R.id.iv_main_pic));
                        helper.getView(R.id.rl_main).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ////TODO：跳转至订单界面,这样写会内存泄漏
                                RequestDetailActivity.navigate(activity,request);
                            }
                        });
                    }
                    else helper.getView(R.id.iv_main_pic).setVisibility(View.GONE);
                } else {
                    //TODO：若请求失败？
                }
            }
        });
        helper.setText(R.id.thing_price, "物品价格：" + item.getPrice() + item.getPriceType());
        helper.setText(R.id.tv_tip, "索要小费：" + item.getTip() + item.getTipType());
        helper.setText(R.id.tv_chat_time, "用户于：" + item.getUpdatedAt() + "接收了你的订单");

        for (String tag : item.getTags()) {
            TextView tv = (TextView) LayoutInflater.from(mContext).inflate(R.layout.tv_tag, null);
            ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            marginLayoutParams.setMargins(4, 4, 4, 4);
            tv.setLayoutParams(marginLayoutParams);
            tv.setText(tag);
            tv.setClickable(true);
            ((FlowLayout) helper.getView(R.id.chat_tag)).addView(tv);
        }
        helper.getView(R.id.btn_chat_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /////TODO:确认接单的操作
            }
        });

        helper.getView(R.id.btn_chat_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ////TODO：不接单的操作
            }
        });

    }
}
