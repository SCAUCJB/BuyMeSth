package edu.scau.buymesth.ui;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import base.BaseActivity;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;
import butterknife.Bind;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import edu.scau.buymesth.R;
import edu.scau.buymesth.bmob.User;
import edu.scau.buymesth.main.view.TabActivity;
import view.keke.AdvanceImageView;

/**
 * Created by IamRabbit on 2016/8/2.
 */
public class WelcomeActivity extends BaseActivity{
    private BmobUser bmobUser;
    @Bind(R.id.welcome_image)
    AdvanceImageView welcomeImage;

    private boolean initialized = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        welcomeImage.setOnImageDownloadLinstener(new AdvanceImageView.OnImageDownloadLinstener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ObjectAnimator.ofFloat(welcomeImage,"alpha",0.0f,1.0f).setDuration(500).start();
                    }
                });
            }
        });
    }

    @Override
    public boolean canSwipeBack() {
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!initialized){
            //在这里进行初始化的操作
            Bmob.initialize(this, "211614edac96ab4c9492179cf459993a");
            bmobUser = BmobUser.getCurrentUser();
            //完成初始化
            initialized = true;
            //打开新页面
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    WelcomeActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            jumpToNextActivity();
                        }
                    });
                }
            }).start();
        }
    }

    private void jumpToNextActivity() {
        if(bmobUser!=null){
            Intent intent = new Intent(WelcomeActivity.this, TabActivity.class);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean showColorStatusBar() {
        return false;
    }
}
