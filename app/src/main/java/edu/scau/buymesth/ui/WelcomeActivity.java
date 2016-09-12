package edu.scau.buymesth.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import base.BaseActivity;
import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import edu.scau.Constant;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.User;
import edu.scau.buymesth.main.TabActivity;
import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by IamRabbit on 2016/8/2.
 */
public class WelcomeActivity extends BaseActivity {
    private volatile User bmobUser;
    @Bind(R.id.welcome_image)
    ImageView welcomeImage;


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
        AlphaAnimation animation = new AlphaAnimation(0f, 1f);
        animation.setDuration(500);
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

        //原本在这里进行初始化的操作
        //放到了Application里面，防止不经过这个页面就进入Activity导致context空指针

        bmobUser = BmobUser.getCurrentUser(User.class);
        if (bmobUser != null)
            queryUser();
            //完成初始化
        else {
            jumpToNextActivity();
        }

    }

    private void queryUser() {
        User user = BmobUser.getCurrentUser(User.class);
        BmobQuery<User> query = new BmobQuery<>();
        query.getObjectObservable(User.class, user.getObjectId()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).toSingle().subscribe(new SingleSubscriber<User>() {
            @Override
            public void onSuccess(User user) {
                storeToSharePreference(user);
            }

            @Override
            public void onError(Throwable throwable) {
                finish();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void jumpToNextActivity() {
        if (bmobUser != null) {
            Intent intent = new Intent(WelcomeActivity.this, TabActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void storeToSharePreference(User user) {
        SharedPreferences settings = getSharedPreferences(Constant.SHARE_PREFERENCE_USER_INFO, MODE_PRIVATE);
//让setting处于编辑状态
        SharedPreferences.Editor editor = settings.edit();
//存放数据
        editor.putString(Constant.KEY_RESIDENCE, user.getResidence());
        editor.putString(Constant.KEY_GENDA, user.getGender());
        editor.putString(Constant.KEY_AVATAR, user.getAvatar());
        editor.putString(Constant.KEY_NICKNAME, user.getNickname());
        editor.putInt(Constant.KEY_EXP, user.getExp());
        editor.putString(Constant.KEY_SIGNATURE, user.getSignature());
//完成提交
        editor.apply();

        jumpToNextActivity();
    }

    @Override
    public boolean showColorStatusBar() {
        return false;
    }
}
