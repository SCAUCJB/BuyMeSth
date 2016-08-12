package edu.scau.buymesth.ui;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import base.BaseActivity;
import butterknife.Bind;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.User;
import edu.scau.buymesth.main.TabActivity;

/**
 * Created by IamRabbit on 2016/8/4.
 */
public class RegisterActivity extends BaseActivity{
    @Bind(R.id.editText_account_reg)
    EditText inputAccount;
    @Bind(R.id.editText_nickname_reg)
    EditText inputNickname;
    @Bind(R.id.editText_password_reg)
    EditText inputPassword;
    @Bind(R.id.editText_password_confirm_reg)
    EditText inputPasswordConfirm;
    @Bind(R.id.radiogroup_gender)
    RadioGroup radioGroupGender;
    @Bind(R.id.button_register_reg)
    Button buttonRegister;
    @Bind(R.id.button_cancel_reg)
    Button buttonCancel;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    public void initView() {
        String account = getIntent().getStringExtra("username");
        if(account!=null)
            inputAccount.setText(account);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInput()){
                    String username = inputAccount.getText().toString();
                    String password = inputPassword.getText().toString();
                    User user = new User();
                    user.setUsername(username);
                    user.setPassword(password);
                    user.setNickname(inputNickname.getText().toString());
                    user.signUp(new SaveListener<User>(){

                        @Override
                        public void done(User user, BmobException e) {
                            if(user!=null){
                                //注册成功
                                toast(getString(R.string.action_register_succeed));
                                Intent intent = new Intent(RegisterActivity.this, TabActivity.class);
                                startActivity(intent);
                            }else {
                                toast(getString(R.string.action_register_failed)+":"+e.getMessage());
                            }
                        }
                    });
                }
            }
        });

    }

    public boolean checkInput(){
        boolean pass = true;
        if(inputAccount.getText().toString().length()<=0){
            inputAccount.setError(getString(R.string.error_input_too_short));
            pass = false;
        }
        if(inputPassword.getText().toString().length()<6){
            inputPassword.setError(getString(R.string.error_input_too_short));
            pass = false;
        }
        if(!inputPasswordConfirm.getText().toString().equals(inputPassword.getText().toString())){
            inputPasswordConfirm.setError(getString(R.string.error_input_too_short));
            pass = false;
        }
        if(inputNickname.getText().toString().length()<=0){
            inputNickname.setError(getString(R.string.error_input_too_short));
            pass = false;
        }
        return pass;
    }

    @Override
    public boolean showColorStatusBar() {
        return true;
    }

    @Override
    public int getStatusColorResources() {
        return R.color.colorAccent;
    }

    @Override
    public boolean canSwipeBack() {
        return false;
    }
}
