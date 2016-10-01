package edu.scau.buymesth.adapter;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import adpater.BaseQuickAdapter;
import adpater.BaseViewHolder;
import edu.scau.buymesth.R;

/**
 * Created by ! on 2016/9/13.
 */
public class MyPictureAdapter extends BaseQuickAdapter<MyPictureAdapter.ImageItem> {

    public List<ImageItem> dataAdd;
    public List<ImageItem> oriData;
//    public List<String> tempData;
    private int mImageGroup = 0;

    public void setList(List<ImageItem> list) {
        oriData = list;
        dataAdd.clear();
        dataAdd.addAll(list);
        if(dataAdd.size()<9)dataAdd.add(null);
        setNewData(dataAdd);
    }

    public void setList(List<ImageItem> list,int group) {
        oriData = list;
        dataAdd.clear();
        dataAdd.addAll(list);
        if(dataAdd.size()<9)dataAdd.add(null);
        mImageGroup = group;
        setNewData(dataAdd);
    }



    public MyPictureAdapter(List<ImageItem> data) {
        super(R.layout.item_picture, data);
        dataAdd = new ArrayList<>();
        oriData = data;
        setList(data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ImageItem item) {
        if (item ==null) {
            ImageView btn = helper.getView(R.id.btn_cancle);
            btn.setVisibility(View.GONE);
            Glide.with(mContext).load(R.drawable.ic_add).centerCrop().into((ImageView) helper.getView(R.id.iv));
        } else {
            Glide.with(mContext).load(mImageGroup==0?item.sourceImage:item.compressedImage)
                    .thumbnail(0.1f).centerCrop().into((ImageView) helper.getView(R.id.iv));

            ImageView btn = helper.getView(R.id.btn_cancle);
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(v -> {
                getData().remove(item);
                oriData.remove(item);
                if(dataAdd.get(dataAdd.size()-1)!=null)dataAdd.add(null);
                notifyDataSetChanged();
            });
        }
    }

    @Override
    public long getItemId(int position) {
        if(getData().get(position)!=null)
            return 0;
        return 1;
    }

    public int getmImageGroup() {
        return mImageGroup;
    }

    public void setmImageGroup(int mImageGroup) {
        this.mImageGroup = mImageGroup;
    }

    public static class ImageItem{
        public String compressedImage;
        public String sourceImage;

        public ImageItem() {
            this.compressedImage = null;
            this.sourceImage = null;
        }

        public ImageItem(String sourceImage,String compressedImage) {
            this.compressedImage = compressedImage;
            this.sourceImage = sourceImage;
        }
    }

}
