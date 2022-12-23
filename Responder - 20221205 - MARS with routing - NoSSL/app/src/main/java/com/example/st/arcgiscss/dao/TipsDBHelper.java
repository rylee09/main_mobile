package com.example.st.arcgiscss.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class TipsDBHelper {
    private static TipsDBHelper instance = null;
    private SqlLiteHelper helper;
    private SQLiteDatabase db;

    public TipsDBHelper(Context context) {
        helper = new SqlLiteHelper(context);
        db = helper.getWritableDatabase();
    }

    public void closeDb(){
        db.close();
        helper.close();
    }
    public static TipsDBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new TipsDBHelper(context);
        }
        return instance;
    }

    private class SqlLiteHelper extends SQLiteOpenHelper {

        private static final int DB_VERSION = 1;
        private static final String DB_NAME = "tips";
        public SqlLiteHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "CREATE TABLE  IF NOT EXISTS " + DB_NAME
                    + "( id INTEGER PRIMARY KEY AUTOINCREMENT,tip text,route_number text"+")";
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            dropTable(db);
            onCreate(db);
        }

        private void dropTable(SQLiteDatabase db) {
            String sql = "DROP TABLE IF EXISTS "+DB_NAME;
            db.execSQL(sql);
        }

    }

    public void saveTipsMsg(String msg,String number){
        ContentValues values = new ContentValues();
        values.put("tip",msg);
        values.put("route_number",number);
        db.insert(helper.DB_NAME, "id", values);
    }

    public int getCountTips(){
        String sql = "select * from "+helper.DB_NAME;
        Cursor cursor = db.rawQuery(sql, new String[]{});
        int count = cursor.getCount();
        cursor.close();
        return count;
    }


    public List<String> getTipsMsg()  {
        List<String> tips = new ArrayList<String>();
        String sql = "select * from "+helper.DB_NAME+" order by id desc ";
        Cursor cursor = db.rawQuery(sql, new String[]{});
        while(cursor.moveToNext()){
            String tip_str = cursor.getString(1);
            tips.add(tip_str);
        }
        cursor.close();
        return tips;
    }

    /**
     * View notification history
     */
    public String searchTipsNamedb(String tip){
        String time = "";
        String sql = "select * from  "+helper.DB_NAME +
                " where tip = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{tip});
        while(cursor.moveToNext()){
            time = cursor.getString(2);
        }
        cursor.close();
        return time;
    }

    public String searchTipsdb(String time){
        String tip = "";
        String sql = "select * from  "+helper.DB_NAME +
                " where route_number = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{time});
        while(cursor.moveToNext()){
            tip = cursor.getString(1);
        }
        cursor.close();
        return tip;
    }


//    public String searchTipsNamedb(String route_number){
//        String time = "";
//        String sql = "select * from  "+helper.DB_NAME +
//                " where route_number = ?";
//        Cursor cursor = db.rawQuery(sql, new String[]{route_number});
//        while(cursor.moveToNext()){
//            time = cursor.getString(2);
//        }
//        cursor.close();
//        return time;
//    }


    public String delTimeDatetip(String route_time){
        db.delete(helper.DB_NAME, "route_number=?", new String[]{route_time});
        return searchTipsdb(route_time);
    }


    public void clear(){
        db.delete(helper.DB_NAME, "id>?", new String[]{"0"});
    }
}