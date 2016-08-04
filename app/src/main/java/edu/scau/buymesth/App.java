package edu.scau.buymesth;

import android.app.Application;

import crush.CustomActivityOnCrash;

/**
 * Created by John on 2016/8/4.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CustomActivityOnCrash.install(this);
    }
}
