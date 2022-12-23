package com.example.st.arcgiscss.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.st.arcgiscss.model.Direction;
import com.google.gson.Gson;

public class NavigationDBHepler {
    private static NavigationDBHepler instance = null;

    private SqlLiteHelper helper;
    private SQLiteDatabase db;


    public NavigationDBHepler(Context context) {
        helper = new SqlLiteHelper(context);
        db = helper.getWritableDatabase();
    }

    public void closeDb(){
        db.close();
        helper.close();
    }
    public static NavigationDBHepler getInstance(Context context) {
        if (instance == null) {
            instance = new NavigationDBHepler(context);
        }
        return instance;
    }

    private class SqlLiteHelper extends SQLiteOpenHelper {

        private static final int DB_VERSION = 1;
        private static final String DB_NAME = "navigation";
        public SqlLiteHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "CREATE TABLE  IF NOT EXISTS " + DB_NAME
                    + "( id INTEGER PRIMARY KEY AUTOINCREMENT,routeno text,direction text"+")";
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

    public void saveNavigationMsg(String routeNo, Direction direction){
        Gson gson = new Gson();
        String dt = gson.toJson(direction, Direction.class);
        ContentValues values = new ContentValues();
        values.put("routeno", routeNo);
        values.put("direction",dt);
        if (getNavigationInfo(routeNo) == null){
            db.insert(helper.DB_NAME, "id", values);
        }else {
            db.update(helper.DB_NAME,values,"routeno=?",new String[]{routeNo});
        }

    }

    /**
     */
    public void delUpDate(String routeno){
        db.delete(helper.DB_NAME, "routeno=?", new String[]{routeno});
    }


    /**
     *  View route history
     */
    public Direction getNavigationInfo(String routeNo){
        String msg;
        Gson gson = new Gson();
        Direction direction = new Direction();
        String sql = "select * from  "+helper.DB_NAME +
                " where routeno = ?"+
                "order by id desc";
        Cursor cursor = db.rawQuery(sql, new String[]{routeNo});
        while(cursor.moveToNext()){
            String route = cursor.getString(2);
            direction = gson.fromJson(route,Direction.class);
            return direction;
        }
        return null;
    }


//    public Direction getNavigationInfo(String routeNo)  {
//        Gson gson = new Gson();
//        String sql = "select * from "+helper.DB_NAME;
//        Cursor cursor = db.rawQuery(sql, new String[]{});
//        while(cursor.moveToNext()){
//            String incidentStr = cursor.getString(3);
//            Direction incident = gson.fromJson(incidentStr,Direction.class);
//            if (Direction.equals(incident.getIncidentNo())){
//                return incident;
//            }
//        }
//        cursor.close();
//        return null;
//    }

    public void clear(){
        db.delete(helper.DB_NAME, "id>?", new String[]{"0"});
    }
}

