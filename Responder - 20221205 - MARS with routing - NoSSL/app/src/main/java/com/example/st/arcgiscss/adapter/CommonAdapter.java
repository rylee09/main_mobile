package com.example.st.arcgiscss.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Daxue on 2018/3/2.
 */
public abstract class CommonAdapter<T> extends BaseAdapter
{  
    protected LayoutInflater mInflater;
    protected Context mContext;
    protected List<T> mDatas;
    protected final int mItemLayoutId;  
  
    public CommonAdapter(Context context, List<T> mDatas, int itemLayoutId)
    {  
        this.mContext = context;  
        this.mInflater = LayoutInflater.from(mContext);
        this.mDatas = mDatas;  
        this.mItemLayoutId = itemLayoutId;  
    }  
  
    @Override
    public int getCount()  
    {  
        return mDatas.size();  
    }  
  
    @Override
    public T getItem(int position)  
    {  
        return mDatas.get(position);  
    }  
  
    @Override
    public long getItemId(int position)  
    {  
        return position;  
    }  
  
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {  
        final CommonViewHolder commonViewHolder = getViewHolder(position, convertView,
                parent);  
        convert(commonViewHolder, getItem(position));
        return commonViewHolder.getConvertView();
  
    }  
  
    public abstract void convert(CommonViewHolder helper, T item);
  
    private CommonViewHolder getViewHolder(int position, View convertView,
                                           ViewGroup parent)
    {  
        return CommonViewHolder.get(mContext, convertView, parent, mItemLayoutId,
                position);  
    }  
  
}  