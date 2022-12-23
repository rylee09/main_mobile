package com.example.st.arcgiscss.util;

import android.content.Context;

public class DisplayUtil {
    /** 

     *  
     * @param pxValue 

     * @return 
     */  
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
  
    /** 

     *  
     * @param dipValue 

     * @return 
     */  
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dipValue * scale + 0.5f);  
    }  
  
    /**
     *  
     * @param pxValue
     * @return 
     */  
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (pxValue / fontScale + 0.5f);  
    }  
  
    /** 

     *  
     * @param spValue
     */  
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (spValue * fontScale + 0.5f);  
    }  
}  