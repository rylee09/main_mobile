package com.example.st.arcgiscss.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

public class SelfDialog extends AlertDialog {


    private String mTitle;
    private String mContent;
    private String mYes;
    private String mNo;
    private Context mContxt;
    private static SelfDialog mInstance;


    public static SelfDialog getInstance(Context context) {
        if (mInstance == null) {
            synchronized (SelfDialog.class) {
                if (mInstance == null) {
                    mInstance = new SelfDialog(context);
                }
            }
        }
        return mInstance;
    }
    public SelfDialog(Context context,String mTitle,String mContent,String mYes,String mNo) {
        super(context);
        this.mContxt=context;
        this.mContent=mContent;
        this.mTitle=mTitle;
        this.mYes=mYes;
        this.mNo=mNo;
    }
    public static SelfDialog getInstanceYesAndNo(Context context,String mTitle,String mContent,String mYes,String mNo) {
        if (mInstance == null) {
            synchronized (SelfDialog.class) {
                if (mInstance == null) {
                    mInstance = new SelfDialog(context, mTitle,mContent ,mYes,mNo );
                }
            }
        }
        return mInstance;
    }
    public SelfDialog(Context context,String mTitle,String mContent,String mYes) {
        super(context);
        this.mContent=mContent;
        this.mTitle=mTitle;
        this.mYes=mYes;
    }
    public static SelfDialog getInstanceYes(Context context,String mTitle,String mContent,String mYes) {
        if (mInstance == null) {
            synchronized (SelfDialog.class) {
                if (mInstance == null) {
                    mInstance = new SelfDialog(context, mTitle,mContent ,mYes );
                }
            }
        }
        return mInstance;
    }
    public void buildYesAndNoDialog(){
        Builder builder = new Builder(mContxt);
        builder.setTitle(mTitle);
        builder.setMessage(mContent);
        builder.setNegativeButton(mYes, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(mContxt, mNo, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setPositiveButton(mYes, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(mContxt, mYes, Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void buildYesDialog(){
        Builder builder = new Builder(mContxt);
        builder.setTitle(mTitle);
        builder.setMessage(mContent);
        builder.setPositiveButton(mYes, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(mContxt, mYes, Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    protected SelfDialog(Context context) {
        super(context);
    }

}
