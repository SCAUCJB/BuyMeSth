package edu.scau.buymesth.user.setting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import base.BaseActivity;
import butterknife.Bind;
import edu.scau.Constant;
import edu.scau.buymesth.R;

import static edu.scau.buymesth.user.setting.SettingInputFragment.TYPE_NICKNAME;
import static edu.scau.buymesth.user.setting.SettingInputFragment.TYPE_RESIDENCE;
import static edu.scau.buymesth.user.setting.SettingInputFragment.TYPE_SIGNATURE;

/**
 * Created by John on 2016/9/9.
 */

public class UserSettingActivity extends BaseActivity implements UserSettingFragment.OnItemClickedListener, SettingInputFragment.OnInputCompletedListener {
    @Bind(R.id.btn_submit)
    Button mSubmitBtn;
    @Bind(R.id.tv_title)
    TextView mTitleTv;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_setting;
    }


    @Override
    protected int getToolBarId() {
        return R.id.tb_setting;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            return;
        }
        UserSettingFragment userSettingFragment = new UserSettingFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.container, userSettingFragment).commit();
    }

    @Override
    public void initView() {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean canSwipeBack() {
        return true;
    }

    @Override
    public void onSignatureClicked() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        SettingInputFragment settingInputFragment = new SettingInputFragment();
        Bundle args=new Bundle();
        args.putByte("type", TYPE_SIGNATURE);
        settingInputFragment.setArguments(args);
        transaction.replace(R.id.container, settingInputFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        mSubmitBtn.setVisibility(View.VISIBLE);
        mTitleTv.setText("请输入个性签名");
    }

    @Override
    public void onNicknameClicked() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        SettingInputFragment settingInputFragment = new SettingInputFragment();
        Bundle args=new Bundle();
        args.putByte("type", TYPE_NICKNAME);
        settingInputFragment.setArguments(args);
        transaction.replace(R.id.container, settingInputFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        mSubmitBtn.setVisibility(View.VISIBLE);
        mTitleTv.setText("请输入昵称");
    }

    @Override
    public void onResidenceClicked() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        SettingInputFragment settingInputFragment = new SettingInputFragment();
        Bundle args=new Bundle();
        args.putByte("type", TYPE_RESIDENCE);
        settingInputFragment.setArguments(args);
        transaction.replace(R.id.container, settingInputFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        mSubmitBtn.setVisibility(View.VISIBLE);
        mTitleTv.setText("请输入常住地");
    }

    @Override
    public int getStatusColorResources() {
        return R.color.colorPrimaryDark;
    }

    @Override
    public void onInputCompleted(String input,byte type) {
        UserSettingFragment userSettingFragment = new UserSettingFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().popBackStack();
        ft.replace(R.id.container, userSettingFragment).commit();
        SharedPreferences sp = mContext.getSharedPreferences(Constant.SHARE_PREFERENCE_CACHE_FREASHNESS, MODE_PRIVATE);
        sp.edit().putBoolean("UserInfoNew",true).apply();
    }
    public void resetToolbar(){
        mSubmitBtn.setVisibility(View.INVISIBLE);
        mTitleTv.setText("个人设置");
    }
}
