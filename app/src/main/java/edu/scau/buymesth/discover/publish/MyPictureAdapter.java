package edu.scau.buymesth.discover.publish;

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
public class MyPictureAdapter extends BaseQuickAdapter<String> {

    public List<String> dataAdd;

    public void setList(List<String> list) {
        dataAdd.clear();
        dataAdd.addAll(list);
        if(dataAdd.size()<9)dataAdd.add(null);
        setNewData(dataAdd);
    }

    public MyPictureAdapter(List<String> data) {
        super(R.layout.item_picture, data);
        dataAdd = new ArrayList<>();
        setList(data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        if (item ==null) {
            ImageView btn = helper.getView(R.id.btn_cancle);
            btn.setVisibility(View.GONE);
            Glide.with(mContext).load(R.drawable.ic_add).centerCrop().into((ImageView) helper.getView(R.id.iv));
        } else {
            Glide.with(mContext).load(item).thumbnail(0.1f).centerCrop().into((ImageView) helper.getView(R.id.iv));

            ImageView btn = helper.getView(R.id.btn_cancle);
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(v -> {
                getData().remove(item);
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

}
