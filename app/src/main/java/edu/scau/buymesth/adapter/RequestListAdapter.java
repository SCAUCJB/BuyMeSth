package edu.scau.buymesth.adapter;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import adpater.BaseQuickAdapter;
import adpater.BaseViewHolder;
import base.util.GlideCircleTransform;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Request;
import edu.scau.buymesth.util.ColorChangeHelper;
import edu.scau.buymesth.util.DateFormatHelper;

/**
 * Created by John on 2016/8/4.
 */

public class RequestListAdapter extends BaseQuickAdapter<Request> {

    private SparseArray<Drawable> mLevelDrawableCache=new SparseArray<>();
    public RequestListAdapter( ) {
        super(R.layout.item_home_view,null);
    }

    @Override
    protected void convert(BaseViewHolder helper, Request item) {
            helper.setText(R.id.tv_name,
                    item.getUser().getNickname())
            .setText(R.id.tv_tweet_date, DateFormatHelper.dateFormat(item.getCreatedAt()))
            .setText(R.id.tv_tweet_title,item.getTitle())
            .setText(R.id.tv_tweet_text,item.getContent())
            .setText(R.id.tv_level,"LV "+ item.getUser().getExp()/10);

        Drawable levelBg = mLevelDrawableCache.get(item.getUser().getExp()/10*10);
        if(levelBg==null){
            levelBg = ColorChangeHelper.tintDrawable(mContext.getResources().getDrawable(R.drawable.rect_black),
                    ColorStateList.valueOf(ColorChangeHelper.IntToColorValue(item.getUser().getExp()/10*10)));
            mLevelDrawableCache.put(item.getUser().getExp()/10*10,levelBg);
        }
        helper.getView(R.id.tv_level).setBackground(levelBg);

        Integer high = item.getMaxPrice();
        Integer low = item.getMinPrice();
        if (low != null) {
            helper.setText(R.id.tv_price,"￥" + low + "~￥" + high);
        } else {
            helper.setText(R.id.tv_price,"￥" + high);
        }
           Glide.with(mContext).load(item.getUser().getAvatar()).crossFade().placeholder(R.mipmap.def_head).transform(new GlideCircleTransform(mContext)).into((ImageView) helper.getView(R.id.iv_avatar_author));
        if(item!=null&&item.getUrls()!=null&&!item.getUrls().isEmpty())
        {
            helper.setVisible(R.id.iv_tweet_image,true);
            Glide.with(mContext).load(item.getUrls().get(0)).diskCacheStrategy(DiskCacheStrategy.ALL).thumbnail(0.1f)
                    .centerCrop().into((ImageView) helper.getView(R.id.iv_tweet_image));
        }
        else{
            helper.setVisible(R.id.iv_tweet_image,false);
        }
    }
}
