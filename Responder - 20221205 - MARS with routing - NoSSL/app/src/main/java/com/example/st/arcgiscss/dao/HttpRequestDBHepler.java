package com.example.st.arcgiscss.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.st.arcgiscss.model.RequestModel;

public class HttpRequestDBHepler {
    private static HttpRequestDBHepler instance = null;

    private SqlLiteHelper helper;
    private SQLiteDatabase db;


    public HttpRequestDBHepler(Context context) {
        helper = new SqlLiteHelper(context);
        db = helper.getWritableDatabase();
    }

    public void closeDb(){
        db.close();
        helper.close();
    }
    public static HttpRequestDBHepler getInstance(Context context) {
        if (instance == null) {
            instance = new HttpRequestDBHepler(context);
        }
        return instance;
    }

    private class SqlLiteHelper extends SQLiteOpenHelper {

        private static final int DB_VERSION = 1;
        private static final String DB_NAME = "httprequest";
        public SqlLiteHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "CREATE TABLE  IF NOT EXISTS " + DB_NAME
                    + "( id INTEGER PRIMARY KEY AUTOINCREMENT,requestname text,requestparams text"+")";
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

    public void saveHttpRequestMsg(RequestModel requestModel){
        ContentValues values = new ContentValues();
        values.put("requestname", requestModel.getRequestName());
        values.put("requestparams",requestModel.getRequestparams());
        db.insert(helper.DB_NAME, "id", values);
    }

    /**
     */
    public void delUpDate(String requestName){
        db.delete(helper.DB_NAME, "requestname=?", new String[]{requestName});
    }


//    public List<RequestModel> getAllRequest(){
//
//        List<RequestModel> requestModelList = new ArrayList<>();
//       String sql = "select * from  "+helper.DB_NAME +
//                " order by id desc";
//        Cursor cursor = db.rawQuery(sql, new String[]{});
//        while(cursor.moveToNext()){
//            String requestName = cursor.getString(1);
//            String requestparams = cursor.getString(2);
//
//            RequestModel requestModel = new RequestModel();
//            requestModel.setRequestName(requestName);
//            requestModel.setRequestparams(requestparams);
//            requestModelList.add(requestModel);
//        }
//        return requestModelList;
//    }



    public boolean deletPostRequestdb() {
        String selectQuerey = "SELECT * FROM " + helper.DB_NAME;
        Cursor cursor = db.rawQuery(selectQuerey, null);
        if (cursor.moveToFirst()) {
            db.delete(helper.DB_NAME, "id = ?", new String[]{cursor.getInt(0) + ""});
            return true;
        }
        return false;
    }


    public RequestModel getPostRequestdb() {
        String selectQuerey = "SELECT * FROM " + helper.DB_NAME;
        RequestModel prioritise = new RequestModel();
        Cursor cursor = db.rawQuery(selectQuerey, null);
        if (cursor.moveToFirst()) {
            prioritise.setRequestName(cursor.getString(1));
            prioritise.setRequestparams(cursor.getString(2));
//                prioritise.setRequestTag(cursor.getString(3));
        }
        return prioritise;
    }


    /**
     *  View route history
     */
//    public Direction getRequestHttpInfo(String routeNo){
//        String msg;
//        Gson gson = new Gson();
//        Direction direction = new Direction();
//        String sql = "select * from  "+helper.DB_NAME +
//                " where routeno = ?"+
//                "order by id desc";
//        Cursor cursor = db.rawQuery(sql, new String[]{routeNo});
//        while(cursor.moveToNext()){
//            String route = cursor.getString(2);
//            direction = gson.fromJson(route,Direction.class);
//            return direction;
//        }
//        return null;
//    }


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

