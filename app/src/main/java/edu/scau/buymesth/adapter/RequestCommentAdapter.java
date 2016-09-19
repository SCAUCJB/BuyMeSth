package edu.scau.buymesth.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import adpater.BaseQuickAdapter;
import adpater.BaseViewHolder;
import base.util.GlideCircleTransform;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Comment;

/**
 * Created by John on 2016/8/25.
 */

public class RequestCommentAdapter extends BaseQuickAdapter<Comment> {
    public RequestCommentAdapter( List<Comment> data) {
        super(R.layout.item_request_comment_view, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Comment item) {
        Glide.with(mContext).load(item.getAuthor().getAvatar()).placeholder(R.mipmap.def_head).transform(new GlideCircleTransform(mContext)).into((ImageView) helper.getView(R.id.iv_icon));
        helper.setText(R.id.tv_name,item.getAuthor().getNickname())
                .setText(R.id.tv_comment,item.getContent())
                .setText(R.id.tv_comment_date,item.getCreatedAt());
    }
}
