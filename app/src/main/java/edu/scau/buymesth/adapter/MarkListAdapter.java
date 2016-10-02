package edu.scau.buymesth.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import adpater.BaseQuickAdapter;
import adpater.BaseViewHolder;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Request;

/**
 * Created by John on 2016/10/2.
 */

public class MarkListAdapter extends BaseQuickAdapter<Request> {
    public MarkListAdapter( ) {
        super(R.layout.item_mark, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, Request item) {
        helper.setText(R.id.tv_title,item.getTitle()).setText(R.id.tv_content,item.getContent());
        Integer high = item.getMaxPrice();
        Integer low = item.getMinPrice();
        if (low != null) {
            helper.setText(R.id.tv_price,"期望价格：￥" + low + "~￥" + high);
        } else {
            helper.setText(R.id.tv_price,"期望价格：￥" + high);
        }
        if(item!=null&&item.getUrls()!=null&&!item.getUrls().isEmpty())
        {
            helper.setVisible(R.id.imageView,true);
            Glide.with(mContext).load(item.getUrls().get(0)).diskCacheStrategy(DiskCacheStrategy.ALL).thumbnail(0.1f)
                    .centerCrop().into((ImageView) helper.getView(R.id.imageView));
        }else{
            helper.setVisible(R.id.imageView,false);
        }
    }
}
