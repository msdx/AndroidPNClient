package com.githang.android.apnbb.demo.notify;/**
 * Created by msdx on 2014/3/25.
 */

import com.githang.android.apnbb.Constants;
import org.jivesoftware.smack.packet.IQ;

import java.io.Serializable;

/**
 * User: Geek_Soledad(msdx.android@qq.com)
 * Date: 2014-03-25
 * Time: 17:59
 * 消息实体类。
 */
public class NotifyIQ extends IQ implements Serializable{

    private static final long serialVersionUID = 123123151L;

    private String id;

    private String apiKey;

    private String title;

    private String message;

    private String uri;

    private String time;


    @Override
    public String getChildElementXML() {
        StringBuilder buf = new StringBuilder();
        buf.append("<").append("notification").append(" xmlns=\"").append(
                Constants.DEFAULT_NAMESPACE).append("\">");
        if (id != null) {
            buf.append("<id>").append(id).append("</id>");
        }
        buf.append("</").append("notification").append("> ");
        return buf.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "NotifyIQ{" +
                "time='" + time + '\'' +
                ", uri='" + uri + '\'' +
                ", message='" + message + '\'' +
                ", title='" + title + '\'' +
                ", apiKey='" + apiKey + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
