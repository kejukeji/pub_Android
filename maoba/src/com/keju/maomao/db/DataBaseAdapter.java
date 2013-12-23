/**
 * 
 */
package com.keju.maomao.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.keju.maomao.bean.SortModelBean;

/**
 * 数据库操作类
 * 
 * @author zhouyong 说明： 1、数据库操作类 2、定义好数据表名，数据列，数据表创建语句 3、操作表的方法紧随其后
 */
public class DataBaseAdapter {
	/**
	 * 数据库版本
	 */
	private static final int DATABASE_VERSION = 1;
	/**
	 * 数据库名称
	 */
	private static final String DATABASE_NAME = "maomao.db";
	/**
	 * 数据库表id
	 */
	public static final String RECORD_ID = "_id";

	private SQLiteDatabase db;
	private ReaderDbOpenHelper dbOpenHelper;

	public DataBaseAdapter(Context context) {
		this.dbOpenHelper = new ReaderDbOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void open() {
		this.db = dbOpenHelper.getWritableDatabase();
	}

	public void close() {
		if (db != null) {
			db.close();
		}
		if (dbOpenHelper != null) {
			dbOpenHelper.close();
		}
	}

	private class ReaderDbOpenHelper extends SQLiteOpenHelper {

		public ReaderDbOpenHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase _db) {
			// 创建表
			_db.execSQL(CREATE_SQL_CITYS);
		}

		/**
		 * 升级应用时，有数据库改动在此方法中修改。
		 */
		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {

		}
	}

	/****************************** 城市表 ********************************/

	/**
	 * 城市表
	 */
	public static final String TABLE_NAME_CITYS = "t_citys";

	/**
	 * 城市表中的列定义
	 * 
	 * @author zhouyong 
	 */
	public interface CitysColumns {
		public static final String CITYID = "cityId";
		public static final String NAME = "name";
		public static final String PROVINCEID = "provinceId";
	}

	/**
	 * 城市表查询列
	 */
	public static final String[] PROJECTION_CITYS = new String[] { RECORD_ID,CitysColumns.PROVINCEID ,
		CitysColumns.NAME };
	/**
	 * 城市表的创建语句
	 */
	public static final String CREATE_SQL_CITYS = "create table " + TABLE_NAME_CITYS + " (" + RECORD_ID
			+ " integer primary key autoincrement," + CitysColumns.PROVINCEID + " integer, " + CitysColumns.NAME + " text " + ");";

	/**
	 * 批量插入城市信息
	 * 
	 * @param scbList
	 */
	public synchronized void bantchCitys(List<SortModelBean> citysList) {
		SQLiteDatabase localDb = db;
		try {
			localDb.beginTransaction();
			localDb.delete(TABLE_NAME_CITYS, null, null);
			for (SortModelBean citysBean : citysList) {
				String sql = "insert into " + TABLE_NAME_CITYS + " (" + CitysColumns.PROVINCEID + ","
						+ CitysColumns.NAME + ") values(?,?)";
				localDb.execSQL(sql, new Object[] {citysBean.getProvinceId(),citysBean.getCityName()});
			}
			localDb.setTransactionSuccessful();
		} finally {
			localDb.endTransaction();
		}
	}
	/**
	 * 获取城市数据
	 * 
	 * @return
	 */
	public List<SortModelBean> findAllCitys() {
		List<SortModelBean> citysList = new ArrayList<SortModelBean>();
		Cursor c = db.query(TABLE_NAME_CITYS, PROJECTION_CITYS, null, null, null, null, CitysColumns.PROVINCEID);
		while (c.moveToNext()) {
			SortModelBean city = new SortModelBean();
			city.setProvinceId(c.getInt(1));
			city.setCityName(c.getString(2));
			citysList.add(city);
		}
		c.close();
		return citysList;
	}
	/**
	 * 通过GPS、数据库获取的城市名来获取省份（直辖市）id
	 * 
	 * @param cityName
	 * @return
	 */
	public int findProvinceId(String pName) {
		int pId = 0;
		// int cityNameLe = cityName.length();
		// String cityNameSh = cityName.substring(0, cityNameLe - 1);
		Cursor cursor = db.query(TABLE_NAME_CITYS, null, "name like ?", new String[] { "%" + pName + "%" }, null, null,
				null);
		while (cursor.moveToNext()) {
			int pIndex = cursor.getColumnIndex("provinceId");
			pId = cursor.getInt(pIndex);
		}
		cursor.close();
		return pId;
	}

}
