package com.example.st.arcgiscss.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.example.st.arcgiscss.R;


public class RotationLoadingView extends View {

    private Context mContext;
    private boolean mIsAnimation;
    private int rotate;
    private final int ROTATE_STEP = 10;
    private int mWidth;
    private int mHeight;

    private Bitmap mForeBitmap;

    private Matrix mMatrix = new Matrix();

    private boolean mClockwise = true;
    private PaintFlagsDrawFilter mPaintFlagsDrawFilter;


    public RotationLoadingView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public RotationLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = mForeBitmap.getWidth();
        mHeight = mForeBitmap.getHeight();
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mForeBitmap.isRecycled() && mIsAnimation) {
            init();
        }

        if (!(mForeBitmap.isRecycled())) {
            mMatrix.setRotate(rotate, mForeBitmap.getWidth() / 2, mForeBitmap.getHeight() / 2);
            canvas.setDrawFilter(mPaintFlagsDrawFilter);
            canvas.drawBitmap(mForeBitmap, mMatrix, null);
            if (mIsAnimation) {
                rotate = rotate + ROTATE_STEP > 360 ? 0 : rotate + ROTATE_STEP;
                rotate = mClockwise ? rotate : -rotate;
                postInvalidate();
            }
        }
    }


    public void startRotationAnimation() {
        mIsAnimation = true;
        invalidate();
    }

    public void stopRotationAnimation() {
        mIsAnimation = false;
    }

    protected void onDeAttachedToWindow() {
        stopRotationAnimation();
        super.onDetachedFromWindow();
    }

    private void init() {
        mPaintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mForeBitmap = ((BitmapDrawable) mContext.getResources().getDrawable(getResID())).getBitmap();
        invalidate();
    }

    private int getResID() {
        return R.drawable.dengdai;
    }

}
