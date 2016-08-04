package edu.scau.buymesth.ui;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import base.BaseActivity;
import butterknife.Bind;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import edu.scau.buymesth.R;
import edu.scau.buymesth.main.view.TabActivity;

/**
 * Created by IamRabbit on 2016/8/3.
 */
public class LoginActivity extends BaseActivity{
    @Bind(R.id.editText_account)
    EditText inputAccount;
    @Bind(R.id.editText_password)
    EditText inputPassword;
    @Bind(R.id.textView_find_password)
    TextView buttonFindPassword;
    @Bind(R.id.button_login)
    Button buttonLogin;
    @Bind(R.id.button_register)
    Button buttonRegister;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initView() {
        BmobUser bmobUser = BmobUser.getCurrentUser();
        if(bmobUser!=null){
            inputAccount.setText(bmobUser.getUsername());
        }
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInput()){
                    String username = inputAccount.getText().toString();
                    String password = inputPassword.getText().toString();
                    BmobUser user = new BmobUser();
                    user.setUsername(username);
                    user.setPassword(password);
                    user.login(new SaveListener<BmobUser>(){

                        @Override
                        public void done(BmobUser bmobUser, BmobException e) {
                            if(bmobUser!=null){
                                //登陆成功
                                toast(getString(R.string.action_login_succeed));
                                Intent intent = new Intent(LoginActivity.this, TabActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                toast(getString(R.string.action_login_failed)+":"+e.getMessage());
                            }
                        }
                    });
                }
            }
        });
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                if(bmobUser==null){
                    intent.putExtra("username",inputAccount.getText().toString());
                }
                startActivity(intent);
            }
        });
    }

    public boolean checkInput(){
        boolean pass = true;
        if(inputAccount.getText().toString().length()<=0){
            inputAccount.setError(getString(R.string.error_input_too_short));
            pass = false;
        }
        if(inputPassword.getText().toString().length()<=0){
            inputPassword.setError(getString(R.string.error_input_too_short));
            pass = false;
        }
        return pass;
    }

    @Override
    public boolean canSwipeBack() {
        return false;
    }

    @Override
    public boolean showColorStatusBar(){
        return true;
    }

    @Override
    public int getStatusColorResources(){
        return R.color.colorAccent;
    }
}
