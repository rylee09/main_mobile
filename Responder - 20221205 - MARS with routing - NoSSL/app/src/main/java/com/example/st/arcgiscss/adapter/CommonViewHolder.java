package com.example.st.arcgiscss.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.st.arcgiscss.R;


/**
 * Created by Daxue on 2018/3/2.
 */
public class CommonViewHolder
{  
    private final SparseArray<View> mViews;
    private int mPosition;  
    private View mConvertView;
  
    private CommonViewHolder(Context context, ViewGroup parent, int layoutId,
                             int position)
    {  
        this.mPosition = position;  
        this.mViews = new SparseArray<View>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,
                false);  
        // setTag  
        mConvertView.setTag(this);  
    }  
  
    /** 
     *
     *  
     * @param context 
     * @param convertView 
     * @param parent 
     * @param layoutId 
     * @param position 
     * @return 
     */  
    public static CommonViewHolder get(Context context, View convertView,
                                       ViewGroup parent, int layoutId, int position)
    {  
        if (convertView == null)  
        {  
            return new CommonViewHolder(context, parent, layoutId, position);
        }  
        return (CommonViewHolder) convertView.getTag();
    }  
  
    public View getConvertView()
    {  
        return mConvertView;  
    }  
  
    /** 
     * Get the control for the control through the Id of the control, if not, then join the views
     *  
     * @param viewId 
     * @return 
     */  
    public <T extends View> T getView(int viewId)
    {  
        View view = mViews.get(viewId);
        if (view == null)  
        {  
            view = mConvertView.findViewById(viewId);  
            mViews.put(viewId, view);  
        }  
        return (T) view;  
    }  
  
    /** 
     * Set the string for the TextView
     *  
     * @param viewId 
     * @param text 
     * @return 
     */  
    public CommonViewHolder setText(int viewId, String text)
    {  
        TextView view = getView(viewId);
        view.setText(text);  
        return this;  
    }  
  
    /** 
     * Set the image for ImageView
     *  
     * @param viewId 
     * @param drawableId 
     * @return 
     */  
    public CommonViewHolder setImageResource(int viewId, int drawableId)
    {  
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);  
  
        return this;  
    }  
  
    /**
     *  
     * @param viewId 
     * @param drawableId 
     * @return 
     */  
    public CommonViewHolder setImageBitmap(int viewId, Bitmap bm)
    {  
        ImageView view = getView(viewId);
        view.setImageBitmap(bm);  
        return this;  
    }  
  
    /**
     *  
     * @param viewId 
     * @param drawableId 
     * @return 
     */  
    public CommonViewHolder setImageByUrl(Context context, int viewId, String url)
    {
        Glide.with(context).load(url).error(R.drawable.ic_launcher).into((ImageView) getView(viewId));
        return this;  
    }  
  
    public int getPosition()  
    {  
        return mPosition;  
    }  
  
}  