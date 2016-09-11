package edu.scau.buymesth.user.setting;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.Button;

import base.BaseActivity;
import butterknife.Bind;
import edu.scau.buymesth.R;

/**
 * Created by John on 2016/9/9.
 */

public class UserSettingActivity extends BaseActivity implements UserSettingFragment.OnItemClickedListener ,SettingInputFragment.OnInputCompletedListener{
    @Bind(R.id.btn_submit)
    Button mSubmitBtn;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_setting;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState==null){
            UserSettingFragment userSettingFragment=new UserSettingFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
       //     ft.add(userSettingFragment,"fragment");
        }
    }

    @Override
    public void initView() {

    }

    @Override
    public boolean canSwipeBack() {
        return true;
    }

    @Override
    public void onAvatarClicked() {

    }

    @Override
    public void onInputCompleted(String input) {

    }
}
