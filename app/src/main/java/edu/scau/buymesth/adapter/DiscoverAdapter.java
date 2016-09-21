package edu.scau.buymesth.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adpater.BaseMultiItemQuickAdapter;
import adpater.BaseViewHolder;
import base.util.GlideCircleTransform;
import base.util.SpaceItemDecoration;
import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.UpdateListener;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Moment;
import edu.scau.buymesth.request.requestdetail.RequestDetailActivity;
import edu.scau.buymesth.util.ColorChangeHelper;
import edu.scau.buymesth.util.DateFormatHelper;
import gallery.PhotoActivity;
import ui.layout.NineGridLayout;

/**
 * Created by IamRabbit on 2016/8/12.
 */
public class DiscoverAdapter extends BaseMultiItemQuickAdapter<Moment> {

    private Drawable mIconRedHeart,mIconGrayHeart;
    private Activity mActivity;

    private Map<Integer,Drawable> mLevelDrawableChache;

    public DiscoverAdapter(Activity activity,List<Moment> data) {
        super(data);
        addItemType(0, R.layout.item_discover_view_normal);
        addItemType(1, R.layout.item_discover_view_request);
        mLevelDrawableChache = new HashMap<>();
        mActivity = activity;
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

        setLike(helper,item.isLike(),item);

        Glide.with(mContext).load(item.getUser().getAvatar())
                .crossFade()
                .placeholder(R.mipmap.def_head)
                .transform(new GlideCircleTransform(mContext))
                .into((ImageView) helper.getView(R.id.iv_avatar));

        NineGridLayout nineGridLayout = helper.getView(R.id.nine_grid_layout);
        nineGridLayout.setUrlList(item.getImages());

        View.OnClickListener defaultOnClickListener = v -> onItemsContentClick(helper,v,item,helper.getAdapterPosition());

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
        ((NineGridLayout)helper.getView(R.id.nine_grid_layout)).setOnItemClickListener((view, position, urls, itemType) ->
                PhotoActivity.navigate(mActivity,(NineGridLayout)helper.getView(R.id.nine_grid_layout),item.getImages(),position));
    }

    private void setLike(BaseViewHolder helper, boolean like ,Moment item) {
        item.setLike(like);
        helper.setText(R.id.tv_likes,""+item.getLikes());
        if(like){
            ((ImageView)helper.getView(R.id.iv_likes))
                    .setImageDrawable(mIconRedHeart);
        }else {
            ((ImageView)helper.getView(R.id.iv_likes))
                    .setImageDrawable(mIconGrayHeart);
        }
    }

    public void onItemsContentClick(BaseViewHolder helper,View v, Object item, int adapterPosition){
        switch (v.getId()){
            case R.id.ly_delete:
                if((((Moment) item).getUser().getObjectId().equals(BmobUser.getCurrentUser().getObjectId()))){
                    new AlertDialog.Builder(mContext)
                            .setTitle(mContext.getResources().getString(R.string.text_delete))
                            .setMessage("delete ?")
                            .setPositiveButton("yes", (dialog, which) -> {
                                Moment deleteMoment = new Moment();
                                deleteMoment.setObjectId(((Moment) item).getObjectId());
                                deleteMoment.setAuthorDelete(true);
                                deleteMoment.delete(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if(e==null)
                                            remove(adapterPosition);
                                    }
                                });
                            })
                            .setNegativeButton("no",null)
                            .show();
                }
                break;
            case R.id.ly_likes:
                AsyncCustomEndpoints ace = new AsyncCustomEndpoints();
                //第一个参数是上下文对象，第二个参数是云端逻辑的方法名称，第三个参数是上传到云端逻辑的参数列表（JSONObject cloudCodeParams），第四个参数是回调类
                JSONObject params = new JSONObject();
                try {
                    params.put("liker",BmobUser.getCurrentUser().getObjectId());
                    params.put("moment",((Moment) item).getObjectId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ace.callEndpoint("like",params , new CloudCodeListener() {
                    @Override
                    public void done(Object o, BmobException e) {
                        if(o!=null){
                            if(((String)o).equals("true")){
                                if(((Moment) item).isLike())return;
                                ((Moment) item).setLikes(((Moment) item).getLikes()+1);
                                setLike(helper,true, (Moment) item);
                            }else {
                                if(!((Moment) item).isLike())return;
                                ((Moment) item).setLikes(((Moment) item).getLikes()-1);
                                setLike(helper,false, (Moment) item);
                            }
                        }
                    }
                });
                break;
            case R.id.request_view:
                RequestDetailActivity.navigate(mActivity,((Moment)item).getRequest());
                break;
            default:
                break;
        }
    }
}
