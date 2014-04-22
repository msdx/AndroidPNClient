package com.githang.android.apnbb;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.androidpn.client.LogUtil;

/**
 * 广播工具，发送广播。
 * Created by msdx on 14-4-15.
 */
public class BroadcastUtil {
    private static final String LOG_TAG = LogUtil.makeLogTag(BroadcastUtil.class);

    // BROADCAST FOR ANDROIDPN CONNECT STATUS

    // 连接中状态
    public static final String APN_STATUS_CONNECTING = "org.androidpn.client.ANDROIDPN_STATUS_CONNECTING";
    // 连接状态
    public static final String APN_STATUS_CONNECTED = "org.androidpn.client.ANDROIDPN_STATUS_CONNECTED";
    // 断开状态
    public static final String APN_STATUS_DISCONNECT = "org.androidpn.client.ANDROIDPN_STATUS_DISCONNECT";
    // 重连中状态
    public static final String APN_STATUS_RECONNECTING = "org.androidpn.client.ANDROIDPN_STATUS_RECONNECTING";
    // 重连成功状态
    public static final String APN_STATUS_RECONNECT_SUCCESS = "org.androidpn.client.ANDROIDPN_STATUS_RECONNECT_SUCCESS";
    // 连接失败状态
    public static final String APN_STATUS_CONNECT_FAILED = "org.androidpn.client.ANDROIDPN_STATUS_CONNECT_FAILED";
    // 已登录状态
    public static final String APN_STATUS_LOGINED = "org.androidpn.client.ANDROIDPN_STATUS_LOGINED";
    // 登录中状态
    public static final String APN_STATUS_LOGINING = "org.androdipn.client.ANDROIDPN_STATUS_LOGINING";
    // 登录成功
    public static final String APN_STATUS_LOGIN_SUCCESS = "org.androidpn.client.ANDROIDPN_STATUS_LOGIN_SUCCESS";
    // 登录失败
    public static final String APN_STATUS_LOGIN_FAIL = "org.androidpn.client.ANDROIDPN_STATUS_LOGIN_FAIL";

    // BROADCAST FOR ANDROIDPN ACTION

    // 连接动作
    public static final String APN_ACTION_CONNECT = "org.androidpn.client.ANDROIDPN_ACTION_CONNECT";
    // 注册动作
    public static final String APN_ACTION_REGISTER = "org.androidpn.client.ANDROIDPN_ACTION_REGISTER";
    // 登录动作
    public static final String APN_ACTION_LOGIN = "org.androidpn.client.ANDROIDPN_ACTION_LOGIN";
    // 重连动作
    public static final String APN_ACTION_RECONNECT = "org.androidpn.client.ANDROIDPN_ACTION_RECONNECT";
    // 断开动作
    public static final String APN_ACTION_DISCONNECT = "org.androidpn.client.ANDORIDPN_ACTION_DISCONNECT";
    // 发送已接收状态
    public static final String APN_ACTION_RECEIPT = "org.androidpn.client.ANDROIDPN_ACTION_RECEIPT";

    //查询连接状态
    public static final String APN_ACTION_REQUEST_STATUS = "org.androidpn.client.ANDROIDPN_ACTION_REQUEST_STATUS";

    // 广播接收已准备好的消息
    public static final String ANDROIDPN_MSG_RECEIVER_READY = "org.androidpn.client.ANDROIDPN_MSG_RECEIVER_READY";

    public static final void sendBroadcast(Context context, String action) {
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(context);
        lbm.sendBroadcast(new Intent(action));
    }

    public static final void sendBroadcast(Context context, Intent intent) {
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(context);
        lbm.sendBroadcast(intent);
    }

}
