package com.example.st.arcgiscss.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.st.arcgiscss.R;

import java.util.List;

public class MTipsAdapter extends BaseAdapter {
    private Context context;
    private List<String> tips;
    public MTipsAdapter(Context context, List<String> tips) {
        this.context = context;
        this.tips = tips;
    }

    public List<String> getTips() {
        return tips;
    }

    public void setTips(List<String> tips) {
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

        String tip = tips.get(position);
        if(tip == null)
        {
            return null;
        }
        if(convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_m_tips, null);
        }


        ViewHolder viewHolder = new ViewHolder();
        viewHolder.adapter_item_name = (TextView)convertView.findViewById(R.id.adapter_item_name);
        viewHolder.adapter_item_name.setText(tip);
        return convertView;
    }
    private class ViewHolder
    {
        public TextView adapter_item_name;
    }
}
