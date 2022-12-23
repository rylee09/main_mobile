package com.example.st.arcgiscss.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * 
 * @author zhy
 * 
 */
public class DensityUtils {
	private DensityUtils() {
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static int dp2px(Context context, float dpVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dpVal, context.getResources().getDisplayMetrics());
	}

	/**
	 * 
	 * @param context

	 * @return
	 */
	public static int sp2px(Context context, float spVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				spVal, context.getResources().getDisplayMetrics());
	}

	/**
	 * 
	 * @param context
	 * @param pxVal
	 * @return
	 */
	public static float px2dp(Context context, float pxVal) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (pxVal / scale);
	}

	/**
	 * @param pxVal
	 * @return
	 */
	public static float px2sp(Context context, float pxVal) {
		return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
	}


	public static int autoPaintTextSize(Context context) {
		float RATIO = 0;

		DisplayMetrics displayMetrics = context.getResources()
				.getDisplayMetrics();
		int screenWidth = displayMetrics.widthPixels;
		int screenHeight = displayMetrics.heightPixels;
		float ratioWidth = (float) screenWidth / 480;
		float ratioHeight = (float) screenHeight / 800;
		RATIO = Math.min(ratioWidth, ratioHeight);

		return Math.round(20 * RATIO);
	}
}
