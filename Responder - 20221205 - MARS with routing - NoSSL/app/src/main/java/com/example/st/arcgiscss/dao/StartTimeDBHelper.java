package com.example.st.arcgiscss.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StartTimeDBHelper {
    private static StartTimeDBHelper instance = null;

    private SqlLiteHelper helper;
    private SQLiteDatabase db;

    public StartTimeDBHelper(Context context) {
        helper = new SqlLiteHelper(context);
        db = helper.getWritableDatabase();
    }

    public void closeDb(){
        db.close();
        helper.close();
    }
    public static StartTimeDBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new StartTimeDBHelper(context);
        }
        return instance;
    }

    private class SqlLiteHelper extends SQLiteOpenHelper {
        private static final int DB_VERSION = 1;
        private static final String DB_NAME = "setime";

        public SqlLiteHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "CREATE TABLE  IF NOT EXISTS setime"
                    + "( id INTEGER PRIMARY KEY AUTOINCREMENT,stime text"+")";
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            dropTable(db);
            onCreate(db);
        }

        private void dropTable(SQLiteDatabase db) {
            String sql = "DROP TABLE IF EXISTS setime";
            db.execSQL(sql);
        }

    }

    /**
     * 0
     */
//    public void saveTimeMsg(String start,String number){
//        boolean flag = searchNumberdb(number);
//        ContentValues values = new ContentValues();
//        if (flag){
//            values.put("number",number);
//            values.put("stime",start);
//            db.update(helper.DB_NAME,values,"number=?",new String[]{number});
//        }else {
//            values.put("number",number);
//            values.put("stime",start);
//            db.insert(helper.DB_NAME, "id", values);
//        }
//    }


    public void saveTimeMsg(String start){
        ContentValues values = new ContentValues();
        values.put("stime",start);
        db.insert(helper.DB_NAME, "id", values);

    }

//    public int getCountTimeMsg(){
//        String sql = "select * from "+helper.DB_NAME;
//        Cursor cursor = db.rawQuery(sql, new String[]{});
//        int count = cursor.getCount();
//        cursor.close();
//        return count;
//    }
//
//
//
//    public String getTimeMsg()  {
//        String sql = "select * from "+helper.DB_NAME;
//        Cursor cursor = db.rawQuery(sql, new String[]{});
//        while(cursor.moveToFirst()){
//            String time_str = cursor.getString(2);
//            return time_str;
//        }
//        cursor.close();
//        return null;
//    }


    /**
     * Get time series
     */
    public String getStartTime(){
        String starts = new String();
        String sql = "select * from " +helper.DB_NAME +
                " order by stime asc";
        Cursor cursor = db.rawQuery(sql, new String[]{});
        while(cursor.moveToNext()){
            starts =cursor.getString(1);
        }
        cursor.close();
        return starts;
    }


//    public List<String> getAllStartTimeList(){
//        List<String> starts = new ArrayList<String>();
//        String sql = "select * from " +helper.DB_NAME;
//        Cursor cursor = db.rawQuery(sql, new String[]{});
//        while(cursor.moveToNext()){
//            starts.add(cursor.getString(2));
//        }
//        cursor.close();
//        return starts;
//    }

//	/**
//	 * @param msgId
//	 */
//	public void delNewMsg(String msgId){
//		db.delete(helper.DB_NAME, " msgId=? and whosMsg=?", new String[]{msgId,Constants.USER_NAME});
//	}
    /**
     */
    public void delTimeDate(String stime){
        db.delete(helper.DB_NAME, " stime=?", new String[]{stime});
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


    public boolean searchNumberdb(String number){
        String sql = "select stime from  "+helper.DB_NAME +
                " where number = ? "+
                "order by id desc";
        Cursor cursor = db.rawQuery(sql, new String[]{number});
        while(cursor.moveToNext()){
            String stime = cursor.getString(0);
            if (stime != null && stime.length()>0){
                return true;
            }
        }
        return false;
    }

}
