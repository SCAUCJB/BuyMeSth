package edu.scau.buymesth.ui;

import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import base.BaseActivity;
import base.util.ToastUtil;
import butterknife.Bind;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import edu.scau.buymesth.R;
import edu.scau.buymesth.main.TabActivity;
import util.HideIMEHelper;

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
    @Bind(R.id.progress_login)
    View progressLogin;
    private boolean mIsExit = false;
    private Handler handler = new Handler();
    private EditText et;
    private AlertDialog.Builder mDialog2;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initView() {
        //hide the input method automatically
        HideIMEHelper.wrap(this);
        BmobUser bmobUser = BmobUser.getCurrentUser();
        if(bmobUser!=null){
            inputAccount.setText(bmobUser.getUsername());
        }
        buttonLogin.setOnClickListener(v -> {
            if(checkInput()){
                String username = inputAccount.getText().toString();
                String password = inputPassword.getText().toString();
                BmobUser user = new BmobUser();
                user.setUsername(username);
                user.setPassword(password);
                progressLogin.setVisibility(View.VISIBLE);
                user.login(new SaveListener<BmobUser>(){
                    @Override
                    public void done(BmobUser bmobUser1, BmobException e) {
                        progressLogin.post(() -> progressLogin.setVisibility(View.INVISIBLE));
                        if(bmobUser1 !=null){
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
        });
        buttonRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
            if(bmobUser==null){
                intent.putExtra("username",inputAccount.getText().toString());
            }
            startActivity(intent);
        });
        buttonFindPassword.setOnClickListener(v -> {
            if (et == null) et = new EditText(LoginActivity.this);
            et.setText(inputAccount.getText().toString());
            if(mDialog2 ==null)
                mDialog2 = new AlertDialog.Builder(LoginActivity.this).setTitle("找回密码")
                    .setMessage("请输入注册时的邮箱地址")
                    .setView(et)
                    .setPositiveButton("确认", (dialog, which) -> BmobUser.requestEmailVerify(et.getText().toString(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                ToastUtil.show("请求验证邮件成功，请到邮箱中进行激活。");
                            }else{
                                ToastUtil.show("失败:" + e.getMessage());
                            }
                        }
                    })).setNegativeButton("取消",null);
            mDialog2.show();
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

//    @Override
//    public void onBackPressed() {
//        if (mIsExit) {
//            ActivityManager manager = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE); //获取应用程序管理器
//            manager.killBackgroundProcesses(getPackageName());
//        } else {
//            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
//            mIsExit = true;
//            handler.postDelayed(() -> mIsExit = false, 2000);
//        }
//    }

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
