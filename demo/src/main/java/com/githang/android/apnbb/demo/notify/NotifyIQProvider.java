package com.githang.android.apnbb.demo.notify;/**
 * Created by msdx on 2014/3/25.
 */

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

/**
 * User: Geek_Soledad(msdx.android@qq.com)
 * Date: 2014-03-25
 * Time: 18:02
 * 消息实体转化。
 */
public class NotifyIQProvider implements IQProvider {
    public NotifyIQProvider() {
    }

    @Override
    public IQ parseIQ(XmlPullParser parser) throws Exception {
        NotifyIQ notification = new NotifyIQ();
        for (boolean done = false; !done; ) {
            int eventType = parser.next();
            if (eventType == 2) {
                if ("id".equals(parser.getName())) {
                    notification.setId(parser.nextText());
                }
                if ("apiKey".equals(parser.getName())) {
                    notification.setApiKey(parser.nextText());
                }
                if ("title".equals(parser.getName())) {
                    notification.setTitle(parser.nextText());
                }
                if ("message".equals(parser.getName())) {
                    notification.setMessage(parser.nextText());
                }
                if ("uri".equals(parser.getName())) {
                    notification.setUri(parser.nextText());
                }
                if ("time".equals(parser.getName())) {
                    notification.setTime(parser.nextText());
                }
            } else if (eventType == 3
                    && "notification".equals(parser.getName())) {
                done = true;
            }
        }

        return notification;
    }
}
