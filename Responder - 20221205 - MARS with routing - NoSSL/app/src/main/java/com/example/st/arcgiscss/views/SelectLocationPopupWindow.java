package com.example.st.arcgiscss.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.example.st.arcgiscss.R;
import com.example.st.arcgiscss.adapter.SelectLocationPopupAdapter;
import com.example.st.arcgiscss.model.LocationPointList;
import com.example.st.arcgiscss.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class SelectLocationPopupWindow extends PopupWindow implements View.OnClickListener{

    private View mView;
    private Context mContext;
    private ListView locationsView;
    private List<LocationPointList.LocationPoint> mlocations = new ArrayList<>();
    private SelectLocationPopupAdapter adapter;

    private LocationPointList.LocationPoint selectInfo;

    public SelectLocationPopupWindow(final Context context, final LocationsSelectListener listener) {
        super(context);
        mContext = context;
        View mainView = LayoutInflater.from(context).inflate(R.layout.picker_locations, null);
        mainView.setOnClickListener(this);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        setTouchable(true);
        setContentView(mainView);
        mView = mainView.findViewById(R.id.rl_locations);
        locationsView = mainView.findViewById(R.id.lv_locaitons);
        adapter = new SelectLocationPopupAdapter(mContext,mlocations);
        locationsView.setAdapter(adapter);
        locationsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i <mlocations.size(); i++) {
                    LocationPointList.LocationPoint locationInfo = mlocations.get(i);
                    locationInfo.setSelectStatus("0");
                }
                selectInfo = mlocations.get(position);
                selectInfo.setSelectStatus("1");
                adapter.notifyDataSetChanged();
            }
        });

        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mainView.findViewById(R.id.tv_tips_yes).setOnClickListener(

                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (null != listener&& selectInfo != null) {
                            listener.selectFromYES(selectInfo);
                        }
                        dismiss();
                    }
                });

    }


    @Override
    public void onClick(View view) {
        dismiss();
    }



    public interface LocationsSelectListener {

        public void selectFromYES(LocationPointList.LocationPoint locationInfo);

    }

    public void initData(List<LocationPointList.LocationPoint> locations){

        mlocations.addAll(locations);
        mlocations.add(new LocationPointList.LocationPoint("Search More","0"));
        changeViewData();
    }

    private void changeViewData()
    {
        adapter = new SelectLocationPopupAdapter(mContext,mlocations);
        locationsView.setAdapter(adapter);
        Utility.setListViewHeightBasedOnChildren(locationsView);
    }

}
