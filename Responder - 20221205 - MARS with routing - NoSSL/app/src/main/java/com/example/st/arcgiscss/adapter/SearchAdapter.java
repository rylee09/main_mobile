package com.example.st.arcgiscss.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.st.arcgiscss.R;
import com.example.st.arcgiscss.model.LocationPointList;

import java.util.List;

public class SearchAdapter extends BaseAdapter {
    private Context context;
    private List<LocationPointList.LocationPoint> searchtext;
    public SearchAdapter(Context context, List<LocationPointList.LocationPoint> searchtext) {
        this.context = context;
        this.searchtext = searchtext;
    }

    @Override
    public int getCount() {
        return searchtext.size();
    }

    @Override
    public Object getItem(int position) {
        return searchtext.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LocationPointList.LocationPoint searchlocaiton = searchtext.get(position);
        if(searchlocaiton == null)
        {
            return null;
        }
        if(convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_search_text, null);
        }

       ViewHolder viewHolder = new ViewHolder();
        viewHolder.adapter_item_name = (TextView)convertView.findViewById(R.id.adapter_item_name);
        viewHolder.adapter_item_name.setText(searchlocaiton.getLocationName());
        return convertView;
    }
    private class ViewHolder
    {
        public TextView adapter_item_name;
    }
}
