package com.example.st.arcgiscss.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.st.arcgiscss.model.ATraveld;
import com.example.st.arcgiscss.model.RouteWay;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class RouteNaviInfoDBHelper {
    private static RouteNaviInfoDBHelper instance = null;
    private SqlLiteHelper helper;
    private SQLiteDatabase db;
    private Context context;

    public RouteNaviInfoDBHelper(Context context) {
        helper = new SqlLiteHelper(context);
        db = helper.getWritableDatabase();
    }

    public void closeDb(){
        db.close();
        helper.close();
    }
    public static RouteNaviInfoDBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new RouteNaviInfoDBHelper(context);
        }
        return instance;
    }



    private class SqlLiteHelper extends SQLiteOpenHelper {

        private static final int DB_VERSION = 1;
        private static final String DB_NAME = "routenavigtion";
        public SqlLiteHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "CREATE TABLE  IF NOT EXISTS " + DB_NAME
                    + "( id INTEGER PRIMARY KEY AUTOINCREMENT,routeno text,atraveld text"+")";
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

    public void saveRouteNaviInfoMsg(ATraveld aTraveld){
        Gson gson = new Gson();
        ContentValues values = new ContentValues();
        values.put("routeno",aTraveld.getRouteNo());
        values.put("atraveld",gson.toJson(aTraveld));
        if (getNavigationInfo(aTraveld.getRouteNo()) == null){
            db.insert(helper.DB_NAME, "id", values);
        }else {
            db.update(helper.DB_NAME,values,"routeno=?",new String[]{aTraveld.getRouteNo()});
        }
    }


    /**
     *  View route history
     */
    public ATraveld getNavigationInfo(String routeNo){
        String msg;
        Gson gson = new Gson();
        ATraveld aTraveld = new ATraveld();
        String sql = "select * from  "+helper.DB_NAME +
                " where routeno = ?"+
                "order by id desc";
        Cursor cursor = db.rawQuery(sql, new String[]{routeNo});
        while(cursor.moveToNext()){
            String ahead = cursor.getString(2);
            aTraveld = gson.fromJson(ahead,ATraveld.class);
            return aTraveld;
        }
        return null;
    }


//    public void updateRouteMsg(RouteWay msg){
//        Gson gson = new Gson();
//        String sql = "select * from  "+helper.DB_NAME;
//        Cursor cursor = db.rawQuery(sql, new String[]{});
//
//        while(cursor.moveToNext()){
//            String route = cursor.getString(2);
//            RouteWay routeWay = gson.fromJson(route,RouteWay.class);
//            if (routeWay.getRouteNo().equals(msg.getRouteNo())){
//                ContentValues values = new ContentValues();
//                values.put("time",routeWay.getTime());
//                msg.setTime(routeWay.getTime());
//                String newroute = gson.toJson(msg, RouteWay.class);
//                values.put("routeway",newroute);
//                db.update(helper.DB_NAME,values,"time=?",new String[]{routeWay.getTime()});
//            }
//        }
//    }
//
//    /**
//     *  Check if chat history exists
//     */
//    public boolean searchRouteNamedb(String time){
//        String msg;
//        Gson gson = new Gson();
//        String sql = "select * from  "+helper.DB_NAME +
//                " where time = ?"+
//                "order by id desc";
//        Cursor cursor = db.rawQuery(sql, new String[]{time});
//        while(cursor.moveToNext()){
//            String route = cursor.getString(2);
//            RouteWay routeWay = gson.fromJson(route,RouteWay.class);
//            if (routeWay.getTime().equals(time)){
//                return true;
//            }
//        }
//        return false;
//    }
//
//
////    public List<String> deleteTipsTime(String convoyNo) {
////        List<String> tipslist = new ArrayList<>();
////        Gson gson = new Gson();
////        String sql = "select * from "+helper.DB_NAME;
////        Cursor cursor = db.rawQuery(sql, new String[]{});
////        while(cursor.moveToNext()){
////            String routeway_str = cursor.getString(1);
////            RouteWay rout = gson.fromJson(routeway_str,RouteWay.class);
////            if (rout.getConvoy().getConvoyNo() == convoyNo){
////                tipslist.add(rout.getTime());
////                delTimeDate(rout.getTime());
////            }
////        }
////        cursor.close();
////        return  tipslist;
////    }
//
//
//    /**
//     *  View route history
//     */
//    public RouteWay searchRouteWaydb(String time){
//        String msg;
//        Gson gson = new Gson();
//        RouteWay routeWay = new RouteWay();
//        String sql = "select * from  "+helper.DB_NAME +
//                " where time = ?"+
//                "order by id desc";
//        Cursor cursor = db.rawQuery(sql, new String[]{time});
//        while(cursor.moveToNext()){
//            String route = cursor.getString(2);
//            routeWay = gson.fromJson(route,RouteWay.class);
//            return routeWay;
//        }
//        return null;
//    }

    public List<ATraveld> getRouteWayMsg()  {
        List<ATraveld> routeWays = new ArrayList<ATraveld>();
        RouteWay msg;
        Gson gson = new Gson();
        String sql = "select * from "+helper.DB_NAME;
        Cursor cursor = db.rawQuery(sql, new String[]{});
        while(cursor.moveToNext()){
            String routeway_str = cursor.getString(2);
            ATraveld rout = gson.fromJson(routeway_str,ATraveld.class);
            routeWays.add(rout);
        }
        cursor.close();
        return routeWays;
    }

//    public void clear(){
//        db.delete(helper.DB_NAME, "id>?", new String[]{"0"});
//    }
//
//
//
    public void delRouteNoDate(String routeno){
        db.delete(helper.DB_NAME, "routeno=?", new String[]{routeno});
    }
}


