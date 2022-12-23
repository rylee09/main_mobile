package com.example.st.arcgiscss.dao;

public class LocationDBHelper{
//        private static LocationDBHelper instance = null;
//
//        private SqlLiteHelper helper;
//        private SQLiteDatabase db;
//
//
//        public LocationDBHelper(Context context) {
//            helper = new SqlLiteHelper(context);
//            db = helper.getWritableDatabase();
//        }
//
//        public void closeDb(){
//            db.close();
//            helper.close();
//        }
//        public static LocationDBHelper getInstance(Context context) {
//            if (instance == null) {
//                instance = new LocationDBHelper(context);
//            }
//            return instance;
//        }
//
//private class SqlLiteHelper extends SQLiteOpenHelper {
//
//    private static final int DB_VERSION = 1;
//    private static final String DB_NAME = "location";
//    public SqlLiteHelper(Context context) {
//        super(context, DB_NAME, null, DB_VERSION);
//    }
//
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        String sql = "CREATE TABLE  IF NOT EXISTS " + DB_NAME
//                + "( id INTEGER PRIMARY KEY AUTOINCREMENT,location text,act_location text"+")";
//        db.execSQL(sql);
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        dropTable(db);
//        onCreate(db);
//    }
//
//    private void dropTable(SQLiteDatabase db) {
//        String sql = "DROP TABLE IF EXISTS "+DB_NAME;
//        db.execSQL(sql);
//    }
//
//}
//
//    public void saveNavigationMsg(String location, String  act_location){
//        Gson gson = new Gson();
//        ContentValues values = new ContentValues();
//        values.put("location", location);
//        values.put("act_location",act_location);
//        if (getNavigationInfo(routeNo) == null){
//            db.insert(helper.DB_NAME, "id", values);
//        }else {
//            db.update(helper.DB_NAME,values,"routeno=?",new String[]{routeNo});
//        }
//
//    }
//
//    /**
//     */
//    public void delUpDate(String routeno){
//        db.delete(helper.DB_NAME, "routeno=?", new String[]{routeno});
//    }
//
//
//    /**
//     *  View route history
//     */
//    public Direction getNavigationInfo(String routeNo){
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

//    public void clear(){
//        db.delete(helper.DB_NAME, "id>?", new String[]{"0"});
//    }
}


