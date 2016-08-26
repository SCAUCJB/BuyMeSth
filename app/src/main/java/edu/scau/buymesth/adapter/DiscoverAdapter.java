package edu.scau.buymesth.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import adpater.BaseMultiItemQuickAdapter;
import adpater.BaseQuickAdapter;
import adpater.BaseViewHolder;
import base.util.GlideCircleTransform;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.DataServer;
import edu.scau.buymesth.data.adapter.MomentItem;
import edu.scau.buymesth.data.bean.Moment;
import edu.scau.buymesth.data.bean.MomentsComment;
import edu.scau.buymesth.data.bean.MomentsLike;
import edu.scau.buymesth.data.bean.User;
import edu.scau.buymesth.util.ColorChangeHelper;
import edu.scau.buymesth.util.DateFormatHelper;
import ui.layout.NineGridLayout;

/**
 * Created by IamRabbit on 2016/8/12.
 */
public class DiscoverAdapter extends BaseMultiItemQuickAdapter<Moment> {

    private OnItemsContentClickListener onItemsContentClickListener;
    private String myID;

//    public DiscoverAdapter(int dataSize) {
//        myID = BmobUser.getCurrentUser().getObjectId();
//    }

    public DiscoverAdapter(List<Moment> data) {
        super(data);
        addItemType(0, R.layout.item_discover_view_normal);
        addItemType(1, R.layout.item_discover_view_request);
        myID = BmobUser.getCurrentUser().getObjectId();
    }

    @Override
    protected void convert(BaseViewHolder helper, Moment item) {
        helper.setText(R.id.tv_name,
                item.getAuthor().getNickname())
                .setText(R.id.tv_tweet_date, DateFormatHelper.dateFormat(item.getCreatedAt()))
                .setText(R.id.tv_tweet_text,item.getContent())
                .setText(R.id.tv_level,"LV "+ item.getAuthor().getExp()/10)
                .setText(R.id.tv_likes,""+item.getLikes())
                .setText(R.id.tv_comments,""+item.getComments());

        if(item.getRequest()!=null){
            helper.setText(R.id.tv_request_title,item.getRequest().getTitle());
            if(item.getRequest().getUrls()!=null&&item.getRequest().getUrls().size()>0){
                Glide.with(mContext).load(item.getRequest().getUrls().get(0))
                        .into((ImageView) helper.getView(R.id.iv_request_cover));
            }else {
                helper.getView(R.id.iv_request_cover).setVisibility(View.INVISIBLE);
            }
        }

        helper.getView(R.id.tv_level).setBackground(
                ColorChangeHelper.tintDrawable(mContext.getResources().getDrawable(R.drawable.rect_black),
                        ColorStateList.valueOf(ColorChangeHelper.IntToColorValue(item.getAuthor().getExp())))
        );

        if(item.isLike()){
            ((ImageView)helper.getView(R.id.iv_likes))
                    .setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_favorite_red)
            );
        }


        Glide.with(mContext).load(item.getAuthor().getAvatar())
                .crossFade()
                .placeholder(R.mipmap.def_head)
                .transform(new GlideCircleTransform(mContext))
                .into((ImageView) helper.getView(R.id.iv_avatar));
        if(item.getImages()!=null&&item.getImages().size()>0)
            ((NineGridLayout)helper.getView(R.id.nine_grid_layout)).setUrlList(item.getImages());

        View.OnClickListener defaultOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemsContentClickListener.onItemsContentClick(v,item,helper.getAdapterPosition());
            }
        };
        helper.getView(R.id.ly_likes).setOnClickListener(defaultOnClickListener);
        helper.getView(R.id.ly_comments).setOnClickListener(defaultOnClickListener);
        if(item.getRequest()!=null)
        helper.getView(R.id.request_view).setOnClickListener(defaultOnClickListener);
        ((NineGridLayout)helper.getView(R.id.nine_grid_layout)).setOnImageClickListener(new NineGridLayout.OnImageClickListener() {
            @Override
            public void onClickImage(int position, String url, List<String> urlList) {
                onItemsContentClickListener.onItemsContentClick(helper.getView(R.id.nine_grid_layout),urlList,position);
            }
        });
    }

    public OnItemsContentClickListener getOnItemsContentClickListener() {
        return onItemsContentClickListener;
    }

    public void setOnItemsContentClickListener(OnItemsContentClickListener onItemsContentClickListener) {
        this.onItemsContentClickListener = onItemsContentClickListener;
    }

    public interface OnItemsContentClickListener{
        public void onItemsContentClick(View v, Object item, int adapterPosition);
    }
}
