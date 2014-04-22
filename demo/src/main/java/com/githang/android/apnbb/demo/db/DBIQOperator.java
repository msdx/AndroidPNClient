package com.githang.android.apnbb.demo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.androidpn.client.LogUtil;

import java.util.ArrayList;
import java.util.List;

import com.githang.android.apnbb.demo.notify.NotifyIQ;

/**
 * User: Geek_Soledad(msdx.android@qq.com)
 * Date: 2014-03-26
 * Time: 13:25
 * 数据库操作类。
 */
public class DBIQOperator {
    private static final String LOG_TAG = LogUtil.makeLogTag(DBIQOperator.class);
    private DBOpenHelper dbOpenHelper;
    private SQLiteDatabase dbReader;
    public DBIQOperator(Context context) {
        dbOpenHelper = new DBOpenHelper(context);
    }

    public void saveIQ(NotifyIQ iq) {
        Log.d(LOG_TAG, "saveIq: " + iq.toString());
        ContentValues cv = new ContentValues();
        cv.put(DBConsts.IQ_ID, iq.getId());
        cv.put(DBConsts.IQ_APIKEY, iq.getApiKey());
        cv.put(DBConsts.IQ_TITLE, iq.getTitle());
        cv.put(DBConsts.IQ_MSG, iq.getMessage());
        cv.put(DBConsts.IQ_URI, iq.getUri());
        cv.put(DBConsts.IQ_TIME, iq.getTime());
        SQLiteDatabase dbWriter = dbOpenHelper.getWritableDatabase();
        dbWriter.insert(DBConsts.TABLE_IQ, null, cv);
        dbWriter.close();;
    }

    public Cursor queryAll() {
        dbReader = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbReader.rawQuery(DBConsts.QUERY_ALL_IQ, null);
        cursor.moveToFirst();
        Log.d(LOG_TAG, "queryAll:" + cursor.getCount());
        return cursor;
    }

    public List<NotifyIQ> queryAllIQS() {
        SQLiteDatabase dbReader = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbReader.rawQuery(DBConsts.QUERY_ALL_IQ, null);
        ArrayList<NotifyIQ> notifyIQs = new ArrayList<NotifyIQ>();
        final int idIndex = cursor.getColumnIndex(DBConsts.IQ_ID);
        final int keyIndex = cursor.getColumnIndex(DBConsts.IQ_APIKEY);
        final int msgIndex = cursor.getColumnIndex(DBConsts.IQ_MSG);
        final int titleIndex = cursor.getColumnIndex(DBConsts.IQ_TITLE);
        final int uriIndex = cursor.getColumnIndex(DBConsts.IQ_URI);
        final int timeIndex = cursor.getColumnIndex(DBConsts.IQ_TIME);
        while(cursor.moveToNext()){
            NotifyIQ iq = new NotifyIQ();
            iq.setId(cursor.getString(idIndex));
            iq.setApiKey(cursor.getString(keyIndex));
            iq.setMessage(cursor.getString(msgIndex));
            iq.setTitle(cursor.getString(titleIndex));
            iq.setUri(cursor.getString(uriIndex));
            iq.setTime(cursor.getString(timeIndex));
            notifyIQs.add(iq);
        }
        cursor.close();
        dbReader.close();
        return notifyIQs;
    }

    public NotifyIQ queryIQBy_Id(long _id) {
        SQLiteDatabase dbReader = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbReader.rawQuery(DBConsts.QUERY_IQ_BY_TABLE_ID + _id, null);
        NotifyIQ iq = new NotifyIQ();
        if(cursor != null && cursor.getCount() > 0) {
            cursor.moveToNext();
            iq.setId(CursorUtil.getString(cursor, DBConsts.IQ_ID));
            iq.setApiKey(CursorUtil.getString(cursor, DBConsts.IQ_APIKEY));
            iq.setMessage(CursorUtil.getString(cursor, DBConsts.IQ_MSG));
            iq.setTitle(CursorUtil.getString(cursor, DBConsts.IQ_TITLE));
            iq.setUri(CursorUtil.getString(cursor, DBConsts.IQ_URI));
            iq.setTime(CursorUtil.getString(cursor, DBConsts.IQ_TIME));
        }
        cursor.close();
        dbReader.close();
        return iq;
    }

    public void closeDB() {
        if ( dbReader != null && dbReader.isOpen()){
            dbReader.close();
        }
    }
}
