package edu.scau.buymesth.adapter;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import adpater.BaseQuickAdapter;
import adpater.BaseViewHolder;
import edu.scau.buymesth.R;

/**
 * Created by Jammy on 2016/10/7.
 */
public class PicAdapter extends BaseQuickAdapter<String> {
    public PicAdapter( List<String> data) {
        super(R.layout.item_picture,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.getView(R.id.btn_cancle).setVisibility(View.GONE);
        Glide.with(mContext).load(item).centerCrop().into((ImageView) helper.getView(R.id.iv));
    }
}
