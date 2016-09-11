package edu.scau.buymesth.user.setting;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import base.BaseFragment;
import edu.scau.buymesth.R;

/**
 * Created by John on 2016/9/11.
 */

public class UserSettingFragment extends BaseFragment {
    View mAvatar;
    View mNickname;
    View mGender;
    View mResidence;
    View mSignature;
    OnItemClickedListener mCallback;
    interface OnItemClickedListener{
        void onAvatarClicked();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnItemClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnItemClickedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_setting,container,false);
        mAvatar=view.findViewById(R.id.rl_avatar);
        mNickname=view.findViewById(R.id.rl_nickname);
        mGender=view.findViewById(R.id.rl_gender);
        mResidence=view.findViewById(R.id.rl_residence);
        mSignature=view.findViewById(R.id.rl_signature);
        setListener();
        return view;

    }

    private void setListener() {
        mAvatar.setOnClickListener(v->{if (mCallback!=null){mCallback.onAvatarClicked();}});
    }
}
