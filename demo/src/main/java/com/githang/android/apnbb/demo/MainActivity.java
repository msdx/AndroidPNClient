package com.githang.android.apnbb.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.githang.android.apnbb.BroadcastUtil;
import com.githang.android.apnbb.demo.notify.NotifyListActivity;
import com.githang.android.apnbb.demo.notify.NotifySettingsActivity;

import org.androidpn.client.LogUtil;
import org.androidpn.client.ServiceManager;


public class MainActivity extends ActionBarActivity {
    private static final String LOG_TAG = LogUtil.makeLogTag(MainActivity.class);
    private TextView topTextView;
    private AndroidpnStatusReceiver statusReceiver;
    private Button login;

    private LocalBroadcastManager lbm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        topTextView = (TextView)findViewById(R.id.top_textview);
        login = (Button)findViewById(R.id.bt_login);
        final ServiceManager serviceManager = new ServiceManager(this);
        serviceManager.setNotificationIcon(R.drawable.ic_launcher);
        serviceManager.startService();

        lbm = LocalBroadcastManager.getInstance(this);
        Log.d(LOG_TAG, "localbroadcast......" + lbm.toString());
        statusReceiver = new AndroidpnStatusReceiver();
        statusReceiver.doRegister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        lbm.sendBroadcast(new Intent(BroadcastUtil.APN_ACTION_REQUEST_STATUS));
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_notify_all_msgs:
                startActivity(new Intent(this, NotifyListActivity.class));
                break;
            case R.id.bt_login:
                BroadcastUtil.sendBroadcast(this, BroadcastUtil.APN_ACTION_RECONNECT);
                v.setEnabled(false);
            default:
                break;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, NotifySettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        statusReceiver.doRegister(this);
        super.onDestroy();
    }

    public class AndroidpnStatusReceiver extends BroadcastReceiver {

        public final void doRegister(Context context) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(BroadcastUtil.APN_STATUS_DISCONNECT);
            filter.addAction(BroadcastUtil.APN_STATUS_CONNECT_FAILED);
            filter.addAction(BroadcastUtil.APN_STATUS_CONNECTED);
            filter.addAction(BroadcastUtil.APN_STATUS_RECONNECTING);
            filter.addAction(BroadcastUtil.APN_STATUS_CONNECTING);
            filter.addAction(BroadcastUtil.ANDROIDPN_MSG_RECEIVER_READY);
            lbm.registerReceiver(this, filter);
        }

        public final void doUnregester(Context context) {
            lbm.unregisterReceiver(this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if(topTextView == null) {
                return;
            }
            String action = intent.getAction();
            Log.d(LOG_TAG, action);
            if(BroadcastUtil.APN_STATUS_CONNECTED.equals(action)) {
                topTextView.setText(R.string.status_connected);
                login.setVisibility(View.GONE);
            } else if (BroadcastUtil.APN_STATUS_CONNECT_FAILED.equals(action)) {
                topTextView.setText(R.string.status_reconnect_failed);
                login.setVisibility(View.VISIBLE);
                login.setEnabled(true);
            } else if (BroadcastUtil.APN_STATUS_DISCONNECT.equals(action)) {
                topTextView.setText(R.string.status_disconnect);
                login.setVisibility(View.VISIBLE);
                login.setEnabled(true);
            } else if (BroadcastUtil.APN_STATUS_RECONNECTING.equals(action)) {
                topTextView.setText(R.string.status_reconnecting);
                login.setVisibility(View.GONE);
                login.setEnabled(false);
            } else if (BroadcastUtil.ANDROIDPN_MSG_RECEIVER_READY.equals(action)) {
                lbm.sendBroadcast(new Intent(BroadcastUtil.APN_ACTION_REQUEST_STATUS));
            } else if (BroadcastUtil.APN_STATUS_CONNECTING.equals(action)) {
                topTextView.setText(R.string.status_conning);
                login.setVisibility(View.GONE);
            }
        }
    }

}
