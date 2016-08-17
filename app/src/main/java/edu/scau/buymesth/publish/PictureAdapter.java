package edu.scau.buymesth.publish;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.List;

import adpater.BaseQuickAdapter;
import adpater.BaseViewHolder;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import edu.scau.buymesth.R;

/**
 * Created by Jammy on 2016/8/17.
 */
public class PictureAdapter extends BaseQuickAdapter<PhotoInfo>{

    /////布局和数据
    public PictureAdapter(int layoutResId, List<PhotoInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PhotoInfo item) {
//        helper.setImageResource(R.id.rv_iv,item.getPhotoId());
        helper.setImageBitmap(R.id.rv_iv,BitmapFactory.decodeFile(item.getPhotoPath()));
    }
}
