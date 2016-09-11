package edu.scau.buymesth.user.setting;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import base.BaseFragment;
import base.util.GlideCircleTransform;
import edu.scau.Constant;
import edu.scau.buymesth.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by John on 2016/9/11.
 */

public class UserSettingFragment extends BaseFragment {
    View mAvatar;
    View mNickname;
    TextView mNicknameTv;
    TextView mSignatureTv;
    View mGender;
    View mResidence;
    View mSignature;
    OnItemClickedListener mCallback;
    private ImageView mAvatarIv;
    private TextView mGenderTv;

    interface OnItemClickedListener{
        void onSignatureClicked();
        void onNicknameClicked();
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
        Log.d("zhx","onCreateView");
        ((UserSettingActivity)getActivity()).resetToolbar();
        View view=inflater.inflate(R.layout.fragment_setting,container,false);
        mAvatar=view.findViewById(R.id.rl_avatar);
        mAvatarIv= (ImageView) view.findViewById(R.id.iv_avatar);
        mNickname=view.findViewById(R.id.rl_nickname);
        mNicknameTv= (TextView) view.findViewById(R.id.tv_nickname);
        mGender=view.findViewById(R.id.rl_gender);
        mGenderTv= (TextView) view.findViewById(R.id.tv_gender);
        mResidence=view.findViewById(R.id.rl_residence);
        mSignature=view.findViewById(R.id.rl_signature);
        mSignatureTv= (TextView) view.findViewById(R.id.tv_signature);

        SharedPreferences settings = getActivity().getSharedPreferences(Constant.SHARE_PREFERENCE_USER_INFO, MODE_PRIVATE);
        String gender = settings.getString(Constant.KEY_GENDA, "");
        String avatar = settings.getString(Constant.KEY_AVATAR, "");
        String nickname = settings.getString(Constant.KEY_NICKNAME, "");
        String signature = settings.getString(Constant.KEY_SIGNATURE, "");
        if(!avatar.equals("")){
            Glide.with(getActivity()).load(avatar).transform(new GlideCircleTransform(getActivity())).into(mAvatarIv);
        }
        if (!gender.equals("")){
            mGenderTv.setText(gender);
        }
        if (!nickname.equals("")){
            mNicknameTv.setText(nickname);
        }
        if (!signature.equals("")){
            mSignatureTv.setText(signature);
        }

//        Bundle args = getArguments();
//        if(args!=null){
//            if(args.getByte("type")==SettingInputFragment.TYPE_NICKNAME)
//            mNicknameTv.setText(args.getString("input"));
//            else if((args.getByte("type")==SettingInputFragment.TYPE_SIGNATURE))
//                mSignatureTv.setText(args.getString("input"));
//        }
        setListener();
        return view;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("zhx","onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("zhx","onDetach");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("zhx","onDestroyView");
    }

    private void setListener() {
        mSignature.setOnClickListener(v->{if (mCallback!=null){mCallback.onSignatureClicked();}});
        mNickname.setOnClickListener(v->{if (mCallback!=null){mCallback.onNicknameClicked();}});
    }
}
