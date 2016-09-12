package edu.scau.buymesth.user.setting;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.User;
import ui.widget.PickerView;

/**
 * Created by John on 2016/9/12.
 */

public class GenderDialogFragment extends DialogFragment {
    private PickerView mGenderPv;
    String selectedGender;
    OnGenderPickListener mCallback;

    interface OnGenderPickListener {
        void onGenderPick(String gender);
    }

    public void setOnGenderPickListener(OnGenderPickListener callback) {
        mCallback = callback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_gender_pick, null);
        mGenderPv = (PickerView) view.findViewById(R.id.pv_gender);
        ArrayList<String> list = new ArrayList<>();
        list.add("男性");
        list.add("女性");
        list.add("转性别");
        list.add("双性人");
        list.add("两性人");
        list.add("无性别");
        list.add("顺性人");
        list.add("流性人");
        list.add("性别存疑");
        mGenderPv.setOnSelectListener(text -> selectedGender = text);
        mGenderPv.setData(list);
        builder.setView(view)
                .setPositiveButton("确定",
                        (dialog, id) -> {
                            User user = new User();
                            user.setGender(selectedGender);
                            mCallback.onGenderPick(selectedGender);
                            BmobUser bmobUser = BmobUser.getCurrentUser();
                            user.update(bmobUser.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                    } else {
                                    }
                                }
                            });

                        }).setNegativeButton("取消", null);
        return builder.create();

    }

}
