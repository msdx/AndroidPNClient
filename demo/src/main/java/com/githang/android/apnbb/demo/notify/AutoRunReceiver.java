package com.githang.android.apnbb.demo.notify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.githang.android.apnbb.demo.R;

import org.androidpn.client.LogUtil;
import org.androidpn.client.ServiceManager;


public class AutoRunReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = LogUtil.makeLogTag(AutoRunReceiver.class);
    public AutoRunReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            Log.d(LOG_TAG, "action_boot_completed");
            final ServiceManager serviceManager = new ServiceManager(context);
            serviceManager.setNotificationIcon(R.drawable.ic_launcher);
            serviceManager.startService();
        }
    }
}
