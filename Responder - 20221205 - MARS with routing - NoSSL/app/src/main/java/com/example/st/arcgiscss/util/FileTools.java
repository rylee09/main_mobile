package com.example.st.arcgiscss.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class FileTools {

	private static FileTools fileUtils=null;
	private SharedPreferences pre;
	
	private FileTools(Context context){
		this.pre=context.getSharedPreferences("CBMap_config", Context.MODE_PRIVATE);
	}
	
	
	public static FileTools getInstance(Context context){
		if(fileUtils==null){
			fileUtils=new FileTools(context);			
		}
		return fileUtils;
	}

	public String readSharedPreference(String key){
		return pre.getString(key, null);
	}
	
	public int readInt(String key){
		return pre.getInt(key, 0);
	}
	
	public long readLong(String key){
		return pre.getInt(key, 0);
	}
	
	public float readFloat(String key){
		return pre.getFloat(key, 0f);
	}
	
	public boolean readBoolean(String key){
		return pre.getBoolean(key, false);
	}
	

	public void writeSharePreference(String key, String val){
		Editor edit= pre.edit();
		edit.putString(key, val);
		edit.commit();
	}
	

	public void writeBoolean(String key, boolean val){
		Editor edit= pre.edit();
		edit.putBoolean(key, val);
		edit.commit();		
	}
	

	public void writeFloat(String key, float val){
		Editor edit= pre.edit();
		edit.putFloat(key, val);
		edit.commit();		
	}
	

	public void writeInt(String key, int val){
		Editor edit= pre.edit();
		edit.putInt(key, val);
		edit.commit();		
	}
	

	public void writeInt(String key, long val){
		Editor edit= pre.edit();
		edit.putLong(key, val);
		edit.commit();		
	}
}
