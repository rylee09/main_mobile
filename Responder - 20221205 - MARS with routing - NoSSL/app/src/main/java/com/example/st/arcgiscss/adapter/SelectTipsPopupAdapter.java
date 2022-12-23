package com.example.st.arcgiscss.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.st.arcgiscss.R;
import com.example.st.arcgiscss.model.WayPoints;

import java.util.ArrayList;

public class SelectTipsPopupAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<WayPoints> tips;
    public SelectTipsPopupAdapter(Context context, ArrayList<WayPoints> tips) {
        this.context = context;
        this.tips = tips;
    }

    public ArrayList<WayPoints> getTips() {
        return tips;
    }

    public void setTips(ArrayList<WayPoints> tips) {
        this.tips = tips;
    }

    @Override
    public int getCount() {
        return tips.size();
    }

    @Override
    public Object getItem(int position) {
        return tips.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        WayPoints tip = tips.get(position);
        if(tip == null)
        {
            return null;
        }
        if(convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_selecttips, null);
        }
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.tv_waypoint_number = (TextView)convertView.findViewById(R.id.tv_waypoint_number);
        viewHolder.tv_waypoint = (TextView)convertView.findViewById(R.id.tv_waypoint);
        int i = position+1;
        viewHolder.tv_waypoint_number.setText("WayPoint"+i+" :");
        viewHolder.tv_waypoint.setText(tip.getName());
        return convertView;
    }
    private class ViewHolder
    {
        public TextView tv_waypoint_number;
        public TextView tv_waypoint;
    }
}
