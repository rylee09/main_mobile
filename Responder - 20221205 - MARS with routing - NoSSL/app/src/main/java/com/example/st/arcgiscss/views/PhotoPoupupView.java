package com.example.st.arcgiscss.views;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.PopupWindow;

import com.example.st.arcgiscss.R;

import org.xutils.common.util.DensityUtil;


/**
 * Created by ludeyuan on 2017/12/21.
 */

public class PhotoPoupupView extends PopupWindow implements View.OnClickListener {

    private View mBottomView;
    private Context mContext;
    private PhotoPoupupView mPickerView;

    public PhotoPoupupView(final Context context, final PhotoSelectListener listener) {
        super(context);
        mContext = context;

        View mainView = LayoutInflater.from(context).inflate(R.layout.layout_photo_picker, null);
        mainView.setOnClickListener(this);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        setTouchable(true);
        setContentView(mainView);
        mBottomView = mainView.findViewById(R.id.photo_picker_layout_bottom);

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(context, 1f);
            }
        });


        mainView.findViewById(R.id.photo_picker_layout_album).setOnClickListener(

                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (null != listener) {
                            listener.selectFromAlbum();
                        }
                        dismiss();
                    }
                });


        mainView.findViewById(R.id.photo_picker_layout_camera).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (null != listener) {
                            listener.selectFromCamera();
                        }
                        dismiss();
                    }
                });
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);

        float botomHeight = mBottomView.getHeight()+0.0f;
        if(botomHeight<=0){
            botomHeight = DensityUtil.dip2px(80);
        }
        ObjectAnimator upAni = ObjectAnimator.ofFloat(mBottomView,
                "translationY", botomHeight, 0f);
        upAni.setInterpolator(new DecelerateInterpolator());
        upAni.setDuration(500);
        upAni.start();
    }

    private void backgroundAlpha(Context context, Float bgAlpha) {

        WindowManager.LayoutParams layoutParams = ((Activity)mContext).getWindow().getAttributes();
        layoutParams.alpha = bgAlpha;
        ((Activity) mContext).getWindow().setAttributes(layoutParams);
    }

    @Override
    public void onClick(View view) {
        dismiss();
    }

    /**

     */
    public interface PhotoSelectListener {

        /**
         *Choose photos from album
         */
        public void selectFromAlbum();

        /**
         *Select from camera
         */
        public void selectFromCamera();
    }
}
