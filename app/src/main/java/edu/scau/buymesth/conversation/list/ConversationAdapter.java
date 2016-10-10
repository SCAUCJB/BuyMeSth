package edu.scau.buymesth.conversation.list;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import adpater.BaseQuickAdapter;
import adpater.BaseViewHolder;
import base.util.GlideCircleTransform;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import edu.scau.buymesth.R;
import edu.scau.buymesth.userinfo.UserInfoActivity;
import edu.scau.buymesth.util.DateFormatHelper;

/**
 * Created by ！ on 2016/9/21.
 */

public class ConversationAdapter extends BaseQuickAdapter<BmobIMConversation> {

    Activity mActivity;
//    List<BmobIMConversation> mConversations;

    public ConversationAdapter(Activity activity , List<BmobIMConversation> conversations) {
        super(R.layout.item_conversation,conversations);
        this.mActivity = activity;
//        this.mConversations = conversations;
    }

    @Override
    protected void convert(BaseViewHolder helper, BmobIMConversation item) {
        helper.setText(R.id.tv_name,item.getConversationTitle());
        Glide.with(mActivity).load(item.getConversationIcon()).crossFade()
                .placeholder(R.mipmap.def_head)
                .transform(new GlideCircleTransform(mContext))
                .into((ImageView) helper.getView(R.id.iv_avatar));
        helper.getView(R.id.iv_avatar).setOnClickListener(v -> {
            //打开个人页
            helper.getView(R.id.iv_avatar).setOnClickListener(v1 -> UserInfoActivity.navigate(mActivity, item.getConversationId()));
        });
        if(item.getMessages().size()!=0){
            BmobIMMessage newMsg = item.getMessages().get(0);
            String content = "unknown message";
            try {
                JSONObject jsonMsg = new JSONObject(newMsg.getContent());
                switch (jsonMsg.getInt("type")){
                    case 0 : content = jsonMsg.getString("content"); break;
                    case 1 : content = "[图片]"; break;
                    case 2 : content = "[购物请求]"; break;
                    case 3 : content = "[文件]"; break;
                    default:break;
                }
            } catch (JSONException e) { }

            helper.setText(R.id.tv_new_msg,content);
            helper.setText(R.id.tv_date, DateFormatHelper.dateFormat(newMsg.getCreateTime()));
        }else {
            helper.setText(R.id.tv_new_msg," ");
            helper.setText(R.id.tv_date, DateFormatHelper.dateFormat(item.getUpdateTime()));
        }
        int unreadCount = (int) BmobIM.getInstance().getUnReadCount(item.getConversationId());
        if(unreadCount>0){
            helper.getView(R.id.unread_mark).setVisibility(View.VISIBLE);
            helper.setText(R.id.unread_mark,item.getUnreadCount()>99?"99+":String.valueOf(unreadCount));
        }else {
            helper.getView(R.id.unread_mark).setVisibility(View.INVISIBLE);
        }
    }
}
