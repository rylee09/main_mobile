package com.example.st.arcgiscss.wiget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

@SuppressLint("AppCompatCustomView")
public class RadianImageView extends ImageView {

    private float[] rids = {10.0f,10.0f,10.0f,10.0f,10.0f,10.0f,10.0f,10.0f,};

    public RadianImageView(Context context) {
        super(context);
    }

    public RadianImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RadianImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    protected void onDraw(Canvas canvas) {
        Path path = new Path();
        int w = this.getWidth();
        int h = this.getHeight();
        path.addRoundRect(new RectF(0,0,w,h),rids, Path.Direction.CW);
        canvas.clipPath(path);
        super.onDraw(canvas);
    }
}