/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.androidpn.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.githang.android.apnbb.BroadcastUtil;
import com.githang.android.apnbb.NetworkUtil;

/** 
 * A broadcast receiver to handle the changes in network connection states.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class ConnectivityReceiver extends BroadcastReceiver {

    private static final String LOGTAG = LogUtil
            .makeLogTag(ConnectivityReceiver.class);

    private NotificationService notificationService;

    public ConnectivityReceiver(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOGTAG, "ConnectivityReceiver.onReceive()...");
        String action = intent.getAction();
        Log.d(LOGTAG, "action=" + action);

        if (NetworkUtil.isNetworkAvaible(context)) {
            Log.i(LOGTAG, "Network connected");
            notificationService.connect();
            BroadcastUtil.sendBroadcast(context, BroadcastUtil.APN_ACTION_RECONNECT);
        } else {
            Log.e(LOGTAG, "Network unavailable");
            notificationService.disconnect();
            BroadcastUtil.sendBroadcast(context, BroadcastUtil.APN_ACTION_DISCONNECT);
        }
    }

}
