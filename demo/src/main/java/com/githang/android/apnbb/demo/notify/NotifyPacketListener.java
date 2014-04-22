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

import android.content.Intent;
import android.util.Log;

import com.githang.android.apnbb.BroadcastUtil;
import com.githang.android.apnbb.Constants;
import org.androidpn.client.LogUtil;
import org.androidpn.client.XmppManager;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;

/** 
 * This class notifies the receiver of incoming notifcation packets asynchronously.  
 *
 * @author Geek_Soledad (msdx.android@qq.com)
 */
public class NotifyPacketListener implements PacketListener {

    private static final String LOGTAG = LogUtil
            .makeLogTag(NotifyPacketListener.class);

    private final XmppManager xmppManager;

    public NotifyPacketListener(XmppManager xmppManager) {
        this.xmppManager = xmppManager;
    }

    @Override
    public void processPacket(Packet packet) {
        Log.d(LOGTAG, "NotifyPacketListener.processPacket()...");
        Log.d(LOGTAG, "packet.toXML()=" + packet.toXML());


        if (packet instanceof NotifyIQ) {
            NotifyIQ notifyIQ = (NotifyIQ) packet;
            Log.d(LOGTAG, "packet is " + packet.getClass().toString());
            if (notifyIQ.getChildElementXML().contains(Constants.DEFAULT_NAMESPACE)) {
                Log.d(LOGTAG, notifyIQ.getChildElementXML().toString());
                Intent intent = new Intent(NotifyReceiver.ACTION_SHOW_NOTIFICATION);
                intent.putExtra(Constants.INTENT_EXTRA_IQ, notifyIQ);
                //                intent.setData(Uri.parse((new StringBuilder(
                //                        "notif://notification.androidpn.org/")).append(
                //                        notificationApiKey).append("/").append(
                //                        System.currentTimeMillis()).toString()));

                xmppManager.getContext().sendBroadcast(intent);
                Log.d(LOGTAG, "send broadcast" + NotifyReceiver.ACTION_SHOW_NOTIFICATION);
                Intent receiptIntent = new Intent(BroadcastUtil.APN_ACTION_RECEIPT);
                receiptIntent.putExtra(Constants.INTENT_EXTRA_IQ, notifyIQ);
                BroadcastUtil.sendBroadcast(xmppManager.getContext(), receiptIntent);
            }
        }

    }

}
