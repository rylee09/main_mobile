package com.example.st.arcgiscss.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.example.st.arcgiscss.R;
import com.example.st.arcgiscss.activites.NewMainActivity;
import com.example.st.arcgiscss.d3View.D3Fragment;
import com.example.st.arcgiscss.d3View.D3View;
import com.example.st.arcgiscss.model.FuelTopUp;
import com.example.st.arcgiscss.util.CacheUtils;
import com.example.st.arcgiscss.views.IosAlertDialog;

public class TaskRequestFragment extends D3Fragment {
    @D3View(click = "onClick")
    RelativeLayout rl_top_up_fuel;

    @D3View(click = "onClick")
    ImageView iv_top_up_fuel;
    @D3View
    TextView tv_fuel_status;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("EVENT", "[TaskRequestFragment] onCreateView");
        View view = setContentView(inflater, R.layout.frag_task_request);
        initView();
        initData();

        //ZN - 20210707
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(myBroadcastReceiver,
                new IntentFilter("NewFuelTopUpMsgReceiver"));

        return view;
    }

    public void initView() {
    }


    public void initData() {
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_top_up_fuel:
                if (tv_fuel_status.getText().equals("")) {
                    showTopUpRequestConfirmView(NewMainActivity.SYSTEM_PENDING, "Top Up Fuel");
                } else if (tv_fuel_status.getText().equals("Approved")) {
                    showTopUpDoneConfirmView(NewMainActivity.SYSTEM_READY, "System Ready");
                } else if (tv_fuel_status.getText().equals("Rejected")) {
                    tv_fuel_status.setText("");
                    ((NewMainActivity)getActivity()).callServerTopupfuel("N");
                    //unregister topupfuel listener
                    ((NewMainActivity)getActivity()).unregisterTopUpFuelListener();
                    iv_top_up_fuel.setImageResource(R.mipmap.topupfuel);
                    rl_top_up_fuel.setEnabled(true);
                } else {
                    //do nothing
                }

                break;
        }
    }

    //ZN - 20210707
    private void showTopUpRequestConfirmView(String status, String remarks){

        new IosAlertDialog(getContext()).builder().setTitle("Top Up Fuel").setMsg("Confirm Request for Top Up Fuel ?").setCancelable(false).setNegativeButton("No", null).setPositiveButton("YES", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CacheUtils.getUser(getContext()).setSystemReady(status);
                ((NewMainActivity)getActivity()).callServerTopupfuel("P");
                ((NewMainActivity)getActivity()).registerTopUpFuelListener();
                tv_fuel_status.setText("Pending");
                tv_fuel_status.setTextColor(Color.parseColor("#FFBF00"));
                iv_top_up_fuel.setImageResource(R.mipmap.topupfuel_amber);
                rl_top_up_fuel.setEnabled(false);
            }
        }).show();
    }

    //ZN - 20210707
    private void showTopUpDoneConfirmView(String status, String remarks){

        new IosAlertDialog(getContext()).builder().setTitle("Top Up Fuel").setMsg("Confirm Top Up Fuel Completed ?").setCancelable(false).setNegativeButton("No", null).setPositiveButton("YES", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CacheUtils.getUser(getContext()).setSystemReady(status);
                ((NewMainActivity)getActivity()).updateSystemReadyStatus(status);
                ((NewMainActivity)getActivity()).callServerSystemReady(status, remarks);
                ((NewMainActivity)getActivity()).callServerTopupfuel("N");
                ((NewMainActivity)getActivity()).unregisterTopUpFuelListener();
                tv_fuel_status.setText("");
                iv_top_up_fuel.setImageResource(R.mipmap.topupfuel);
                rl_top_up_fuel.setEnabled(true);
            }
        }).show();
    }

    //ZN - 20210707
    private final BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, android.content.Intent intent) {
            Log.i("onReceive", "[myBroadcastReceiver] top up fuel onReceive");
            FuelTopUp status = (FuelTopUp) intent.getExtras().getSerializable("top_up_fuel_status");
            updateTopUpFuelStatus(status);
        }
    };

    //ZN - 20210707
    private void updateTopUpFuelStatus(FuelTopUp status) {
        if (status.getApprovalStatus().equals(NewMainActivity.SYSTEM_PENDING)) {
            //pending HQ response, do nothing
            return;
        } else if (status.getApprovalStatus().equals("Y")) {
            //approved
            //tv_fuel.setText("Tap When Refuelling Done");
            tv_fuel_status.setText("Approved");
            tv_fuel_status.setTextColor(Color.GREEN);
            iv_top_up_fuel.setImageResource(R.mipmap.topupfuel_green);
            //enable button press
            rl_top_up_fuel.setEnabled(true);

            //ZN - 20210819 update main status page
            ((NewMainActivity)getActivity()).updateSystemReadyStatus(NewMainActivity.SYSTEM_NOT_READY);

        } else {
            //rejected
            //tv_fuel.setText("Tap To Go Back");
            tv_fuel_status.setText("Rejected");
            tv_fuel_status.setTextColor(Color.RED);
            iv_top_up_fuel.setImageResource(R.mipmap.topupfuel_red);
            //enable button press
            rl_top_up_fuel.setEnabled(true);
        }
    }

    @Override
    public void onDetach() {
        Log.i("EVENT", "[TaskRequestFragment] onDetach");
        super.onDetach();
    }

    @Override
    public void onPause() {
        Log.i("EVENT", "[TaskRequestFragment] onPause");
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.i("EVENT", "[TaskRequestFragment] onResume");
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        Log.i("EVENT", "[TaskRequestFragment] onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i("EVENT", "[TaskRequestFragment] onPause");
        super.onDestroy();
    }

}


