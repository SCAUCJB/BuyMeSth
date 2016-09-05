package edu.scau.buymesth.adapter;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import adpater.BaseQuickAdapter;
import adpater.BaseViewHolder;
import edu.scau.buymesth.R;

/**
 * Created by Jammy on 2016/8/17.
 */
public class PictureAdapter extends BaseQuickAdapter<String> {

    public void setList(List<String> list) {
        if (list.size() != 9)
            list.add(null);
        setNewData(list);
    }

    public PictureAdapter(List<String> data) {
        super(R.layout.item_picture, data);
        setList(data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        if (item ==null) {
            ImageView btn = helper.getView(R.id.btn_cancle);
            btn.setVisibility(View.GONE);
            Glide.with(mContext).load(R.mipmap.ic_add).centerCrop().into((ImageView) helper.getView(R.id.iv));
        } else {
            Glide.with(mContext).load(item).thumbnail(0.1f).centerCrop().into((ImageView) helper.getView(R.id.iv));
            ImageView btn = helper.getView(R.id.btn_cancle);
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getData().remove(item);
                    if(getData().get(getData().size()-1)!=null){
                        setList(getData());
                    }
                    notifyDataSetChanged();
                }
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
