package edu.scau.buymesth.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import base.BaseActivity;
import cn.bmob.v3.Bmob;
import butterknife.Bind;
import cn.bmob.v3.BmobUser;
import edu.scau.buymesth.R;
import edu.scau.buymesth.main.view.TabActivity;

/**
 * Created by IamRabbit on 2016/8/2.
 */
public class WelcomeActivity extends BaseActivity{
    private BmobUser bmobUser;
    @Bind(R.id.welcome_image)
    ImageView welcomeImage;


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
        AlphaAnimation animation=new AlphaAnimation(0f,1f);
        animation.setDuration(1000);
        Glide.with(this).load("http://cdn.duitang.com/uploads/item/201312/03/20131203154448_WUTaC.thumb.700_0.jpeg")
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .animate(animation)
                .into(welcomeImage);
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
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                WelcomeActivity.this.runOnUiThread(() -> jumpToNextActivity());
            }).start();
        }
    }

    private void jumpToNextActivity() {
        if(bmobUser!=null){
            Intent intent = new Intent(WelcomeActivity.this, TabActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean showColorStatusBar() {
        return false;
    }
}
