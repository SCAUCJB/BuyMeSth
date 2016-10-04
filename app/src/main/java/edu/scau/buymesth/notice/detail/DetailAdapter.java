package edu.scau.buymesth.notice.detail;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import adpater.BaseQuickAdapter;
import adpater.BaseViewHolder;
import edu.scau.buymesth.R;

/**
 * Created by Jammy on 2016/10/2.
 */
public class DetailAdapter extends BaseQuickAdapter<String> {

    public DetailAdapter(List<String> data) {
        super(R.layout.order_moment_pic,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        Glide.with(mContext).load(item).thumbnail(0.1f).centerCrop().into((ImageView) helper.getView(R.id.iv));
    }
}
