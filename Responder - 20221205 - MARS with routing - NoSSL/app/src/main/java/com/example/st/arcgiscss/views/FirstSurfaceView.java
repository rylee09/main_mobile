package com.example.st.arcgiscss.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.st.arcgiscss.constant.MyApplication;


public class FirstSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    //SurfaceHolder
    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    private boolean mIsDrawing;
    private Paint mPaint;
    private Path mPath;


    public FirstSurfaceView(Context context) {
        super(context);
        initView();
    }

    public FirstSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public FirstSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mHolder = getHolder();

        mHolder.addCallback(this);

        mPath = new Path();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(6);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);

        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mIsDrawing = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsDrawing = false;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        while (mIsDrawing) {
            try {
                mCanvas = mHolder.lockCanvas();
                drawIt(mCanvas);

            } catch (Exception e) {
            } finally {
                if (mCanvas != null)
                    mHolder.unlockCanvasAndPost(mCanvas);
            }


            long end = System.currentTimeMillis();
            if (end - start < 100) {
                try {
                    Thread.sleep(100 - end + start);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void drawIt(Canvas canvas) {
        canvas.drawBitmap(MyApplication.getInstance().getShotMap(),0,0,mPaint);
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    /**
     *
     * @return true
     */
    public boolean reDraw() {
        mPath.reset();
        return true;
    }

    public Bitmap getBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawIt(canvas);
        return bitmap;
    }
}