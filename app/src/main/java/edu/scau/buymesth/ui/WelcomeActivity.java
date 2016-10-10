package edu.scau.buymesth.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import base.BaseActivity;
import cn.bmob.v3.BmobUser;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.User;
import edu.scau.buymesth.main.TabActivity;

/**
 * Created by IamRabbit on 2016/8/2.
 */
public class WelcomeActivity extends BaseActivity {
    private volatile User bmobUser;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_welcome;
    }


    @Override
    public void initView() {
        ImageView welcomeImage = (ImageView) findViewById(R.id.welcome_image);
        Glide.with(this).load("http://cdn.duitang.com/uploads/item/201312/03/20131203154448_WUTaC.thumb.700_0.jpeg")
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(welcomeImage);


    }
public void showAlert(){
    if(alertDialog==null)
    alertDialog = new AlertDialog.Builder(this).setTitle(R.string.help).setMessage(R.string.string_help_text)
            .setNegativeButton(R.string.quit, (dialog, which) -> {
                toast("不能读写缓存导致无法正常运行");
                finish();
            }).setPositiveButton(R.string.settings, (dialog, which) -> startAppSettings()).setCancelable(false).create();
    alertDialog.show();
}
    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                toast("我们需要访问存储来读写缓存");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        243);
            } else {
                //申请WRITE_EXTERNAL_STORAGE权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        243);
            }

        } else {
            bmobUser = BmobUser.getCurrentUser(User.class);
            jumpToNextActivity();
        }
    }

    AlertDialog alertDialog=null;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 243) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                bmobUser = BmobUser.getCurrentUser(User.class);
                jumpToNextActivity();
            } else {
                showAlert();
            }

        }
    }

    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));

        startActivity(intent);
    }

    @Override
    public boolean canSwipeBack() {
        return false;
    }


    private void jumpToNextActivity() {
        if (bmobUser != null) {
            Intent intent = new Intent(WelcomeActivity.this, TabActivity.class);
            int flag = Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP;
            intent.setFlags(flag);
            startActivity(intent);
            finish();
        } else {
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
