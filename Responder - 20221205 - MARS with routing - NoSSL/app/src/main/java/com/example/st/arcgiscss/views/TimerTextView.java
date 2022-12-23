package com.example.st.arcgiscss.views;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.example.st.arcgiscss.constant.MyApplication;


public class TimerTextView extends AppCompatTextView implements Runnable{

    public TimerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    private long  mmin, msecond;
    private boolean run=false;

    public void setTimes(long[] times) {
        mmin = times[0];
        msecond = times[1];
    }

    /**
     */
    private void ComputeTime() {
        msecond--;
        if (msecond < 0) {
            mmin--;
            msecond = 59;
            if (mmin < 0) {
                mmin = 59;
            }
        }
    }

    public boolean isRun() {
        return run;
    }

    public void beginRun() {
        this.run = true;
        run();
    }

    public void stopRun(){
        this.run = false;
    }


    @Override
    public void run() {
        if(run){
            ComputeTime();
            String strTime= mmin+"M : "+msecond+"S";
            if (msecond<10){
                strTime= mmin+"M : 0"+msecond+"S";
            }
            this.setText(strTime);
            if (mmin==0&&msecond==0){
                Intent intent = new Intent("StartTimeReceiver");
                MyApplication.getInstance().sendBroadcast(intent);
            }
            postDelayed(this, 1000);
        }else {
            removeCallbacks(this);
        }
    }

}