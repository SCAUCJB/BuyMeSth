package edu.scau.buymesth.adapter;

import android.util.Log;
import android.view.View;
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

    public void setList(List<PhotoInfo> list) {
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
        if (item.getPhotoPath()==null) {
            //TODO:8个的BUG在这里
            ImageView btn = helper.getView(R.id.btn_cancle);
            btn.setVisibility(View.GONE);
            item.setPhotoPath(null);
            Glide.with(mContext).load(R.mipmap.ic_add).centerCrop().into((ImageView) helper.getView(R.id.iv));
        } else {
            String url = item.getPhotoPath();
            Glide.with(mContext).load(url).centerCrop().into((ImageView) helper.getView(R.id.iv));
            ImageView btn = helper.getView(R.id.btn_cancle);
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getData().remove(item);
                    if(getData().get(getData().size()-1).getPhotoPath()!=null){
                        setList(getData());
                    }
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
