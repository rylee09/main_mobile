package com.example.st.arcgiscss.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;

public class TypefaceUtil {
    /**
     * @return
     */
    public static String addHtmlRedFlag(String string) {
        return "<font color=\"red\">" + string + "</font>";
    }

    /**
     */
    public static String keywordMadeRed(String sourceString, String keyword) {
        String result = "";
        if (sourceString != null && !"".equals(sourceString.trim())) {
            if (keyword != null && !"".equals(keyword.trim())) {
                result = sourceString.replaceAll(keyword, "<font color=\"red\">" + keyword + "</font>");
            } else {
                result = sourceString;
            }
        }
        return result;
    }

    /**
     * <p>Replace the font of specified view and it's children</p>
     * @param root The root view.
     * @param fontPath font file path relative to 'assets' directory.
     */
    public static void replaceFont(@NonNull View root, String fontPath) {
        if (root == null || TextUtils.isEmpty(fontPath)) {
            return;
        }


        if (root instanceof TextView) { // If view is TextView or it's subclass, replace it's font
            TextView textView = (TextView)root;
            int style = Typeface.NORMAL;
            if (textView.getTypeface() != null) {
                style = textView.getTypeface().getStyle();
            }
            textView.setTypeface(createTypeface(root.getContext(), fontPath), style);
        } else if (root instanceof ViewGroup) { // If view is ViewGroup, apply this method on it's child views
            ViewGroup viewGroup = (ViewGroup) root;
            for (int i = 0; i < viewGroup.getChildCount(); ++i) {
                replaceFont(viewGroup.getChildAt(i), fontPath);
            }
        }
    }

    /**
     * <p>Replace the font of specified view and it's children</p>
     * @param context The view corresponding to the activity.
     * @param fontPath font file path relative to 'assets' directory.
     */
    public static void replaceFont(@NonNull Activity context, String fontPath) {
        replaceFont(getRootView(context),fontPath);
    }


    /*
     * Create a Typeface instance with your font file
     */
    public static Typeface createTypeface(Context context, String fontPath) {
        return Typeface.createFromAsset(context.getAssets(), fontPath);
    }


    public static View getRootView(Activity context)
    {
        return ((ViewGroup)context.findViewById(android.R.id.content)).getChildAt(0);
    }


//    <style name="AppTheme" parent="Theme.AppCompat.Light.DarkActionBar">
//    <!-- Customize your theme here. -->
//    <!-- Set system default typeface -->
//    <item name="android:typeface">monospace</item>
//    </style>
    public static void replaceSystemDefaultFont(@NonNull Context context, @NonNull String fontPath) {
        replaceTypefaceField("MONOSPACE", createTypeface(context, fontPath));
    }

    /**
     * <p>Replace field in class Typeface with reflection.</p>
     */
    private static void replaceTypefaceField(String fieldName, Object value) {
        try {
            Field defaultField = Typeface.class.getDeclaredField(fieldName);
            defaultField.setAccessible(true);
            defaultField.set(null, value);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
