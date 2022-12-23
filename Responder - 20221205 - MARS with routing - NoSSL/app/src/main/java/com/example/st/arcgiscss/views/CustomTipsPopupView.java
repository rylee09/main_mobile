package com.example.st.arcgiscss.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.st.arcgiscss.R;


public class CustomTipsPopupView extends PopupWindow implements View.OnClickListener {

    private View mView;
    private Context mContext;
    private PhotoPoupupView mPickerView;

    private TextView titleView, tip_content;

    public CustomTipsPopupView(final Context context, final SelectTipsPopupWindow.TipsSelectListener listener) {
        super(context);
        mContext = context;
        View mainView = LayoutInflater.from(context).inflate(R.layout.custom_tips, null);
        mainView.setOnClickListener(this);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setTouchable(true);
        setContentView(mainView);
        mView = mainView.findViewById(R.id.rl_tips);
        titleView = mainView.findViewById(R.id.titleView);
        tip_content = mainView.findViewById(R.id.tip_content);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btn_ok = mainView.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    public void initData(String title, String content) {
        changeViewData(title, content);
    }

    private void changeViewData(String title, String content) {
        titleView.setText(title);
        tip_content.setText(content);
    }
}
