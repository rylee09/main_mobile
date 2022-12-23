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
import com.example.st.arcgiscss.d3View.D3Fragment;
import com.example.st.arcgiscss.d3View.D3View;
import com.example.st.arcgiscss.model.User;
import com.example.st.arcgiscss.util.CacheUtils;
import com.example.st.arcgiscss.views.IosAlertDialog;

public class SystemReadinessFragment extends D3Fragment {
    @D3View(click = "onClick")
    LinearLayout ll_vaccinated,ll_schedule,ll_information;
    @D3View(click = "onClick")
    RelativeLayout rl_last_swap, rl_swap_veh, rl_veh_down, rl_pump_tyre, rl_parade_state, rl_send_out;

    @D3View(click = "onClick")
    ImageView iv_back_left;
    @D3View
    TextView tv_nowtime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = setContentView(inflater, R.layout.frag_system_read);
        initView();
        initData();
        return view;
    }

    public void initView() {
    }


    public void initData() {
    }

    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.iv_back_left:
//                ((NewMainActivity)getActivity()).switchFragment(NewMainActivity.FRAG_HOME);
//                break;
            case R.id.rl_parade_state:
//                ((NewMainActivity)getActivity()).callServerSystemReady(NewMainActivity.SYSTEM_NOT_READY, "Swap Vehicle");

                //ZN - 20220619 logging to external file
                Log.i("ON_CLICK", "[onClick] Parade Task clicked");

                //ZN - 20211201 change text to Parade Task
                showSystemReadyConfirmView(NewMainActivity.SYSTEM_NOT_READY, "Low Fuel");
                break;
            case R.id.rl_swap_veh:
//                ((NewMainActivity)getActivity()).callServerSystemReady(NewMainActivity.SYSTEM_NOT_READY, "Swap Vehicle");

                //ZN - 20220619 logging to external file
                Log.i("ON_CLICK", "[onClick] Swap vehicle clicked");

                showSystemReadyConfirmView(NewMainActivity.SYSTEM_NOT_READY, "Low Logistics");
                break;
            case R.id.rl_veh_down:
//                ((NewMainActivity)getActivity()).callServerSystemReady(NewMainActivity.SYSTEM_NOT_READY, "Vehicle Down");

                //ZN - 20220619 logging to external file
                Log.i("ON_CLICK", "[onClick] Vehicle Down clicked");

                showSystemReadyConfirmView(NewMainActivity.SYSTEM_NOT_READY, "Vehicle Down");
                break;
            case R.id.rl_pump_tyre:
//                ((NewMainActivity)getActivity()).callServerSystemReady(NewMainActivity.SYSTEM_NOT_READY, "Pump Tyre");

                //ZN - 20220619 logging to external file
                Log.i("ON_CLICK", "[onClick] Pump tyre clicked");

                showSystemReadyConfirmView(NewMainActivity.SYSTEM_NOT_READY, "Pump Tyre");
                break;
            //ZN - 20220224 new send-out feature
            case R.id.rl_send_out:

                //ZN - 20220619 logging to external file
                Log.i("ON_CLICK", "[onClick] Send out clicked");

                showSendOutSelectionView();
                break;
        }
    }

    //ZN - 20210630
    private void showSystemReadyConfirmView(String status, String remarks){
        Log.i("EVENT", "inside showSystemReadyConfirmView");
        new IosAlertDialog(getContext()).builder().setTitle("System Readiness").setMsg("Confirm Report System Not Ready ?").setCancelable(false).setNegativeButton("No", null).setPositiveButton("YES", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User u = CacheUtils.getUser(getActivity());
                u.setSystemReady(status);
                //CacheUtils.getUser(getActivity()).setSystemReady(status);
                ((NewMainActivity)getActivity()).updateSystemReadyStatus(status);
                ((NewMainActivity)getActivity()).callServerSystemReady(status, remarks);
                ((NewMainActivity)getActivity()).switchFragment(NewMainActivity.FRAG_HOME);

                //ZN - 20220619 logging to external file
                Log.i("ON_CLICK", "[showSystemReadyConfirmView] System readiness updated: " + remarks + " - status: " + status);
            }
        }).show();
    }

    //ZN - 20220224 new send-out feature
    private void showSendOutSelectionView() {
        new IosAlertDialog(getContext()).builder().setTitle("System Readiness").setMsg("Confirm Report Send Out ?").setCancelable(false).setNegativeButton("No", null).setPositiveButton("YES", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NewMainActivity)getActivity()).switchFragment(NewMainActivity.FRAG_SEND_OUT_EVAC);

                //ZN - 20220619 logging to external file
                Log.i("ON_CLICK", "[showSendOutSelectionView] Send out clicked ");
            }
        }).show();
    }

    @Override
    public void onDetach() {
        Log.i("EVENT", "[SystemReadinessFragment] onDetach");
        super.onDetach();
    }

    @Override
    public void onPause() {
        Log.i("EVENT", "[SystemReadinessFragment] onPause");
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.i("EVENT", "[SystemReadinessFragment] onResume");
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        Log.i("EVENT", "[SystemReadinessFragment] onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i("EVENT", "[SystemReadinessFragment] onPause");
        super.onDestroy();
    }

}


