/*
 * @(#)CursorUtil.java		       Project:UniversityTimetable
 * Date:2013-1-21
 *
 * Copyright (c) 2013 CFuture09, Institute of Software, 
 * Guangdong Ocean University, Zhanjiang, GuangDong, China.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.githang.android.apnbb.demo.db;

import android.database.Cursor;

/**
 * 根据列名获取属性值的工具类。
 * 
 * @author Geek_Soledad (66704238@51uc.com)
 */
public class CursorUtil {

	/**
	 * 以字符串返回请求的列的值
	 * 
	 * @param c
	 * @param column
	 *            请求的列名
	 * @return
	 */
	public static String getString(Cursor c, String column) {
		return c.getString(c.getColumnIndex(column));
	}

	/**
	 * 以整型返回请求的列的值
	 * 
	 * @param c
	 * @param column
	 *            请求的列名
	 * @return
	 */
	public static int getInt(Cursor c, String column) {
		return c.getInt(c.getColumnIndex(column));
	}

	/**
	 * 以短整型返回请求的列的值
	 * 
	 * @param c
	 * @param column
	 *            请求的列名
	 * @return
	 */
	public static short getShort(Cursor c, String column) {
		return c.getShort(c.getColumnIndex(column));
	}

	/**
	 * 以长整型返回请求的列的值
	 * 
	 * @param c
	 * @param column
	 *            请求的列名
	 * @return
	 */
	public static long getLong(Cursor c, String column) {
		return c.getLong(c.getColumnIndex(column));
	}

	/**
	 * 以浮点型返回请求的列的值。
	 * 
	 * @param c
	 * @param column
	 *            请求的列名
	 * @return
	 */
	public static float getFloat(Cursor c, String column) {
		return c.getFloat(c.getColumnIndex(column));
	}

	/**
	 * 以双精度浮点型返回请求的列的值。
	 * 
	 * @param c
	 * @param column
	 *            请求的列名
	 * @return
	 */
	public static double getDouble(Cursor c, String column) {
		return c.getDouble(c.getColumnIndex(column));
	}

	/**
	 * 以二进制大对象类型返回请求的列的值。
	 * 
	 * @param c
	 * @param column
	 *            请求的列名
	 * @return
	 */
	public static byte[] getBlob(Cursor c, String column) {
		return c.getBlob(c.getColumnIndex(column));
	}
}
