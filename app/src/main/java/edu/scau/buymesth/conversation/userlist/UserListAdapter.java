package edu.scau.buymesth.conversation.userlist;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import adpater.BaseQuickAdapter;
import adpater.BaseViewHolder;
import base.util.GlideCircleTransform;
import base.util.ToastUtil;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.v3.exception.BmobException;
import edu.scau.buymesth.R;
import edu.scau.buymesth.conversation.chat.ChatFragment;
import edu.scau.buymesth.data.bean.User;
import edu.scau.buymesth.fragment.EmptyActivity;

/**
 * Created by ！ on 2016/9/21.
 */

public class UserListAdapter extends BaseQuickAdapter<User> {

    Activity mActivity;

    public UserListAdapter(Activity activity , List<User> users) {
        super(R.layout.item_user,users);
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
        helper.getView(R.id.bt_go_chat).setOnClickListener(v -> {
            BmobIMUserInfo info = new BmobIMUserInfo(item.getObjectId(), item.getNickname(), item.getAvatar());
            BmobIM.getInstance().startPrivateConversation(info, new ConversationListener() {
                @Override
                public void done(BmobIMConversation conversation, BmobException e) {
                    if (e == null) {
                        //直接进入聊天
                        Bundle arg = new Bundle();
                        arg.putSerializable(BmobIMConversation.class.getName(),conversation);
                        EmptyActivity.navigate(mActivity, ChatFragment.class.getName(),arg);
                    } else {
                        ToastUtil.show(e.getMessage() + "(" + e.getErrorCode() + ")");
                    }
                }
            });
        });
    }
}
