package com.example.st.arcgiscss.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.st.arcgiscss.model.Incidents;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class CIncidentDBHelper {
    private static CIncidentDBHelper instance = null;

    private SqlLiteHelper helper;
    private SQLiteDatabase db;


    public CIncidentDBHelper(Context context) {
        helper = new SqlLiteHelper(context);
        db = helper.getWritableDatabase();
    }

    public void closeDb(){
        db.close();
        helper.close();
    }
    public static CIncidentDBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new CIncidentDBHelper(context);
        }
        return instance;
    }

    private class SqlLiteHelper extends SQLiteOpenHelper {

        private static final int DB_VERSION = 1;
        private static final String DB_NAME = "cincident";
        public SqlLiteHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "CREATE TABLE  IF NOT EXISTS " + DB_NAME
                    + "( id INTEGER PRIMARY KEY AUTOINCREMENT,incidentno text,incident text"+")";
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

    public void saveCIncidentMsg(Incidents msg){
        Gson gson = new Gson();
        String incident = gson.toJson(msg, Incidents.class);
        ContentValues values = new ContentValues();
        values.put("incidentno", msg.getIncidentNo());
        values.put("incident",incident);
        if (getIncidentsInfo(msg.getIncidentNo()) == null){
            db.insert(helper.DB_NAME, "id", values);
        }else {
            db.update(helper.DB_NAME,values,"incidentno=?",new String[]{msg.getIncidentNo()});
        }

    }

    /**
     */
    public void delUpDate(String incidentno){
        db.delete(helper.DB_NAME, "incidentno=?", new String[]{incidentno});
    }


    /**
     *  View route history
     */
    public Incidents searchCIncidentdb(String incidentno){
        String msg;
        Gson gson = new Gson();
        Incidents incidents = new Incidents();
        String sql = "select * from  "+helper.DB_NAME +
                " where incidentno = ?"+
                "order by id desc";
        Cursor cursor = db.rawQuery(sql, new String[]{incidentno});
        while(cursor.moveToNext()){
            String route = cursor.getString(2);
            incidents = gson.fromJson(route,Incidents.class);
        }
        return incidents;
    }

    public List<Incidents> getIncidentMsg()  {
        List<Incidents> incidentItems = new ArrayList<Incidents>();
        Incidents msg;
        Gson gson = new Gson();
        String sql = "select * from "+helper.DB_NAME;
        Cursor cursor = db.rawQuery(sql, new String[]{});
        while(cursor.moveToNext()){
            String incidentStr = cursor.getString(2);
            Incidents incident = gson.fromJson(incidentStr,Incidents.class);
            incidentItems.add(incident);
        }
        cursor.close();
        return incidentItems;
    }


//    public List<Incidents> getIncidentMsg(String route_number)  {
//        List<Incidents> incidentItems = new ArrayList<Incidents>();
//        Incidents msg;
//        Gson gson = new Gson();
//        String sql = "select * from "+helper.DB_NAME;
//        Cursor cursor = db.rawQuery(sql, new String[]{});
//        while(cursor.moveToNext()){
//            String incidentStr = cursor.getString(3);
//            Incidents incident = gson.fromJson(incidentStr,Incidents.class);
//            if (incident.getAffectRoute().contains(route_number)){
//                incidentItems.add(incident);
//            }
//        }
//        cursor.close();
//        return incidentItems;
//    }


    public Incidents getIncidentsInfo(String incidentNo)  {
        Gson gson = new Gson();
        String sql = "select * from "+helper.DB_NAME;
        Cursor cursor = db.rawQuery(sql, new String[]{});
        while(cursor.moveToNext()){
            String incidentStr = cursor.getString(2);
            Incidents incident = gson.fromJson(incidentStr,Incidents.class);
            if (incidentNo.equals(incident.getIncidentNo())){
                return incident;
            }
        }
        cursor.close();
        return null;
    }

    public void clear(){
        db.delete(helper.DB_NAME, "id>?", new String[]{"0"});
    }
}


