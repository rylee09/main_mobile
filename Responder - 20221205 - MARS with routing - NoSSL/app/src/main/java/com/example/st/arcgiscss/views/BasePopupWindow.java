package com.example.st.arcgiscss.views;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.LayoutRes;

import com.example.st.arcgiscss.R;


/**
 * Created by yukuoyuan on 2017/9/23.
 */
public abstract class BasePopupWindow extends PopupWindow implements View.OnClickListener {

    private Context context;

    private View vBgBasePicker;

    private LinearLayout llBaseContentPicker;

    public BasePopupWindow(Context context) {
        super(context);
        this.context = context;
        View view = View.inflate(context, R.layout.picker_base, null);
        vBgBasePicker = view.findViewById(R.id.v_bg_base_picker);
        llBaseContentPicker = (LinearLayout) view.findViewById(R.id.ll_base_content_picker);

        llBaseContentPicker.addView(View.inflate(context, getContentViews(), null));
        setContentView(view);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(0xffffff);
        setBackgroundDrawable(dw);
        this.setAnimationStyle(R.style.BottomDialogWindowAnim);
        initView(view);
        initListener();
        initData();
        vBgBasePicker.setOnClickListener(this);
    }


    protected abstract void initData();


    protected abstract void initListener();

    protected abstract void initView(View view);


    protected abstract int getContentViews();


    @Override
    public void showAsDropDown(View anchor) {
        if (Build.VERSION.SDK_INT >= 24) {
            Rect rect = new Rect();
            anchor.getGlobalVisibleRect(rect);
            int h = anchor.getResources().getDisplayMetrics().heightPixels - rect.bottom;
            setHeight(h);
        }
        super.showAsDropDown(anchor);
    }


    public void showAtLocation(@LayoutRes int layoutid) {
        showAtLocation(LayoutInflater.from(context).inflate(layoutid, null),
                Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.v_bg_base_picker:
                dismiss();
                break;
        }
    }
}

