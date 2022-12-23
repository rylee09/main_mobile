package com.example.st.arcgiscss.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NewMsgDbHelper {
	private static NewMsgDbHelper instance = null;

	private SqlLiteHelper helper;
	private SQLiteDatabase db;

	public NewMsgDbHelper(Context context) {
		helper = new SqlLiteHelper(context);
		db = helper.getWritableDatabase();
	}

	public void closeDb(){
		db.close();
		helper.close();
	}
	public static NewMsgDbHelper getInstance(Context context) {
		if (instance == null) {
			instance = new NewMsgDbHelper(context);
		}
		return instance;
	}

	private class SqlLiteHelper extends SQLiteOpenHelper {
		private static final int DB_VERSION = 1;
		private static final String DB_NAME = "newMsg";

		public SqlLiteHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql = "CREATE TABLE  IF NOT EXISTS newMsg"
					+ "( id INTEGER PRIMARY KEY AUTOINCREMENT,tip text" +")";
			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			dropTable(db);
			onCreate(db);
		}

		private void dropTable(SQLiteDatabase db) {
			String sql = "DROP TABLE IF EXISTS newMsg";
			db.execSQL(sql);
		}

	}

	/**
	 * 0
	 */
	public void saveNewMsg(String tip){
		ContentValues values = new ContentValues();
		values.put("tip",tip);
		db.insert(helper.DB_NAME, null, values);
	}

	public int getCountNewMsg(){
		String sql = "select * from "+helper.DB_NAME;
		Cursor cursor = db.rawQuery(sql, new String[]{});
		int count = cursor.getCount();
		cursor.close();
		return count;
	}

//	/**
//	 * @param msgId
//	 */
//	public void delNewMsg(String msgId){
//		db.delete(helper.DB_NAME, " msgId=? and whosMsg=?", new String[]{msgId,Constants.USER_NAME});
//	}
	/**
	 */
	public void delUpDate(String sendDate){
		db.delete(helper.DB_NAME, " tip=?", new String[]{sendDate});
	}

	public void clearAllNew(){
		db.delete(helper.DB_NAME, " id>?", new String[]{"0"});
	}

//	public int getMsgCount(String msgId){
//		int count = 0 ;
//		String sql ="select sendDate from newMsg where msgId=? and whosMsg=?";
//		Cursor cursor = db.rawQuery(sql, new String[]{msgId,Constants.USER_NAME});
//		if(cursor.moveToFirst()){
//			do{
//				String sendDate = cursor.getString(0);
//				try {
//					if (!DateUtil.compare(sendDate)){
//						count++;
//					}else {
//						delUpDate(sendDate);
//					}
//				} catch (ParseException e) {
//					e.printStackTrace();
//				}
//
//			} while (cursor.moveToNext());
//
//		}
//		cursor.close();
//		return count;
//	}
//

//	public int getMsgCount(){
//		int count = 0 ;
//		String sql ="select sendDate from newMsg where whosMsg=?";
//		Cursor cursor = db.rawQuery(sql, new String[]{Constants.USER_NAME});
//		if(cursor.moveToFirst()){
//			do{
//				String sendDate = cursor.getString(0);
//				try {
//					if (!DateUtil.compare(sendDate)){
//						count++;
//					}else {
//						delUpDate(sendDate);
//					}
//				} catch (ParseException e) {
//					e.printStackTrace();
//				}
//
//			} while (cursor.moveToNext());
//
//		}
//		cursor.close();
//		return count;
//	}

	public void clear(){
		db.delete(helper.DB_NAME, "id>?", new String[]{"0"});
	}
}