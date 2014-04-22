package com.githang.android.apnbb.demo.db;/**
 * Created by msdx on 2014/3/26.
 */

/**
 * User: Geek_Soledad(msdx.android@qq.com)
 * Date: 2014-03-26
 * Time: 13:08
 * 数据库常量类。
 */
public interface DBConsts {

    String DB_NAME = "androidpn.db";
    int VERSION_2 = 2;
    int VERSION_3 = 3;
    int DB_VERSION = VERSION_3;

    String _ID = "_id";

    String TABLE_IQ = "t_iq";

    String IQ_ID = "id";
    String IQ_APIKEY = "apikey";
    String IQ_TITLE = "title";
    String IQ_MSG = "message";
    String IQ_URI = "uri";
    String IQ_TIME = "time";

    String TABLE_SIGNAL = "t_light";
    String SIGNAL_X = "x_axis";
    String SIGNAL_Y = "y_axis";

    String CREATE_TABLE_IQ = String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY, %s TEXT,  %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)", TABLE_IQ, _ID, IQ_ID, IQ_APIKEY, IQ_TITLE, IQ_MSG, IQ_URI, IQ_TIME);
    String UPDATE_TABLE_IQ_FROM_1 = String.format("ALTER TABLE %s ADD time TEXT", IQ_TIME);
    String QUERY_ALL_IQ = String.format("SELECT %s, %s, %s, %s, %s, %s, %s FROM %s ORDER BY %s desc", _ID, IQ_ID, IQ_APIKEY, IQ_TITLE, IQ_MSG, IQ_URI, IQ_TIME, TABLE_IQ, _ID);
    String QUERY_IQ_BY_TABLE_ID = String.format("SELECT %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s=", _ID, IQ_ID, IQ_APIKEY, IQ_TITLE, IQ_MSG, IQ_URI, IQ_TIME, TABLE_IQ, _ID);

    String CREATE_TABLE_SIGNAL = String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY, %s INTEGER, %s INTEGER)", TABLE_SIGNAL, _ID, SIGNAL_X, SIGNAL_Y);
    String QUERY_ALL_SIGNAL = String.format("SELECT %s, %s, %s FROM %s", _ID, SIGNAL_X, SIGNAL_Y, TABLE_SIGNAL);
}
