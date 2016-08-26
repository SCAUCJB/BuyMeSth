package edu.scau.buymesth.adapter;


import android.media.Image;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import adpater.BaseQuickAdapter;
import adpater.BaseViewHolder;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import edu.scau.buymesth.R;

/**
 * Created by Jammy on 2016/8/17.
 */
public class PictureAdapter extends BaseQuickAdapter<PhotoInfo> {

    //    List<PhotoInfo> list;
    public void setList(List<PhotoInfo> list) {
//        this.list = list;
        if (list.size() != 9)
            list.add(new PhotoInfo());
        setNewData(list);
    }


    /////布局和数据
    public PictureAdapter(List<PhotoInfo> data) {
        super(R.layout.item_picture, data);
        setList(data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PhotoInfo item) {
        if (helper.getLayoutPosition()==getItemCount()-1&&getItemCount()!=9) {
            ImageView btn = helper.getView(R.id.btn_cancle);
            btn.setVisibility(View.GONE);
            Glide.with(mContext).load(R.mipmap.ic_add).centerCrop().into((ImageView) helper.getView(R.id.iv));
        } else {
            String url = item.getPhotoPath();
            Glide.with(mContext).load(url).centerCrop().into((ImageView) helper.getView(R.id.iv));
            ImageView btn = helper.getView(R.id.btn_cancle);
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO:如果满9个删除的特殊情况还没考虑
                    Log.v("点击了","删除了");
                    getData().remove(helper.getPosition());
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public long getItemId(int position) {
        if(getData().get(position).getPhotoPath()!=null)
            return 0;
        return 1;
    }
}
