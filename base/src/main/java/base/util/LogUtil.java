package base.util;

import android.util.Log;

/**
 * Created by John on 2016/8/1.
 */
public class LogUtil {

    public static boolean isEnable=false;

    public static void d(String tag, String data) {
        if (isEnable) {
            Log.d(tag, data);
        }


    }
}
