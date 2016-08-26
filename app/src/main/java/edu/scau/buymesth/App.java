package edu.scau.buymesth;

import android.app.Application;
import android.graphics.Color;

import com.squareup.leakcanary.LeakCanary;

import cn.bmob.v3.Bmob;
import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ThemeConfig;
import crush.CustomActivityOnCrash;
import edu.scau.buymesth.publish.GlideImageLoader;

/**
 * Created by John on 2016/8/4.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CustomActivityOnCrash.install(this);
        LeakCanary.install(this);
        //在这里进行初始化的操作
        Bmob.initialize(this, "211614edac96ab4c9492179cf459993a");
        initGallery();
    }

    private void initGallery() {

        //设置相册主题
        ThemeConfig theme = new ThemeConfig.Builder()
                .setTitleBarBgColor(Color.rgb(66, 66, 66))
                .setFabNornalColor(Color.rgb(66, 66, 66))
                .setIconCamera(R.mipmap.ic_camera_alt_white_24dp)
                .build();
        //配置功能
        FunctionConfig functionConfig = new FunctionConfig.Builder()
                .setEnableCamera(true)
                .build();
        //配置imageloader
        GlideImageLoader imageloader = new GlideImageLoader();
        CoreConfig coreConfig = new CoreConfig.Builder(this, imageloader, theme)
                .setFunctionConfig(functionConfig)
                .build();
        GalleryFinal.init(coreConfig);
    }
}
