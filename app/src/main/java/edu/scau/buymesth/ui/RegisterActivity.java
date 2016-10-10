package edu.scau.buymesth.ui;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import base.BaseActivity;
import base.util.ToastUtil;
import butterknife.Bind;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.User;
import edu.scau.buymesth.data.bean.Wallet;
import edu.scau.buymesth.main.TabActivity;
import util.HideIMEHelper;

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
        //hide the input method automatically
        HideIMEHelper.wrap(this);
        String account = getIntent().getStringExtra("username");
        if(account!=null)
            inputAccount.setText(account);
        buttonCancel.setOnClickListener(v -> finish());
        buttonRegister.setOnClickListener(v -> {
            if(checkInput()){
                String username = inputAccount.getText().toString();
                String password = inputPassword.getText().toString();
                User user = new User();
                user.setUsername(username);
                user.setEmail(username);
                user.setPassword(password);
                user.setExp(0);
                user.setNickname(inputNickname.getText().toString());
                user.signUp(new SaveListener<User>(){

                    @Override
                    public void done(User user, BmobException e) {
                        if(e==null&user!=null){
                            //注册成功
                            Wallet wallet = new Wallet();
                            wallet.setUser(user);
                            wallet.setCash(0);
                            wallet.save();
                            ToastUtil.show("注册成功，正在登陆");
                            BmobUser.loginByAccount(inputAccount.getText().toString(), inputPassword.getText().toString(), new LogInListener<User>() {
                                @Override
                                public void done(User user, BmobException e) {
                                    if(e==null) {
                                        ToastUtil.show("登陆成功");
                                        Intent intent = new Intent(RegisterActivity.this, TabActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else {
                                        ToastUtil.show("登陆失败");
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        }else {
                            ToastUtil.show("注册失败"+e==null?"未知错误":e.getMessage());
                        }
                    }
                });
            }
        });

    }

    public boolean isEmail(String email){
        if (null==email || "".equals(email)) return false;
//        Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        Pattern p =  Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
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
        if(!isEmail(inputAccount.getText().toString())){
            inputAccount.setError("不是符合规范的Email");
            return false;
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
