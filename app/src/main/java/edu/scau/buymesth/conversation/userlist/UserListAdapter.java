package edu.scau.buymesth.conversation.userlist;

import android.app.Activity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import adpater.BaseQuickAdapter;
import adpater.BaseViewHolder;
import base.util.GlideCircleTransform;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.User;

/**
 * Created by ！ on 2016/9/21.
 */

public class UserListAdapter extends BaseQuickAdapter<User> {

    Activity mActivity;

    public UserListAdapter(Activity activity , List<User> users) {
        super(R.layout.item_conversation,users);
        this.mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, User item) {
        helper.setText(R.id.tv_name,item.getNickname());
        Glide.with(mActivity).load(item.getAvatar()).crossFade()
                .placeholder(R.mipmap.def_head)
                .transform(new GlideCircleTransform(mContext))
                .into((ImageView) helper.getView(R.id.iv_avatar));
        helper.setText(R.id.tv_new_msg,item.getSignature());
        helper.setText(R.id.tv_date, "");
    }
}
