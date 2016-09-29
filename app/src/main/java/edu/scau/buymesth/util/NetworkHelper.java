package edu.scau.buymesth.util;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by John on 2016/9/24.
 */

public class NetworkHelper {
    public static   boolean isOpenNetwork(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connManager.getActiveNetworkInfo() != null && connManager.getActiveNetworkInfo().isAvailable();
    }
}
