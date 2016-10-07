package edu.scau.buymesth.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import adpater.BaseQuickAdapter;
import adpater.BaseViewHolder;
import base.util.GlideCircleTransform;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Evaluate;
import gallery.PhotoActivity;
import ui.layout.NineGridLayout;

/**
 * Created by John on 2016/10/6.
 */

public class EvaluateListAdapter extends BaseQuickAdapter<Evaluate> {
    public EvaluateListAdapter() {
        super(R.layout.item_evaluate_list, null);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return super.onCreateViewHolder(parent, viewType);

    }

    @Override
    protected void convert(BaseViewHolder helper, Evaluate item) {
        Glide.with(mContext).load(item.getBuyer().getAvatar()).crossFade().placeholder(R.mipmap.def_head).transform(new GlideCircleTransform(mContext)).into((ImageView) helper.getView(R.id.iv_avatar));
        helper.setText(R.id.tv_name,item.getBuyer().getNickname())
                .setText(R.id.tv_content,item.getContent())
                .setText(R.id.tv_score,String.valueOf(item.getScore()))
                .setText(R.id.tv_date,item.getUpdatedAt());
        if(TextUtils.isEmpty(item.getReply())){
            helper.setVisible(R.id.tv_reply,false);
        }else{
            helper.setVisible(R.id.tv_reply,true);
            helper.setText(R.id.tv_reply,item.getReply());
        }

        NineGridLayout nineGridLayout = helper.getView(R.id.nine_grid_layout);
        nineGridLayout.setUrlList(item.getUrlList());
        ((NineGridLayout) helper.getView(R.id.nine_grid_layout)).setOnItemClickListener((view, position, urls, itemType) ->
                PhotoActivity.navigate((Activity) mContext, (NineGridLayout) helper.getView(R.id.nine_grid_layout), item.getUrlList(), position));
    }
}
