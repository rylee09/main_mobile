
package com.example.st.arcgiscss.util;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


public class HideSoftInputHelperTool {

	public static void hide(Activity ctx, MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {

			View v = ctx.getCurrentFocus();
			if (isShouldHideInput(v, ev)) {
				hideSoftInput(ctx, v.getWindowToken());
			}
		}

	}

	private static boolean isShouldHideInput(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {
			int[] l = { 0, 0 };
			v.getLocationInWindow(l);
			int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
			if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	private static void hideSoftInput(Context ctx, IBinder token) {
		if (token != null) {
			InputMethodManager im = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

};
