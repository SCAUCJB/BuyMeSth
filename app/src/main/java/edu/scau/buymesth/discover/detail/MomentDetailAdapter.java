package edu.scau.buymesth.discover.detail;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import adpater.BaseMultiItemQuickAdapter;
import adpater.BaseViewHolder;
import base.util.GlideCircleTransform;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.MomentsComment;
import edu.scau.buymesth.userinfo.UserInfoActivity;
import edu.scau.buymesth.util.ColorChangeHelper;
import edu.scau.buymesth.util.DateFormatHelper;

/**
 * Created by IamRabbit on 2016/8/24.
 */
public class MomentDetailAdapter extends BaseMultiItemQuickAdapter<MomentsComment> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    private Activity mActivity;

    public MomentDetailAdapter(Activity activity,List<MomentsComment> data) {
        super(data);
        mActivity = activity;
        addItemType(0, R.layout.item_moment_comment);
    }

    @Override
    protected void convert(BaseViewHolder helper, MomentsComment item) {
        helper.setText(R.id.tv_name,item.getUser().getNickname())
                .setText(R.id.tv_content,item.getContent())
                .setText(R.id.tv_level,"LV "+item.getUser().getExp()/10)
                .setText(R.id.tv_date, DateFormatHelper.dateFormat(item.getCreatedAt()));

        helper.getView(R.id.tv_level).setBackground(ColorChangeHelper.tintDrawable(mContext.getResources().getDrawable(R.drawable.rect_black),
                ColorStateList.valueOf(ColorChangeHelper.IntToColorValue(item.getUser().getExp()))));
        Glide.with(mContext).load(item.getUser().getAvatar())
                .crossFade()
                .placeholder(R.mipmap.def_head)
                .transform(new GlideCircleTransform(mContext))
                .into((ImageView) helper.getView(R.id.iv_avatar));

        helper.getView(R.id.iv_avatar).setOnClickListener(v1 -> UserInfoActivity.navigate(mActivity, item.getUser()));

    }
}
