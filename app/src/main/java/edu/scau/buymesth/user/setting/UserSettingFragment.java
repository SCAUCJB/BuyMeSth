package edu.scau.buymesth.user.setting;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import base.BaseFragment;
import base.util.GlideCircleTransform;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;
import edu.scau.Constant;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.User;
import edu.scau.buymesth.util.CompressHelper;
import me.iwf.photopicker.PhotoPicker;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by John on 2016/9/11.
 */

public class UserSettingFragment extends BaseFragment {
    View mAvatar;
    View mNickname;
    TextView mNicknameTv;
    TextView mSignatureTv;
    TextView mResidenceTv;
    View mGender;
    View mResidence;
    View mSignature;
    OnItemClickedListener mCallback;
    private ImageView mAvatarIv;
    private TextView mGenderTv;
    private ProgressDialog mDialog;
    private SharedPreferences mUserInfoSp;
    private GenderDialogFragment dialog;

    interface OnItemClickedListener {
        void onSignatureClicked();

        void onNicknameClicked();

        void onResidenceClicked();
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
        Log.d("zhx", "onCreateView");
        ((UserSettingActivity) getActivity()).resetToolbar();
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        mAvatar = view.findViewById(R.id.rl_avatar);
        mAvatarIv = (ImageView) view.findViewById(R.id.iv_avatar);
        mNickname = view.findViewById(R.id.rl_nickname);
        mNicknameTv = (TextView) view.findViewById(R.id.tv_nickname);
        mGender = view.findViewById(R.id.rl_gender);
        mGenderTv = (TextView) view.findViewById(R.id.tv_gender);
        mResidence = view.findViewById(R.id.rl_residence);
        mResidenceTv =(TextView) view.findViewById(R.id.tv_residence);
        mSignature = view.findViewById(R.id.rl_signature);
        mSignatureTv = (TextView) view.findViewById(R.id.tv_signature);

        mUserInfoSp = getActivity().getSharedPreferences(Constant.SHARE_PREFERENCE_USER_INFO, MODE_PRIVATE);
        String gender = mUserInfoSp.getString(Constant.KEY_GENDA, "");
        String avatar = mUserInfoSp.getString(Constant.KEY_AVATAR, "");
        String nickname = mUserInfoSp.getString(Constant.KEY_NICKNAME, "");
        String signature = mUserInfoSp.getString(Constant.KEY_SIGNATURE, "");
        String residence = mUserInfoSp.getString(Constant.KEY_RESIDENCE, "");
        if (!avatar.equals("")) {
            Glide.with(getActivity()).load(avatar).transform(new GlideCircleTransform(getActivity())).into(mAvatarIv);
        }
        if (!gender.equals("")) {
            mGenderTv.setText(gender);
        }
        if (!nickname.equals("")) {
            mNicknameTv.setText(nickname);
        }
        if (!signature.equals("")) {
            mSignatureTv.setText(signature);
        }
        if(!residence.equals("")){
            mResidenceTv.setText(residence);
        }

        setListener();
        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("zhx", "onActivityResult ");
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                if (photos != null && photos.size() == 1)
                    compressAndSubmit(photos);
            }
        }
    }

    public void compressAndSubmit(List<String> photos) {
        showLoadingDialog();
        new Thread(() -> {
            CompressHelper compressHelper = new CompressHelper(getContext());
            String filepath = compressHelper.thirdCompress(new File(photos.get(0)));
            submit(filepath);
        }).start();
    }

    private void submit(String filepath) {
        BmobFile.uploadBatch(new String[]{filepath}, new UploadBatchListener() {
            @Override
            public void onSuccess(List<BmobFile> list, List<String> urls) {
                User user = new User();
                user.setAvatar(urls.get(0));
                BmobUser bmobUser = BmobUser.getCurrentUser();
                user.update(bmobUser.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            mUserInfoSp.edit().putString(Constant.KEY_AVATAR, list.get(0).getLocalFile().getAbsolutePath()).apply();
                            Glide.with(getContext()).load(list.get(0).getLocalFile()).transform(new GlideCircleTransform(getContext())).into(mAvatarIv);
                            closeLoadingDialog();
                        } else {
                            closeLoadingDialog();
                        }
                    }
                });

            }

            @Override
            public void onProgress(int i, int i1, int i2, int i3) {

            }

            @Override
            public void onError(int i, String s) {
                closeLoadingDialog();
            }
        });
    }

    public void showLoadingDialog() {

        if (mDialog == null) {
            mDialog = new ProgressDialog(getContext());
            mDialog.setCancelable(false);
            mDialog.setMessage("上传中");
        }
        mDialog.show();
    }

    public void closeLoadingDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("zhx", "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("zhx", "onDetach");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("zhx", "onDestroyView");
    }

    private void setListener() {
        mSignature.setOnClickListener(v -> {
            if (mCallback != null) {
                mCallback.onSignatureClicked();
            }
        });
        mNickname.setOnClickListener(v -> {
            if (mCallback != null) {
                mCallback.onNicknameClicked();
            }
        });
        mResidence.setOnClickListener(v -> {
            if (mCallback != null) {
                mCallback.onResidenceClicked();
            }
        });
        mGender.setOnClickListener(v -> {
            showGenderPickDialog();
        });
        mAvatar.setOnClickListener(v -> {
            PhotoPicker.builder()
                    .setPhotoCount(1)
                    .setShowGif(false)
                    .setPreviewEnabled(true)
                    .start(getContext(), UserSettingFragment.this, PhotoPicker.REQUEST_CODE);
        });
    }

    public void showGenderPickDialog() {
        if (dialog == null) {
            dialog = new GenderDialogFragment();
            dialog.setOnGenderPickListener(gender -> {
                        mGenderTv.setText(gender);
                        getActivity().getSharedPreferences(Constant.SHARE_PREFERENCE_USER_INFO, MODE_PRIVATE).edit().putString(Constant.KEY_GENDA, gender).apply();
                    }
            );
        }
        dialog.show(getFragmentManager(), "GenderPick");
    }
}
