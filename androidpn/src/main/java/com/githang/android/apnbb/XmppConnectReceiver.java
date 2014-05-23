package com.githang.android.apnbb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.androidpn.client.LogUtil;
import org.androidpn.client.NotificationIQ;
import org.androidpn.client.NotificationIQProvider;
import org.androidpn.client.XmppManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.provider.ProviderManager;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * XMLPP连接的控制类，控制连接、注册、登录、重连、响应连接状态及回执等。
 * Created by msdx on 14-4-17.
 */
public class XmppConnectReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = LogUtil.makeLogTag(XmppConnectReceiver.class);
    private static final Object lock = new Object();
    private static XmppConnectReceiver instance = null;
    private Context context;
    private Handler handler;
    private XmppManager xmppManager;
    private SharedPreferences sharedPrefs;
    private String xmppHost;
    private int xmppPort;
    private Runnable disconnectTask;
    private Runnable reconnectTask;
    private Runnable loginServerTask;
    private boolean isConnecting;


    private XmppConnectReceiver(Context context, XmppManager xmppManager) {
        this.context = context;
        this.xmppManager = xmppManager;
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(context);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastUtil.APN_ACTION_LOGIN);
        filter.addAction(BroadcastUtil.APN_ACTION_RECONNECT);
        filter.addAction(BroadcastUtil.APN_ACTION_REQUEST_STATUS);
        filter.addAction(BroadcastUtil.APN_ACTION_RECEIPT);
        lbm.registerReceiver(this, filter);

        BroadcastUtil.sendBroadcast(context, BroadcastUtil.ANDROIDPN_MSG_RECEIVER_READY);

        sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE);

        xmppHost = sharedPrefs.getString(Constants.XMPP_HOST, "localhost");
        xmppPort = sharedPrefs.getInt(Constants.XMPP_PORT, 5222);


        HandlerThread thread = new HandlerThread(XmppConnectReceiver.class.getSimpleName());
        thread.start();
        handler = new Handler(thread.getLooper());
        disconnectTask = new DisconnectTask();
        reconnectTask = new ReconnectTask();

        loginServerTask = new LoginServer();

    }

    public static final void initInstance(Context context, XmppManager xmppManager) {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new XmppConnectReceiver(context, xmppManager);
                }
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(LOG_TAG, action);
        if (BroadcastUtil.APN_ACTION_LOGIN.equals(action)) {
            doLogin();
        } else if (BroadcastUtil.APN_ACTION_RECONNECT.equals(action)) {
            doReconnect();
        } else if (BroadcastUtil.APN_ACTION_DISCONNECT.equals(action)) {
            doDisconnect();
        } else if (BroadcastUtil.APN_ACTION_REQUEST_STATUS.equals(action)) {
            if (xmppManager.isAuthenticated()) {
                BroadcastUtil.sendBroadcast(context, BroadcastUtil.APN_STATUS_CONNECTED);
            } else if (isConnecting) {
                BroadcastUtil.sendBroadcast(context, BroadcastUtil.APN_STATUS_CONNECTING);
            } else {
                BroadcastUtil.sendBroadcast(context, BroadcastUtil.APN_STATUS_DISCONNECT);
            }
        } else if (BroadcastUtil.APN_ACTION_RECEIPT.equals(action)) {
            doSendReceipt((IQ) intent.getSerializableExtra(Constants.INTENT_EXTRA_IQ));
        }
    }

    private void doSendReceipt(IQ iq) {
        IQ result = IQ.createResultIQ(iq);
        xmppManager.getConnection().sendPacket(result);
        Log.d(LOG_TAG, "receipt" + iq.toString());
    }

    private void doLogin() {
        if (xmppManager.isAuthenticated()) {
            return;
        }
        handler.removeCallbacks(loginServerTask);
        handler.post(loginServerTask);
    }

    private void doReconnect() {
        handler.post(reconnectTask);
    }

    private void doDisconnect() {
        handler.removeCallbacks(loginServerTask);
        handler.post(disconnectTask);
    }

    public static class DelayTime {
        private static final DelayTime delayTime = new DelayTime();
        private AtomicInteger times = new AtomicInteger(0);

        public static void resetTimes() {
            delayTime.times.set(0);
        }

        public static void increase() {
            delayTime.times.incrementAndGet();
        }

        public static int getWaitingTime() {
            int time = delayTime.times.get();
            if (time == 0) {
                return 0;
            }
            // 20秒一次
            if (time < 15) {
                return 20;
            }
            // 60秒一次
            if (time < 20) {
                return 60;
            }
            return time < 30 ? 120 : 300;
        }

    }

    /**
     * A runnable task to connect the server.
     */
    private class LoginServer implements Runnable {
        private static final int REGISTER_TIME_OUT = 60000;

        private LoginServer() {
        }

        public void run() {
            final String uuid = UUIDUtil.getID(context);
            // 密码也设成UUID，以使应用程序清除数据之后，再注册的用户username是一样的。
            final String newUsername = uuid;
            final String newPassword = uuid;
            isConnecting = true;
            boolean isSuccess = false;
            if(connect()) {
                if(register(newUsername, newPassword)) {
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putString(Constants.XMPP_USERNAME,
                            newUsername);
                    editor.putString(Constants.XMPP_PASSWORD,
                            newPassword);
                    editor.commit();
                    String username = sharedPrefs.getString(Constants.XMPP_USERNAME, "");
                    String password = sharedPrefs.getString(Constants.XMPP_PASSWORD, "");
                    if(login(username, password)) {
                        isSuccess = true;
                        BroadcastUtil.sendBroadcast(context, BroadcastUtil.APN_STATUS_LOGINED);
                    } else {
                    }
                }
            } else {
                BroadcastUtil.sendBroadcast(context, BroadcastUtil.APN_STATUS_CONNECT_FAILED);
            }
            isConnecting = false;
            if(!isSuccess) {
                BroadcastUtil.sendBroadcast(context, BroadcastUtil.APN_ACTION_RECONNECT);
            }
        }

        private boolean connect() {
            Log.i(LOG_TAG, "ConnectTask.run()...");
            if (!xmppManager.isConnected()) {
                BroadcastUtil.sendBroadcast(context, BroadcastUtil.APN_STATUS_CONNECTING);
                // Create the configuration for this new connection
                ConnectionConfiguration connConfig = new ConnectionConfiguration(
                        xmppHost, xmppPort);
                // connConfig.setSecurityMode(SecurityMode.disabled);
                connConfig.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
                connConfig.setSASLAuthenticationEnabled(false);
                connConfig.setCompressionEnabled(false);

                XMPPConnection connection = new XMPPConnection(connConfig);
                xmppManager.setConnection(connection);

                try {
                    // Connect to the server
                    connection.connect();
                    BroadcastUtil.sendBroadcast(context, BroadcastUtil.APN_STATUS_CONNECTED);
                    Log.i(LOG_TAG, "XMPP connected successfully");
                    if (NotifierConfig.iqProvider == null) {
                        ProviderManager.getInstance().addIQProvider(Constants.ELEMENT_NAME,
                                Constants.DEFAULT_NAMESPACE,
                                new NotificationIQProvider());
                    } else {
                        try {
                            ProviderManager.getInstance().addIQProvider(Constants.ELEMENT_NAME,
                                    Constants.DEFAULT_NAMESPACE,
                                    Class.forName(NotifierConfig.iqProvider).newInstance());
                        } catch (Exception e) {
                            Log.e(LOG_TAG, e.getMessage(), e);
                            ProviderManager.getInstance().addIQProvider(Constants.ELEMENT_NAME,
                                    Constants.DEFAULT_NAMESPACE,
                                    new NotificationIQProvider());
                        }
                    }
                    return true;
                } catch (XMPPException e) {
                    Log.e(LOG_TAG, "XMPP connection failed", e);
                    return false;
                }
            } else {
                Log.i(LOG_TAG, "XMPP connected already");
                return true;
            }
        }

        public boolean register(String user, String pass) {
            Log.i(LOG_TAG, "RegisterTask.run()...");

            if (xmppManager.isRegistered()) {
                Log.i(LOG_TAG, "Account registered already");
                return true;
            }

            final Registration registration = new Registration();

            PacketFilter packetFilter = new AndFilter(new PacketIDFilter(
                    registration.getPacketID()), new PacketTypeFilter(
                    IQ.class));

            PacketCollector collector = xmppManager.getConnection().createPacketCollector(packetFilter);
            registration.setType(IQ.Type.SET);
            registration.addAttribute("username", user);
            registration.addAttribute("password", pass);
            if (xmppManager.getConnection().isConnected()) {
                xmppManager.getConnection().sendPacket(registration);
                IQ result = (IQ) collector.nextResult(REGISTER_TIME_OUT);
                collector.cancel();
                if(result == null) {
                    Log.d(LOG_TAG, "The server didn't return result after 60 seconds.");
                    return false;
                } else if (result.getType() == IQ.Type.ERROR) {
                    if(result.getError().toString().contains("409")) {
                        return true;
                    } else {
                        return false;
                    }
                } else if (result.getType() == IQ.Type.RESULT) {
                    return true;
                }
                return false;
            } else {
                Log.d(LOG_TAG, "connection is not connected");
                return false;
            }
        }

        public boolean login(String user, String pass) {
            Log.i(LOG_TAG, "LoginTask.run()...");

            if (xmppManager.isAuthenticated()) {
                Log.i(LOG_TAG, "Logged in already");
                return true;
            }

            BroadcastUtil.sendBroadcast(context, BroadcastUtil.APN_STATUS_LOGINING);
            try {
                xmppManager.getConnection().login(
                        user,
                        pass, XmppManager.XMPP_RESOURCE_NAME);
                Log.d(LOG_TAG, "Loggedn in successfully");
                BroadcastUtil.sendBroadcast(context, BroadcastUtil.APN_STATUS_LOGIN_SUCCESS);

                // connection listener
                if (xmppManager.getConnectionListener() != null) {
                    xmppManager.getConnection().addConnectionListener(
                            xmppManager.getConnectionListener());
                }
                PacketFilter packetFilter = null;
                if (NotifierConfig.iq == null) {
                    // packet filter
                    packetFilter = new PacketTypeFilter(
                            NotificationIQ.class);
                } else {
                    packetFilter = new PacketTypeFilter(Class.forName(NotifierConfig.iq));
                }
                // packet listener
                PacketListener packetListener = xmppManager
                        .getPacketListener();
                xmppManager.getConnection().addPacketListener(packetListener, packetFilter);
                xmppManager.getConnection().startKeepAliveThread(xmppManager);
                return true;

            } catch (XMPPException e) {
                BroadcastUtil.sendBroadcast(context, BroadcastUtil.APN_STATUS_LOGIN_FAIL);
                Log.e(LOG_TAG, "LoginTask.run()... xmpp error");
                Log.e(LOG_TAG, "Failed to login to xmpp server. Caused by: "
                        + e.getMessage(), e);
                String INVALID_CREDENTIALS_ERROR_CODE = "401";
                String errorMessage = e.getMessage();
                if (errorMessage != null
                        && errorMessage.contains(INVALID_CREDENTIALS_ERROR_CODE)) {
                    xmppManager.reregisterAccount();
                }
                return false;
            } catch (Exception e) {
                BroadcastUtil.sendBroadcast(context, BroadcastUtil.APN_STATUS_LOGIN_FAIL);
                Log.e(LOG_TAG, "LoginTask.run()... other error");
                Log.e(LOG_TAG, "Failed to login to xmpp server. Caused by: "
                        + e.getMessage());
                return false;
            }
        }
    }

    public class DisconnectTask implements Runnable {
        public void run() {
            if (xmppManager.isConnected()) {
                Log.d(LOG_TAG, "terminatePersistentConnection()... run()");
                xmppManager.getConnection().removePacketListener(
                        xmppManager.getPacketListener());
                xmppManager.getConnection().disconnect();
            }
        }
    }

    public class ReconnectTask implements Runnable {
        public void run() {
            if (xmppManager.isAuthenticated() || !NetworkUtil.isNetworkAvaible(context)) {
                handler.removeCallbacks(reconnectTask);
                DelayTime.resetTimes();
                return;
            }
            if(isConnecting) {
                return;
            }
            Log.d(LOG_TAG, "reconnectTask...");
            BroadcastUtil.sendBroadcast(context, BroadcastUtil.APN_STATUS_RECONNECTING);

            try {
                Thread.sleep(DelayTime.getWaitingTime() * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            BroadcastUtil.sendBroadcast(context, BroadcastUtil.APN_ACTION_LOGIN);

            DelayTime.increase();
        }
    }


}
