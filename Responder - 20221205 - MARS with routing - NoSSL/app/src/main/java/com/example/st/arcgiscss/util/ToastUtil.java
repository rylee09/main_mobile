package com.example.st.arcgiscss.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

	public static void showToast(Context context, String message){
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}
	
	public static void showToast(Context context, int str){
		Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
	}
}
