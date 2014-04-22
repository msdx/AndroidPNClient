package com.githang.android.apnbb;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.androidpn.client.LogUtil;

/**
 * Created by msdx on 14-4-18.
 */
public class NetworkUtil {
    private static final String LOGTAG = LogUtil.makeLogTag(NetworkUtil.class);
    public static final boolean isNetworkAvaible(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null) {
            Log.d(LOGTAG, "Network Type  = " + networkInfo.getTypeName());
            Log.d(LOGTAG, "Network State = " + networkInfo.getState());
            if (networkInfo.isConnected()) {
                return true;
            }
        }
        return false;
    }
}
