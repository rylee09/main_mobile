package com.example.st.arcgiscss.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.example.st.arcgiscss.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DBOpenHelper {
    private final int BUFFER_SIZE = 400000;
    public static final String DB_NAME = "location.db";
    public static final String PACKAGE_NAME = "com.example.st.arcgiscss";
    public static final String DB_PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() +"/"
            + PACKAGE_NAME+ "/databases";
    private Context context;

    public DBOpenHelper(Context context) {
        this.context = context;
    }


    public SQLiteDatabase openDatabase() {
        try {
            File myDataPath = new File(DB_PATH);
            if (!myDataPath.exists())
            {
                myDataPath.mkdirs();
            }
            String dbfile=myDataPath+"/"+DB_NAME;
            if (!(new File(dbfile).exists())) {
                InputStream is = context.getResources().openRawResource(
                        R.raw.location);
                FileOutputStream fos = new FileOutputStream(dbfile);
                byte[] buffer = new byte[BUFFER_SIZE];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile,
                    null);
            return db;
        } catch (FileNotFoundException e) {
            Log.e("Database", "File not found");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Database", "IO exception");
            e.printStackTrace();
        }
        return null;
    }






}
