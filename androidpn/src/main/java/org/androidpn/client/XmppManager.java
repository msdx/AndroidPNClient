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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.githang.android.apnbb.BroadcastUtil;
import com.githang.android.apnbb.Constants;
import com.githang.android.apnbb.NotifierConfig;
import com.githang.android.apnbb.XmppConnectReceiver;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;

/**
 * This class is to manage the XMPP connection between client and server.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class XmppManager {

    private static final String LOGTAG = LogUtil.makeLogTag(XmppManager.class);

    public static final String XMPP_RESOURCE_NAME = "AndroidpnClient";

    private Context context;

    private NotificationService.TaskSubmitter taskSubmitter;

    private NotificationService.TaskTracker taskTracker;

    private SharedPreferences sharedPrefs;

    private XMPPConnection connection;

    private ConnectionListener connectionListener;

    private PacketListener packetListener;

    private Handler handler;

    private Handler toastHandler;

    private String appName;

    public XmppManager(NotificationService notificationService) {
        context = notificationService;
        XmppConnectReceiver.initInstance(context, this);

        appName = context.getPackageManager().getApplicationLabel(context.getApplicationInfo()).toString();
        taskSubmitter = notificationService.getTaskSubmitter();
        taskTracker = notificationService.getTaskTracker();
        sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        connectionListener = new PersistentConnectionListener(this);
        if (NotifierConfig.packetListener == null) {
            Log.i(LOGTAG, "the packetListener is " + NotifierConfig.packetListener);
            packetListener = new NotificationPacketListener(this);
        } else {
            try {
                packetListener = (PacketListener) Class.forName(NotifierConfig.packetListener).getConstructor(XmppManager.class).newInstance(this);
                Log.i(LOGTAG, "the packetListener is " + packetListener.getClass().toString());
            } catch (Exception e) {
                Log.e(LOGTAG, e.getMessage(), e);
                packetListener = new NotificationPacketListener(this);
            }
        }

        handler = new Handler();
        toastHandler = new Handler(Looper.getMainLooper());

    }

    protected void showToast(final String msg) {
        toastHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context.getApplicationContext(), appName + msg, Toast.LENGTH_LONG).show();
            }
        });

    }

    public Context getContext() {
        return context;
    }

    public void connect() {
        Log.d(LOGTAG, "connect()...");
        submitLoginTask();
    }

    public void disconnect() {
        Log.d(LOGTAG, "disconnect()...");
        showToast("断开连接");
        BroadcastUtil.sendBroadcast(context, BroadcastUtil.APN_STATUS_DISCONNECT);
        Thread.currentThread().dumpStack();
        terminatePersistentConnection();
    }
    public void startReconnectionThread() {
        Log.d(LOGTAG, "startReconnectionThread()...");
        BroadcastUtil.sendBroadcast(context, BroadcastUtil.APN_ACTION_RECONNECT);
    }
    public void terminatePersistentConnection() {
        Log.d(LOGTAG, "terminatePersistentConnection()...");
        BroadcastUtil.sendBroadcast(context, BroadcastUtil.APN_ACTION_DISCONNECT);
    }

    public XMPPConnection getConnection() {
        return connection;
    }

    public void setConnection(XMPPConnection connection) {
        this.connection = connection;
    }

    public ConnectionListener getConnectionListener() {
        return connectionListener;
    }

    public PacketListener getPacketListener() {
        return packetListener;
    }

    public Handler getHandler() {
        return handler;
    }

    public void reregisterAccount() {
        removeAccount();
        submitLoginTask();
    }

    public boolean isConnected() {
        return connection != null && connection.isConnected();
    }

    public boolean isAuthenticated() {
        return connection != null && connection.isConnected()
                && connection.isAuthenticated();
    }

    public boolean isRegistered() {
        //FIXME 在这里需要判断服务器上是否已经存在对应的UUID，以确认是否注册
        return sharedPrefs.contains(Constants.XMPP_USERNAME)
                && sharedPrefs.contains(Constants.XMPP_PASSWORD);
    }

    private void submitConnectTask() {
        Log.d(LOGTAG, "submitConnectTask()...");
        BroadcastUtil.sendBroadcast(context, BroadcastUtil.APN_ACTION_CONNECT);
    }

    private void submitRegisterTask() {
        Log.d(LOGTAG, "submitRegisterTask()...");
        submitConnectTask();
        BroadcastUtil.sendBroadcast(context, BroadcastUtil.APN_ACTION_REGISTER);
    }

    private void submitLoginTask() {
        Log.d(LOGTAG, "submitLoginTask()...");
        submitRegisterTask();
        BroadcastUtil.sendBroadcast(context, BroadcastUtil.APN_ACTION_LOGIN);
    }

    private void removeAccount() {
        Editor editor = sharedPrefs.edit();
        editor.remove(Constants.XMPP_USERNAME);
        editor.remove(Constants.XMPP_PASSWORD);
        editor.commit();
    }

}
