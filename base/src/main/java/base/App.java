package base;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import base.util.LogUtil;
import crush.CustomActivityOnCrash;

/**
 * Created by John on 2016/8/1.
 */
public class App extends Application {
    private static App mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        LogUtil.isEnable=true;
        CustomActivityOnCrash.install(this);
    }

    public static Context getAppContext() {
        return mApp;
    }

    public static Resources getAppResources() {
        return mApp.getResources();
    }

}