package com.githang.android.apnbb.demo.db;/**
 * Created by msdx on 2014/3/26.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.androidpn.client.LogUtil;

/**
 * User: Geek_Soledad(msdx.android@qq.com)
 * Date: 2014-03-26
 * Time: 13:06
 * 实现数据库的创建升级操作。
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = LogUtil.makeLogTag(DBOpenHelper.class);

    public DBOpenHelper(Context context) {
        super(context, DBConsts.DB_NAME, null, DBConsts.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(LOG_TAG, "create database");
        Log.i(LOG_TAG, DBConsts.CREATE_TABLE_IQ);
        db.execSQL(DBConsts.CREATE_TABLE_IQ);
        Log.i(LOG_TAG, DBConsts.CREATE_TABLE_SIGNAL);
        db.execSQL(DBConsts.CREATE_TABLE_SIGNAL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion <= oldVersion) {
            return;
        }

        if(oldVersion < DBConsts.VERSION_2 ) {
            db.execSQL(DBConsts.UPDATE_TABLE_IQ_FROM_1);
        }

        if(oldVersion < DBConsts.VERSION_3) {
            db.execSQL(DBConsts.CREATE_TABLE_SIGNAL);
        }
    }
}
