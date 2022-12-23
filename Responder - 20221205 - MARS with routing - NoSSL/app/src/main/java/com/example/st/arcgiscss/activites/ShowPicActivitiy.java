package com.example.st.arcgiscss.activites;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.st.arcgiscss.R;



public class ShowPicActivitiy extends Activity{
    private LinearLayout ll_viewArea;
    private LinearLayout.LayoutParams parm;
    private ViewArea viewArea;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);

        setContentView(R.layout.acti_show_pic);

        ll_viewArea = (LinearLayout) findViewById(R.id.ll_viewArea);
        parm = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        viewArea = new ViewArea(ShowPicActivitiy.this,getIntent().getStringExtra("picPath"));    //Custom layout controls for initializing and storing custom imageViews

        ll_viewArea.addView(viewArea,parm);

        ll_viewArea.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

}
class ViewArea extends FrameLayout {
    private int imgDisplayW;
    private int imgDisplayH;
    private TouchView touchView;
    private DisplayMetrics dm;

    public ViewArea(Context context,String img) {
        super(context);


        imgDisplayW = ((Activity)context).getWindowManager().getDefaultDisplay().getWidth();
        imgDisplayH = ((Activity)context).getWindowManager().getDefaultDisplay().getHeight();

        touchView = new TouchView(context,imgDisplayW,imgDisplayH);

        getBitmap(context,img);


    }

    public void getBitmap(Context context, String uri) {
        Glide.with(context)
                .load(uri)
                .asBitmap()
                .centerCrop()
//                .override(150, 150)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                        touchView.setImageBitmap(bitmap);

                        int layout_w = imgDisplayW;
                        int layout_h = imgDisplayH;


                        LayoutParams params = new LayoutParams(layout_w,layout_h);
                        params.gravity =  Gravity.CENTER;
                        touchView.setLayoutParams(params);
                        ViewArea.this.addView(touchView);
                    }
                });
    }






//    public Bitmap returnBitMap(final String url){
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                URL imageurl = null;
//                try {
//                    imageurl = new URL(url);
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    HttpURLConnection conn = (HttpURLConnection)imageurl.openConnection();
//                    conn.setDoInput(true);
//                    conn.connect();
//                    InputStream is = conn.getInputStream();
//                    mybitmap = BitmapFactory.decodeStream(is);
//                    is.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//
//        return mybitmap;
//    }

}

class TouchView extends ImageView
{
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    static final int BIGGER = 3;
    static final int SMALLER = 4;
    private int mode = NONE;

    private float beforeLenght;
    private float afterLenght;
    private float scale = 0.04f;

    private int screenW;
    private int screenH;


    private int start_x;
    private int start_y;
    private int stop_x ;
    private int stop_y ;
    private TranslateAnimation trans;

    public TouchView(Context context,int w,int h)
    {
        super(context);
        this.setPadding(0, 0, 0, 0);
        screenW = w;
        screenH = h;
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return  (float)Math.sqrt(x * x + y * y);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                stop_x = (int) event.getRawX();
                stop_y = (int) event.getRawY();
                start_x = stop_x - this.getLeft();
                start_y = stop_y - this.getTop();

                if(event.getPointerCount()==2)
                    beforeLenght = spacing(event);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (spacing(event) > 10f) {
                    mode = ZOOM;
                    beforeLenght = spacing(event);
                }
                break;
            case MotionEvent.ACTION_UP:

                int disX = 0;
                int disY = 0;
                if(getHeight()<=screenH )//
                {
                    if(this.getTop()<0 )
                    {
                        disY = getTop();
                        this.layout(this.getLeft(), 0, this.getRight(), 0 + this.getHeight());

                    }
                    else if(this.getBottom()>=screenH)
                    {
                        disY = getHeight()- screenH+getTop();
                        this.layout(this.getLeft(), screenH-getHeight(), this.getRight(), screenH);
                    }
                }else{
                    int Y1 = getTop();
                    int Y2 = getHeight()- screenH+getTop();
                    if(Y1>0)
                    {
                        disY= Y1;
                        this.layout(this.getLeft(), 0, this.getRight(), 0 + this.getHeight());
                    }else if(Y2<0){
                        disY = Y2;
                        this.layout(this.getLeft(), screenH-getHeight(), this.getRight(), screenH);
                    }
                }
                if(getWidth()<=screenW)
                {
                    if(this.getLeft()<0)
                    {
                        disX = getLeft();
                        this.layout(0, this.getTop(), 0+getWidth(), this.getBottom());
                    }
                    else if(this.getRight()>screenW)
                    {
                        disX = getWidth()-screenW+getLeft();
                        this.layout(screenW-getWidth(), this.getTop(), screenW, this.getBottom());
                    }
                }else {
                    int X1 = getLeft();
                    int X2 = getWidth()-screenW+getLeft();
                    if(X1>0) {
                        disX = X1;
                        this.layout(0, this.getTop(), 0+getWidth(), this.getBottom());
                    }else if(X2<0) {
                        disX = X2;
                        this.layout(screenW-getWidth(), this.getTop(), screenW, this.getBottom());
                    }

                }

                while(getHeight()<100||getWidth()<100) {

                    setScale(scale,BIGGER);
                }

                if(disX!=0 || disY!=0)
                {
                    trans = new TranslateAnimation(disX, 0, disY, 0);
                    trans.setDuration(500);
                    this.startAnimation(trans);
                }
                mode = NONE;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:

                if (mode == DRAG) {
                    this.setPosition(stop_x - start_x, stop_y - start_y, stop_x + this.getWidth() - start_x, stop_y - start_y + this.getHeight());
                    stop_x = (int) event.getRawX();
                    stop_y = (int) event.getRawY();

                } else if (mode == ZOOM) {
                    if(spacing(event)>10f)
                    {
                        afterLenght = spacing(event);
                        float gapLenght = afterLenght - beforeLenght;
                        if(gapLenght == 0) {
                            break;
                        }

                        else if(Math.abs(gapLenght)>5f&&getWidth()>70)
                        {
                            if(gapLenght>0) {
                                this.setScale(scale,BIGGER);
                            }else {
                                this.setScale(scale,SMALLER);
                            }
                            beforeLenght = afterLenght;
                        }
                    }
                }
                break;
        }
        return true;
    }


    private void setScale(float temp,int flag) {

        if(flag==BIGGER) {

            this.setFrame(this.getLeft()-(int)(temp*this.getWidth()),
                    this.getTop()-(int)(temp*this.getHeight()),
                    this.getRight()+(int)(temp*this.getWidth()),
                    this.getBottom()+(int)(temp*this.getHeight()));
        }else if(flag==SMALLER){
            this.setFrame(this.getLeft()+(int)(temp*this.getWidth()),
                    this.getTop()+(int)(temp*this.getHeight()),
                    this.getRight()-(int)(temp*this.getWidth()),
                    this.getBottom()-(int)(temp*this.getHeight()));
        }
    }


    private void setPosition(int left,int top,int right,int bottom) {
        this.layout(left,top,right,bottom);
    }

}