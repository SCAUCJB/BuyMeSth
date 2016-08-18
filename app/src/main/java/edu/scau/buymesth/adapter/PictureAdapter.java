package edu.scau.buymesth.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import adpater.BaseQuickAdapter;
import adpater.BaseViewHolder;
import base.util.ImageResizer;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import edu.scau.buymesth.R;

/**
 * Created by Jammy on 2016/8/17.
 */
public class PictureAdapter extends BaseQuickAdapter<PhotoInfo>{

    /////布局和数据
    public PictureAdapter(List<PhotoInfo> data ) {
        super(R.layout.picture_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PhotoInfo item) {
        String url=item.getPhotoPath();
        Glide.with(mContext).load(url).centerCrop().into((ImageView) helper.getView(R.id.iv));

    }
}
