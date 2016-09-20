package edu.scau.buymesth.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import adpater.BaseQuickAdapter;
import adpater.BaseViewHolder;
import base.util.GlideCircleTransform;
import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CloudCodeListener;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Comment;

/**
 * Created by John on 2016/8/25.
 */

public class RequestCommentAdapter extends BaseQuickAdapter<Comment> {
    public RequestCommentAdapter(List<Comment> data) {
        super(R.layout.item_request_comment_view, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Comment item) {
        Glide.with(mContext).load(item.getAuthor().getAvatar()).placeholder(R.mipmap.def_head).transform(new GlideCircleTransform(mContext)).into((ImageView) helper.getView(R.id.iv_icon));
        helper.setText(R.id.tv_name, item.getAuthor().getNickname())
                .setText(R.id.tv_comment, item.getContent())
                .setText(R.id.tv_comment_date, item.getCreatedAt());

        helper.setOnClickListener(R.id.iv_comment_like, v -> {
            AsyncCustomEndpoints ace = new AsyncCustomEndpoints();
            //第一个参数是上下文对象，第二个参数是云端逻辑的方法名称，第三个参数是上传到云端逻辑的参数列表（JSONObject cloudCodeParams），第四个参数是回调类
            JSONObject params = new JSONObject();
            try {
                params.put("liker", BmobUser.getCurrentUser().getObjectId());
                params.put("comment", item.getObjectId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ace.callEndpoint("like", params, new CloudCodeListener() {
                @Override
                public void done(Object o, BmobException e) {
                    if (e == null) {
                        if (o != null) {
                            if (((String) o).equals("true")) {
                                item.setLikes(item.getLikes() + 1);
                                helper.setImageResource(R.id.iv_comment_like, R.drawable.ic_favorite_red);
                            } else {
                                item.setLikes(item.getLikes() - 1);
                                helper.setImageResource(R.id.iv_comment_like, R.drawable.ic_favorite);
                            }
                        }
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        });
    }
}
