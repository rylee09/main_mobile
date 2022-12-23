package com.example.st.arcgiscss.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.st.arcgiscss.R;
import com.example.st.arcgiscss.activites.NewMainActivity;
import com.example.st.arcgiscss.constant.MyApplication;
import com.example.st.arcgiscss.d3View.D3Fragment;
import com.example.st.arcgiscss.d3View.D3View;
import com.example.st.arcgiscss.model.User;
import com.example.st.arcgiscss.util.CacheUtils;
import com.example.st.arcgiscss.views.IosAlertDialog;

public class MoreFragment extends D3Fragment {
    @D3View(click = "onClick")
    RelativeLayout rl_submit_logs;

    @D3View
    TextView tv_version;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = setContentView(inflater, R.layout.frag_more);
        initView();
        initData();
        return view;
    }

    public void initView() {
    }


    public void initData() {
        //ZN - 20220709 standardise version number display
        tv_version.setText("App version: " + MyApplication.getInstance().getVersionNumber());
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_submit_logs:
                //ZN - 20220619 logging to external file
                Log.i("ON_CLICK", "[onClick] Submit Logs clicked");

                ((NewMainActivity)getActivity()).uploadLog();

                break;
        }
    }


    @Override
    public void onDetach() {
        Log.i("EVENT", "[MoreFragment] onDetach");
        super.onDetach();
    }

    @Override
    public void onPause() {
        Log.i("EVENT", "[MoreFragment] onPause");
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.i("EVENT", "[MoreFragment] onResume");
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        Log.i("EVENT", "[MoreFragment] onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i("EVENT", "[MoreFragment] onPause");
        super.onDestroy();
    }

}


