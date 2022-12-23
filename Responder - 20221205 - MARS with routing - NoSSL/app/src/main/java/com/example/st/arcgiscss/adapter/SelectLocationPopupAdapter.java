package com.example.st.arcgiscss.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.st.arcgiscss.R;
import com.example.st.arcgiscss.model.LocationPointList;

import java.util.List;

public class SelectLocationPopupAdapter extends BaseAdapter {
    private Context context;
    private List<LocationPointList.LocationPoint> locationInfos;
    public SelectLocationPopupAdapter(Context context, List<LocationPointList.LocationPoint> locationInfos) {
        this.context = context;
        this.locationInfos = locationInfos;
    }

    @Override
    public int getCount() {
        return locationInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return locationInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LocationPointList.LocationPoint locationInfo = locationInfos.get(position);
        if(locationInfo == null)
        {
            return null;
        }
        if(convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_select_locations, null);
        }
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.tv_locaiton = (TextView)convertView.findViewById(R.id.tv_locaiton);
        viewHolder.iv_select_type = (ImageView)convertView.findViewById(R.id.iv_select_type);
        viewHolder.tv_locaiton.setText(locationInfo.getLocationName());
        if (locationInfo.getSelectStatus().equals("0")){
            Glide.with(this.context).load(R.mipmap.location_unselect).into(viewHolder.iv_select_type);
        }else {
            Glide.with(this.context).load(R.mipmap.location_select).into(viewHolder.iv_select_type);
        }
        return convertView;
    }
    private class ViewHolder
    {
        public TextView tv_locaiton;
        public ImageView iv_select_type;
    }
}
