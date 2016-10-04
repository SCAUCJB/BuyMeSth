package edu.scau.buymesth;

import android.app.Application;
import android.util.Log;

import com.squareup.leakcanary.LeakCanary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import cn.bmob.newim.BmobIM;
import crush.CustomActivityOnCrash;
import edu.scau.buymesth.notice.DemoMessageHandler;

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
//        Bmob.initialize(this, "211614edac96ab4c9492179cf459993a");

       Log.d("zhx","on app create");

        //只有主进程运行的时候才需要初始化
        if (getApplicationInfo().packageName.equals(getMyProcessName())){

//            //im初始化
//            Log.d("zhx","BmobIM init");
            BmobIM.init(this);
//            //注册消息接收器
             BmobIM.registerDefaultMessageHandler(new DemoMessageHandler(this));
        }
    }




    /**
     * 获取当前运行的进程名
     * @return
     */
    public static String getMyProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
