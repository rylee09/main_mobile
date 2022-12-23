package com.example.st.arcgiscss.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PhotoBitmapUtils {
    /**
     */
    private static final String FILES_NAME = "/MyPhoto";
    /**
     */
    public static final String TIME_STYLE = "yyyyMMddHHmmssSSS";
    /**
     */
    public static final String IMAGE_TYPE = ".png";


    private PhotoBitmapUtils() {
    }


    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private static String getPhoneRootPath(Context context) {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                || !Environment.isExternalStorageRemovable()) {
            return context.getExternalCacheDir().getPath();
        } else {
            return context.getCacheDir().getPath();
        }
    }


    public static String getPhotoFileName(Context context) {
        File file = new File(getPhoneRootPath(context) + FILES_NAME);
        if (!file.exists()) {
            file.mkdirs();
        }
        SimpleDateFormat format = new SimpleDateFormat(TIME_STYLE, Locale.getDefault());
        Date date = new Date(System.currentTimeMillis());
        String time = format.format(date);
        String photoName = "/" + time + IMAGE_TYPE;
        return file + photoName;
    }


    public static String savePhotoToSD(Bitmap mbitmap, Context context) {
        FileOutputStream outStream = null;
        String fileName = getPhotoFileName(context);
        try {
            outStream = new FileOutputStream(fileName);
            mbitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
                if (mbitmap != null) {
                    mbitmap.recycle();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static Bitmap getCompressPhoto(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = 4;  // The size of the picture is set to one tenth of the original
        Bitmap bmp = BitmapFactory.decodeFile(path, options);
        options = null;
        return bmp;
    }


    public static String amendRotatePhoto(String originpath, Context context) {


        int angle = readPictureDegree(originpath);

        Bitmap bmp = getCompressPhoto(originpath);;

        Bitmap bitmap = rotaingImageView(angle, bmp);

        return savePhotoToSD(bitmap, context);
    }


    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }


    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        Bitmap returnBm = null;

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bitmap;
        }
        if (bitmap != returnBm) {
            bitmap.recycle();
        }
        return returnBm;
    }
}
