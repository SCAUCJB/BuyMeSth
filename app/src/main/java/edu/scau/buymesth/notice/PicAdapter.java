package edu.scau.buymesth.notice;

import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import adpater.BaseQuickAdapter;
import adpater.BaseViewHolder;
import edu.scau.buymesth.R;
import edu.scau.buymesth.notice.detail.PicPublishActivity;

/**
 * Created by Jammy on 2016/10/1.
 */
public class PicAdapter extends BaseQuickAdapter<String> {
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

    public PicAdapter(List<String> data) {
        super(R.layout.item_picture, data);
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
                btn.setOnClickListener(v -> {
                    if(((PicPublishActivity) mContext).mCompressing){
                        Toast.makeText(mContext,"正在压缩",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ((PicPublishActivity) mContext).mUrlList.remove(helper.getAdapterPosition());
                    remove(helper.getAdapterPosition());
                });
            }else {
                helper.getView(R.id.btn_cancle).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public long getItemId(int position) {
        if(getItemCount()==position)
            return 0;
        return 1;
    }

}
