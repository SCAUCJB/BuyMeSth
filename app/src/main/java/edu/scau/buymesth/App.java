package edu.scau.buymesth;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;
import cn.bmob.v3.BmobUser;
import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ImageLoader;
import cn.finalteam.galleryfinal.ThemeConfig;
import crush.CustomActivityOnCrash;
import edu.scau.buymesth.publish.GlideImageLoader;
import edu.scau.buymesth.ui.WelcomeActivity;

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

        //设置主题
        //ThemeConfig.CYAN
        ThemeConfig theme = new ThemeConfig.Builder()
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
