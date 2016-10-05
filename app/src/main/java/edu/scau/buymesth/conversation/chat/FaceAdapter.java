package edu.scau.buymesth.conversation.chat;

import java.util.List;

import adpater.BaseQuickAdapter;
import adpater.BaseViewHolder;
import edu.scau.buymesth.R;

/**
 * Created by ！ on 2016/9/30.
 */

public class FaceAdapter extends BaseQuickAdapter<String> {

    public FaceAdapter(List<String> faces) {
        super(R.layout.item_face,faces);
    }
    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.face_text,item);
    }
}
