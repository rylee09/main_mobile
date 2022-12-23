package com.example.st.arcgiscss.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.st.arcgiscss.R;
import com.example.st.arcgiscss.model.RouteWay;


public class TimeTipsPopupView  extends PopupWindow implements View.OnClickListener{

    private View mView;
    private Context mContext;
    private PhotoPoupupView mPickerView;

    private TextView tv_start,tv_end;

    private RouteWay routeWay;

    public TimeTipsPopupView(final Context context, final SelectTipsPopupWindow.TipsSelectListener listener) {
        super(context);
        mContext = context;
        View mainView = LayoutInflater.from(context).inflate(R.layout.picker_time, null);
        mainView.setOnClickListener(this);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setTouchable(true);
        setContentView(mainView);
        mView = mainView.findViewById(R.id.rl_tips);
        tv_start = mainView.findViewById(R.id.tv_start);
        tv_end = mainView.findViewById(R.id.tv_end);

        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


//        mainView.findViewById(R.id.tv_tips_yes).setOnClickListener(
//
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if (null != listener) {
//                            listener.selectFromYES();
//                        }
//                        dismiss();
//                    }
//                });

    }


    @Override
    public void onClick(View view) {
        dismiss();
    }



    public interface TipsSelectListener {

        public void selectFromYES();

    }

    public void initData(String start,String end){
        changeViewData(start,end);
    }

    private void changeViewData(String start,String end)
    {
        tv_start.setText(start);
        tv_end.setText(end);

    }

}