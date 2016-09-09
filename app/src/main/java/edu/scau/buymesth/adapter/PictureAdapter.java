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

    public static final int PICTURE_SHOW = 0;
    public static final int PICTURE_SELECT = 1;

    private int mMode = PICTURE_SELECT;

    public void setMode(int mode){
        mMode = mode;
    }

    public void setList(List<String> list) {
        if (list.size() != 9 && mMode == PICTURE_SELECT)
            list.add(null);
        setNewData(list);
    }
    @Deprecated
    public PictureAdapter(List<String> data) {
        super(R.layout.item_picture, data);
        setList(data);
    }

    public PictureAdapter(List<String> data,int mode) {
        super(R.layout.item_picture, data);
        this.mMode = mode;
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

            if(mMode == PICTURE_SELECT){
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
            }else {
                helper.getView(R.id.btn_cancle).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public long getItemId(int position) {
        if(getData().get(position)!=null)
            return 0;
        return 1;
    }

}
