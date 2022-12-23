package com.example.st.arcgiscss.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.st.arcgiscss.R;
import com.example.st.arcgiscss.adapter.SelectTipsPopupAdapter;
import com.example.st.arcgiscss.model.RouteWay;
import com.example.st.arcgiscss.model.WayPoints;
import com.example.st.arcgiscss.util.Utility;

import java.util.ArrayList;

public class SelectTipsPopupWindow extends PopupWindow implements View.OnClickListener{

    private View mView;
    private Context mContext;
    private PhotoPoupupView mPickerView;

    private TextView tv_to_address,tv_from_address;
    private TextView tv_waypoint;
    private RouteWay routeWay;
    private ListView waypointView;
    private SelectTipsPopupAdapter adapter;
    private ArrayList<WayPoints> wayPoints = new ArrayList<>();

    public SelectTipsPopupWindow(final Context context, final TipsSelectListener listener) {
        super(context);
        mContext = context;
        View mainView = LayoutInflater.from(context).inflate(R.layout.picker_tips, null);
        mainView.setOnClickListener(this);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setTouchable(true);
        setContentView(mainView);
        mView = mainView.findViewById(R.id.rl_tips);
        tv_to_address = mainView.findViewById(R.id.tv_to_address);
        waypointView = mainView.findViewById(R.id.lv_waypoints);
        adapter = new SelectTipsPopupAdapter(mContext,wayPoints);
        waypointView.setAdapter(adapter);
        tv_from_address = mainView.findViewById(R.id.tv_from_address);
        waypointView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return true;
            }
        });
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        mainView.findViewById(R.id.tv_tips_yes).setOnClickListener(

                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (null != listener) {
                            listener.selectFromYES();
                        }
                        dismiss();
                    }
                });

    }


    @Override
    public void onClick(View view) {
        dismiss();
    }



    public interface TipsSelectListener {

        public void selectFromYES();

    }

    public void initData(RouteWay way){
       this.routeWay = way;
        changeViewData();
    }

    private void changeViewData()
    {
        tv_from_address.setText(routeWay.getFromAddress());
        tv_to_address.setText(routeWay.getToAddress());
//        String waypointStr = "";
//        for (WayPoints waypoint:routeWay.getWayPoints()){
//            waypointStr += waypoint.getName()+", ";
//        }
//        if (!waypointStr.equals("")){
//            waypointStr = waypointStr.substring(0,waypointStr.length()-2);
//        }
//        tv_waypoint.setText("WayPoint:"+ waypointStr);
//        adapter = new SelectTipsPopupAdapter(mContext,routeWay.getWayPoints());
//        waypointView.setAdapter(adapter);
//        wayPoints = routeWay.getWayPoints();
//        wayPoints = routeWay.getWayPoints();
//        adapter.notifyDataSetChanged();
        adapter = new SelectTipsPopupAdapter(mContext,routeWay.getWayPoints());
        waypointView.setAdapter(adapter);
        Utility.setListViewHeightBasedOnChildren(waypointView);
    }

}
