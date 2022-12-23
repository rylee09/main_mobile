package com.example.st.arcgiscss.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.st.arcgiscss.model.RouteWay;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class RouteDBHelper {
    private static RouteDBHelper instance = null;
    private SqlLiteHelper helper;
    private SQLiteDatabase db;
    private Context context;

    public RouteDBHelper(Context context) {
        helper = new SqlLiteHelper(context);
        db = helper.getWritableDatabase();
    }

    public void closeDb(){
        db.close();
        helper.close();
    }
    public static RouteDBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new RouteDBHelper(context);
        }
        return instance;
    }



    private class SqlLiteHelper extends SQLiteOpenHelper {

        private static final int DB_VERSION = 1;
        private static final String DB_NAME = "route";
        public SqlLiteHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "CREATE TABLE  IF NOT EXISTS " + DB_NAME
                    + "( id INTEGER PRIMARY KEY AUTOINCREMENT,time text,routeway text"+")";
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

    public void saveRouteMsg(RouteWay msg){
        Gson gson = new Gson();
        String routeway = gson.toJson(msg, RouteWay.class);
        ContentValues values = new ContentValues();
        values.put("time",msg.getTime());
        values.put("routeway",routeway);
        db.insert(helper.DB_NAME, "id", values);
    }


    public void updateRouteMsg(RouteWay msg){
        Gson gson = new Gson();
        String sql = "select * from  "+helper.DB_NAME;
        Cursor cursor = db.rawQuery(sql, new String[]{});

        while(cursor.moveToNext()){
            String route = cursor.getString(2);
            RouteWay routeWay = gson.fromJson(route,RouteWay.class);
            if (routeWay.getRouteNo().equals(msg.getRouteNo())){
                ContentValues values = new ContentValues();
                values.put("time",routeWay.getTime());
                msg.setTime(routeWay.getTime());
                String newroute = gson.toJson(msg, RouteWay.class);
                values.put("routeway",newroute);
                db.update(helper.DB_NAME,values,"time=?",new String[]{routeWay.getTime()});
            }
        }
    }

    /**
     *  Check if chat history exists
     */
    public boolean searchRouteNamedb(String time){
        String msg;
        Gson gson = new Gson();
        String sql = "select * from  "+helper.DB_NAME +
                " where time = ?"+
                "order by id desc";
        Cursor cursor = db.rawQuery(sql, new String[]{time});
        while(cursor.moveToNext()){
            String route = cursor.getString(2);
            RouteWay routeWay = gson.fromJson(route,RouteWay.class);
            if (routeWay.getTime().equals(time)){
                return true;
            }
        }
        return false;
    }


//    public List<String> deleteTipsTime(String convoyNo) {
//        List<String> tipslist = new ArrayList<>();
//        Gson gson = new Gson();
//        String sql = "select * from "+helper.DB_NAME;
//        Cursor cursor = db.rawQuery(sql, new String[]{});
//        while(cursor.moveToNext()){
//            String routeway_str = cursor.getString(1);
//            RouteWay rout = gson.fromJson(routeway_str,RouteWay.class);
//            if (rout.getConvoy().getConvoyNo() == convoyNo){
//                tipslist.add(rout.getTime());
//                delTimeDate(rout.getTime());
//            }
//        }
//        cursor.close();
//        return  tipslist;
//    }


    /**
     *  View route history
     */
    public RouteWay searchRouteWaydb(String time){
        String msg;
        Gson gson = new Gson();
        RouteWay routeWay = new RouteWay();
        String sql = "select * from  "+helper.DB_NAME +
                " where time = ?"+
                "order by id desc";
        Cursor cursor = db.rawQuery(sql, new String[]{time});
        while(cursor.moveToNext()){
            String route = cursor.getString(2);
            routeWay = gson.fromJson(route,RouteWay.class);
            return routeWay;
        }
        return null;
    }

    public List<RouteWay> getRouteWayMsg()  {
        List<RouteWay> routeWays = new ArrayList<RouteWay>();
        RouteWay msg;
        Gson gson = new Gson();
        String sql = "select * from "+helper.DB_NAME;
        Cursor cursor = db.rawQuery(sql, new String[]{});
        while(cursor.moveToNext()){
            String routeway_str = cursor.getString(1);
            RouteWay rout = gson.fromJson(routeway_str,RouteWay.class);
            routeWays.add(rout);
//            msg = new Incident(cursor.getInt(0),cursor.getString(1),cursor.getString(2), cursor.getString(3), cursor.getString(4)
//                    , cursor.getString(5), cursor.getInt(6));
//            try {
//                if (!DateUtil.compare(msg.sendDate)){
//                    chatItems.add(msg);
//                    msg = null;
//                }else {
//                    delChatMsgName(msg.sendDate);
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }
        }
        cursor.close();
        return routeWays;
    }

    public void clear(){
        db.delete(helper.DB_NAME, "id>?", new String[]{"0"});
    }



    public void delTimeDate(String time){
        db.delete(helper.DB_NAME, "time=?", new String[]{time});
    }
}

