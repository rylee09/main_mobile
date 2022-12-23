package com.example.st.arcgiscss.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
	
	/*
	 * create commute table
	 */
	public static String CREATE_COMMUTE="create table Commute("
			+ "id integer primary key autoincrement,"
			+ "commute_id integer,"
			+ "address text,"
			+ "longitude real,"
			+ "latitude real)";
	public static String CREATE_CHAT_INFO="create table Notice("
			+ "id integer primary key autoincrement,"
			+ "title text,"
			+ "content text,"
			+ "type integer,"
			+ "time text,"
			+ "from_user text,"
			+ "nickname_from text,"
			+ "from_userId text,"
			+ "to_user text,"
			+ "status integer,"
			+ "location text)";
	
	public static String CREATE_USER_COORDINATE="create table User_Coordinate(" +
			"id integer primary key autoincrement," +
			"user_id text," +
			"imei text," +
			"user_state text," +
			"latlon text," +
			"write_time text," +
			"speed text," +
			"heading text, " +
			"gps_time text)";
	
	public static String CREATE_MAP_REPORT = "create table Map_Report("
			+ "id integer primary key autoincrement,"
			+ "user_id text,"
			+ "report_id integer,"
			+ "latitude real,"
			+ "longitude real,"
			+ "details text,"
			+"report_img blob,"
			+ "save_time text)";
	
	public static String CREATE_CLIENT_USER = "create table Client_User(" +
			"id integer primary key autoincrement," +
			"name text)";
	public static String CREATE_USER_FRIEND = "create table User_Friend(" +
			"id integer primary key autoincrement," +
			"name text," +
			"user_id integer)";

	public static String CREATE_FRIEND_MSG = "create table Friend_Msg(" +
			"id integer primary key autoincrement," +
			"content text," +
			"state integer," +
			"timestamp integer," +
			"friend_id integer," +
			"user_id integer," +
			"msg_type integer," +
			"fromname text," +
			"toname text)";
//	public static String CREATE_FRIEND_UNDELIVER = "create table Friend_Undeliver(" +
//			"id integer primary key autoincrement," +
//			"content text," +
//			"timestamp integer," +
//			"friend_id integer," +
//			"user_id integer)";
	
	private Context context;
	
	public DataBaseHelper(Context context){
		this(context, "arcgiscss.db", null, 1);
	}
	
	public DataBaseHelper(Context context, String name, CursorFactory factory,
                          int version) {
		super(context, name, factory, version);
		this.context=context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_COMMUTE);
		db.execSQL(CREATE_CHAT_INFO);
		db.execSQL(CREATE_MAP_REPORT);
		db.execSQL(CREATE_USER_COORDINATE);
		db.execSQL(CREATE_CLIENT_USER);
		db.execSQL(CREATE_USER_FRIEND);
		db.execSQL(CREATE_FRIEND_MSG);
//		db.execSQL(CREATE_FRIEND_UNDELIVER);
//		Toast.makeText(context, "Table created successfully", Toast.LENGTH_SHORT).show();
	}

	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		onCreate(db);

	}

	
	
}
