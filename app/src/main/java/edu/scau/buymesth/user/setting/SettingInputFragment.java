package edu.scau.buymesth.user.setting;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import base.BaseFragment;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import edu.scau.Constant;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.User;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by John on 2016/9/11.
 */

public class SettingInputFragment extends BaseFragment {
    public static final byte TYPE_NICKNAME=0;
    public static final byte TYPE_SIGNATURE=1;
    private   byte type;
    EditText mInputEt;
    OnInputCompletedListener mCallback;

    interface OnInputCompletedListener{
        void onInputCompleted(String input,byte type);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback=(OnInputCompletedListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnItemClickedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_setting_input,container,false);
        mInputEt= (EditText) view.findViewById(R.id.et_input);
        type=getArguments().getByte("type", (byte) 127);
        ((UserSettingActivity)getActivity()).mSubmitBtn.setOnClickListener(v-> {
            if(type==(byte)127) return;
            submitToBmob(mInputEt.getText().toString(),type);
            SharedPreferences settings = getActivity().getSharedPreferences(Constant.SHARE_PREFERENCE_USER_INFO, MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            if(type==TYPE_NICKNAME)
                editor.putString(Constant.KEY_NICKNAME, mInputEt.getText().toString());
            else if(type==TYPE_SIGNATURE)
            editor.putString(Constant.KEY_SIGNATURE, mInputEt.getText().toString());
            editor.apply();
            mCallback.onInputCompleted(mInputEt.getText().toString(),type);
        });
        return view;
    }

    private void submitToBmob(String input, byte type) {
        User user = new User();

        if(type==TYPE_NICKNAME)
            user.setNickname(input);
        else if(type==TYPE_SIGNATURE)
            user.setSignature(input);
        BmobUser bmobUser = BmobUser.getCurrentUser( );
        user.update(bmobUser.getObjectId(),new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    ((UserSettingActivity)getActivity()).toast("修改成功");
                }else{
                }
            }
        });
    }
}
