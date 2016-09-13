package edu.scau.buymesth.adapter;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adpater.BaseMultiItemQuickAdapter;
import adpater.BaseViewHolder;
import base.util.GlideCircleTransform;
import base.util.SpaceItemDecoration;
import cn.bmob.v3.BmobUser;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Moment;
import edu.scau.buymesth.util.ColorChangeHelper;
import edu.scau.buymesth.util.DateFormatHelper;
import ui.layout.NineGridLayout;

/**
 * Created by IamRabbit on 2016/8/12.
 */
public class DiscoverAdapter extends BaseMultiItemQuickAdapter<Moment> {

    private OnItemsContentClickListener onItemsContentClickListener;
    private Drawable mIconRedHeart,mIconGrayHeart;

    private Map<Integer,Drawable> mLevelDrawableChache;

    public DiscoverAdapter(List<Moment> data) {
        super(data);
        addItemType(0, R.layout.item_discover_view_normal);
        addItemType(1, R.layout.item_discover_view_request);
        mLevelDrawableChache = new HashMap<>();
    }

    @Override
    protected void convert(BaseViewHolder helper, Moment item) {
        if(mIconGrayHeart==null||mIconRedHeart==null){
            mIconRedHeart = mContext.getResources().getDrawable(R.drawable.ic_favorite_red);
            mIconGrayHeart = mContext.getResources().getDrawable(R.drawable.ic_favorite);
        }
        helper.setText(R.id.tv_name,
                item.getUser().getNickname())
                .setText(R.id.tv_tweet_date, DateFormatHelper.dateFormat(item.getCreatedAt()))
                .setText(R.id.tv_tweet_text,item.getContent())
                .setText(R.id.tv_level,"LV "+ item.getUser().getExp()/10)
                .setText(R.id.tv_likes,""+item.getLikes())
                .setText(R.id.tv_comments,""+item.getComments());

        if(item.getRequest()!=null){
            helper.setText(R.id.tv_request_title,item.getRequest().getTitle());
            if(item.getRequest().getUrls()!=null&&item.getRequest().getUrls().size()>0){
                helper.getView(R.id.iv_request_cover).setVisibility(View.VISIBLE);
                Glide.with(mContext).load(item.getRequest().getUrls().get(0))
                        .into((ImageView) helper.getView(R.id.iv_request_cover));
            }else {
                helper.getView(R.id.iv_request_cover).setVisibility(View.INVISIBLE);
            }
        }
        Drawable levelBg = mLevelDrawableChache.get(item.getUser().getExp()/10*10);
        if(levelBg==null){
            levelBg = ColorChangeHelper.tintDrawable(mContext.getResources().getDrawable(R.drawable.rect_black),
                    ColorStateList.valueOf(ColorChangeHelper.IntToColorValue(item.getUser().getExp()/10*10)));
            mLevelDrawableChache.put(item.getUser().getExp()/10*10,levelBg);
        }
        helper.getView(R.id.tv_level).setBackground(levelBg);

        if(item.isLike()){
            ((ImageView)helper.getView(R.id.iv_likes))
                    .setImageDrawable(mIconRedHeart);
        }else {
            ((ImageView)helper.getView(R.id.iv_likes))
                    .setImageDrawable(mIconGrayHeart);
        }


        Glide.with(mContext).load(item.getUser().getAvatar())
                .crossFade()
                .placeholder(R.mipmap.def_head)
                .transform(new GlideCircleTransform(mContext))
                .into((ImageView) helper.getView(R.id.iv_avatar));

        NineGridLayout nineGridLayout = helper.getView(R.id.nine_grid_layout);
        nineGridLayout.setUrlList(item.getImages());
        System.out.println("!!!!!"+"setting URLLLLLLLLLLLLLLLL");

        View.OnClickListener defaultOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemsContentClickListener.onItemsContentClick(v,item,helper.getAdapterPosition());
            }
        };
        if(item.getUser().getObjectId().equals(BmobUser.getCurrentUser().getObjectId())){
            helper.getView(R.id.ly_delete).setVisibility(View.VISIBLE);
            helper.getView(R.id.ly_delete).setOnClickListener(defaultOnClickListener);
        }else {
            helper.getView(R.id.ly_delete).setVisibility(View.INVISIBLE);
        }
        helper.getView(R.id.ly_likes).setOnClickListener(defaultOnClickListener);
        helper.getView(R.id.ly_comments).setOnClickListener(defaultOnClickListener);
        if(item.getRequest()!=null)
        helper.getView(R.id.request_view).setOnClickListener(defaultOnClickListener);
        ((NineGridLayout)helper.getView(R.id.nine_grid_layout)).setOnItemClickListener(new NineGridLayout.OnItemClickListener() {
            @Override
            public void onClick(View view, int position, List<String> urls, int itemType) {
                onItemsContentClickListener.onItemsContentClick(
                        helper.getView(R.id.nine_grid_layout),
                        urls,position);
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
