package com.example.st.arcgiscss.util;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class AnimLineLayout {

    private Context context;
    private LinearLayout ll_menmue;

    public AnimLineLayout(Context context, LinearLayout ll_menmue) {
        this.context = context;
        this.ll_menmue = ll_menmue;
    }

    public void showBottomLayout() {
        TypeEvaluator<ViewGroup.LayoutParams> evaluator = new HeightEvaluator();

        ViewGroup.LayoutParams start = new ViewGroup.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        ViewGroup.LayoutParams end = new ViewGroup.LayoutParams(DisplayUtil.dip2px(context,45), ViewGroup.LayoutParams.MATCH_PARENT);
        ValueAnimator animator = ObjectAnimator.ofObject(ll_menmue, "layoutParams", evaluator, start, end);

        AnimatorSet set = new AnimatorSet();
        set.play(animator);
        set.setDuration(300);
        set.start();
    }


    public void hideBottomLayout() {
        TypeEvaluator<ViewGroup.LayoutParams> evaluator = new HeightEvaluator();
        ViewGroup.LayoutParams start = new ViewGroup.LayoutParams(ll_menmue.getWidth(),ViewGroup.LayoutParams.MATCH_PARENT);
        ViewGroup.LayoutParams end = new ViewGroup.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        ValueAnimator animator = ObjectAnimator.ofObject(ll_menmue, "layoutParams", evaluator, start, end);
        AnimatorSet set = new AnimatorSet();
        set.play(animator);
        set.setDuration(300);
        set.start();
    }

    class HeightEvaluator implements TypeEvaluator<ViewGroup.LayoutParams> {

        @Override
        public ViewGroup.LayoutParams evaluate(float fraction, ViewGroup.LayoutParams startValue, ViewGroup.LayoutParams endValue) {
            ViewGroup.LayoutParams params = ll_menmue.getLayoutParams();
            params.width = (int) (startValue.width + fraction * (endValue.width - startValue.width));
            return params;
        }
    }
}
