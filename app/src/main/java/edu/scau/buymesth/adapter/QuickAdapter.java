package edu.scau.buymesth.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import adpater.BaseQuickAdapter;
import adpater.BaseViewHolder;
import base.util.GlideCircleTransform;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.DataServer;
import edu.scau.buymesth.data.bean.Request;

/**
 * Created by John on 2016/8/4.
 */

public class QuickAdapter extends BaseQuickAdapter<Request> {
    public QuickAdapter(int dataSize) {
        super(R.layout.item_home_view, DataServer.getRequests(dataSize));
    }

    @Override
    protected void convert(BaseViewHolder helper, Request item) {
            helper.setText(R.id.tv_name,item.getAuthor().getNickname())
            .setText(R.id.tv_tweet_date,item.getCreatedAt())
            .setText(R.id.tv_tweet_title,item.getTitle())
            .setText(R.id.tv_tweet_text,item.getContent());

           Glide.with(mContext).load(item.getAuthor().getAvatar()).crossFade().placeholder(R.mipmap.def_head).transform(new GlideCircleTransform(mContext)).into((ImageView) helper.getView(R.id.iv_avatar));
        if(!item.getUrls().isEmpty())
        {
            helper.setVisible(R.id.iv_tweet_image,true);
            Glide.with(mContext).load(item.getUrls().get(0)).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into((ImageView) helper.getView(R.id.iv_tweet_image));
        }
        else{
            helper.setVisible(R.id.iv_tweet_image,false);
        }
    }
}
