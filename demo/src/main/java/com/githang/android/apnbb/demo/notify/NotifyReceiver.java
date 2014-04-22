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
package com.githang.android.apnbb.demo.notify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.githang.android.apnbb.Constants;
import org.androidpn.client.LogUtil;
import org.androidpn.client.Notifier;

import com.githang.android.apnbb.demo.db.DBIQOperator;

/** 
 * Broadcast receiver that handles push notification messages from the server.
 * This should be registered as receiver in AndroidManifest.xml. 
 * 
 * @author Geek_Solodad (msdx.android@qq.com)
 */
public final class NotifyReceiver extends BroadcastReceiver {

    private static final String LOGTAG = LogUtil
            .makeLogTag(NotifyReceiver.class);

    public static final String ACTION_SHOW_NOTIFICATION= "com.githang.android.apnbb.demo.SHOW_NOTIFICATION";

    public NotifyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOGTAG, "NotifyReceiver.onReceive()...");
        String action = intent.getAction();
        Log.d(LOGTAG, "action=" + action);

        if (ACTION_SHOW_NOTIFICATION.equals(action)) {
            Object object = intent.getSerializableExtra(Constants.INTENT_EXTRA_IQ);
            if (object != null && object instanceof  NotifyIQ) {
                NotifyIQ iq = (NotifyIQ) object;

                Notifier notifier = new Notifier(context);
                notifier.notify(iq, iq.getTitle(), iq.getMessage());
                saveToDb(context, iq);
            }
        }
    }

    private void saveToDb(Context context, NotifyIQ iq) {
        DBIQOperator operator = new DBIQOperator(context);
        operator.saveIQ(iq);
    }

}
