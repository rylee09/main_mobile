package com.example.st.arcgiscss.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
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

import com.example.st.arcgiscss.R;
import com.example.st.arcgiscss.activites.NewMainActivity;
import com.example.st.arcgiscss.activites.SelectEvacActivity;
import com.example.st.arcgiscss.constant.MyApplication;
import com.example.st.arcgiscss.d3View.D3Fragment;
import com.example.st.arcgiscss.d3View.D3View;
import com.example.st.arcgiscss.model.FuelTopUp;
import com.example.st.arcgiscss.model.User;
import com.example.st.arcgiscss.util.CacheUtils;
import com.example.st.arcgiscss.views.IosAlertDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PeerReadinessFragment extends D3Fragment {
    @D3View(click = "onClick")
    ImageView iv_back_left, iv_peer_1, iv_peer_2, iv_peer_3, iv_peer_4, iv_peer_5, iv_peer_6 ;
    @D3View
    TextView tv_nowtime, tv_peer_name_1, tv_peer_name_2, tv_peer_name_3, tv_peer_name_4, tv_peer_name_5, tv_peer_name_6;
    @D3View
    TextView tv_peer_relation_1, tv_peer_relation_2, tv_peer_relation_3, tv_peer_relation_4, tv_peer_relation_5, tv_peer_relation_6;

    private final String STANDBY = "STANDBY";
    private final String ACTIVATED = "ACTIVATED";
    private final String OFFLINE = "OFFLINE";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = setContentView(inflater, R.layout.frag_peer_ready);
        initView();
        initData();

//        updatePeerStatus();

        //ZN - 20220125 consolidate peer readiness method call
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(myPeerReadinessMsgReceiver,
                new IntentFilter("NewPeerReadinessMsgReceiver"));

        return view;
    }

    public void initView() {
    }

    public void initData() {
    }

    //ZN - 20210818
    private final BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, android.content.Intent intent) {
            Log.i("onReceive", "[myBroadcastReceiver] peer readiness onReceive");
            Boolean status = (Boolean) intent.getExtras().getSerializable("backupEAS_status");

        }
    };

    //ZN - 20220125 consolidate peer readiness method call
    private final BroadcastReceiver myPeerReadinessMsgReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, android.content.Intent intent) {
            Log.i("onReceive", "[myPeerReadinessMsgReceiver] status onReceive");
            HashMap<String, String> data = (HashMap<String, String>) intent.getExtras().getSerializable("data");
            updatePeerStatus(data);
        }
    };

    @Override
    public void onDetach() {
        Log.i("EVENT", "[PeerReadinessFragment] onDetach");
        super.onDetach();
    }

    @Override
    public void onPause() {
        Log.i("EVENT", "[PeerReadinessFragment] onPause");
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.i("EVENT", "[PeerReadinessFragment] onResume");
        super.onResume();

        //updatePeerStatus();
    }

    @Override
    public void onDestroyView() {
        Log.i("EVENT", "[PeerReadinessFragment] onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i("EVENT", "[PeerReadinessFragment] onPause");

        //ZN - 20220125 consolidate peer readiness method call
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(myPeerReadinessMsgReceiver);

        super.onDestroy();
    }

    //ZN - 20210819
    public void updatePeerStatus() {
        //ZN - 20211124 get camp responder peers
        HashMap<String, String> peerStatus = MyApplication.getInstance().getUserEASStatus();
        User user = CacheUtils.getUser(getActivity());
        if (user.getType().equalsIgnoreCase("EAS")) {
            Log.i("PEER", "PEER display as EAS");
            String temp_peer = MyApplication.peerList.get(MyApplication.getInstance().getUsername());
            String temp_non_peer = MyApplication.nonPeerList.get(MyApplication.getInstance().getUsername());

            //multiple peers will contain ":"
            if (temp_peer.contains(":")) {
                String[] tempArr = temp_peer.split(":");
                tv_peer_name_1.setText(tempArr[0]);
                tv_peer_name_2.setText(tempArr[1]);
                tv_peer_relation_1.setText("PEER");
                tv_peer_relation_2.setText("PEER");

                tempArr = temp_non_peer.split(":");
                tv_peer_name_3.setText(tempArr[0]);
                tv_peer_name_4.setText(tempArr[1]);
                tv_peer_name_5.setText(tempArr[2]);

            } else {
                tv_peer_name_1.setText(temp_peer);
                tv_peer_relation_1.setText("PEER");

                String[] tempArr = temp_non_peer.split(":");
                tv_peer_name_2.setText(tempArr[0]);
                tv_peer_name_3.setText(tempArr[1]);
                tv_peer_name_4.setText(tempArr[2]);
                tv_peer_name_5.setText(tempArr[3]);
            }

            //display of peer status, get username reference from TextView
            //key: username, value: ACTIVATED / STANDBY / OFFLINE
            String temp_status;
            temp_status = peerStatus.get(tv_peer_name_1.getText());
            updatePeerStatusImage(iv_peer_1, temp_status);
            temp_status = peerStatus.get(tv_peer_name_2.getText());
            updatePeerStatusImage(iv_peer_2, temp_status);
            temp_status = peerStatus.get(tv_peer_name_3.getText());
            updatePeerStatusImage(iv_peer_3, temp_status);
            temp_status = peerStatus.get(tv_peer_name_4.getText());
            updatePeerStatusImage(iv_peer_4, temp_status);
            temp_status = peerStatus.get(tv_peer_name_5.getText());
            updatePeerStatusImage(iv_peer_5, temp_status);

        } else {
            //to handle different P1/P2 peer combinations (i.e. 0 peer ,1 peer, 2 peers)
            Log.i("PEER", "PEER display as non-EAS");
            if (peerStatus.size() == 0) {
                //display nothing

                Log.i("PEER", "PEER size is 0");
                tv_peer_name_1.setText("N.A");

            } else if (peerStatus.size() == 1) {
                Log.i("PEER", "PEER size is 1");
                for ( Map.Entry<String, String> entry : peerStatus.entrySet()) {
                    tv_peer_name_1.setText(entry.getKey());
                }

                String temp_status;
                temp_status = peerStatus.get(tv_peer_name_1.getText());
                updatePeerStatusImage(iv_peer_1, temp_status);
            } else {
                Log.i("PEER", "PEER size is 2");
                ArrayList<String> temp_arr = new ArrayList<String>();
                for ( Map.Entry<String, String> entry : peerStatus.entrySet()) {
                    temp_arr.add(entry.getKey());
                }

                tv_peer_name_1.setText(temp_arr.get(0));
                String temp_status;
                temp_status = peerStatus.get(tv_peer_name_1.getText());
                Log.i("PEER", "PEER display 1: " + temp_arr.get(0) + " " + temp_status);
                updatePeerStatusImage(iv_peer_1, temp_status);
                tv_peer_name_2.setText(temp_arr.get(1));
                temp_status = peerStatus.get(tv_peer_name_2.getText());
                Log.i("PEER", "PEER display 2: " + temp_arr.get(0) + " " + temp_status);
                updatePeerStatusImage(iv_peer_2, temp_status);
            }
        }


    }

    //ZN - 20220125 consolidate peer readiness method call
    public void updatePeerStatus(HashMap<String, String> peerStatus) {
        //ZN - 20211124 get camp responder peers
        User user = CacheUtils.getUser(getActivity());
        if (user.getType().equalsIgnoreCase("EAS")) {
            String temp_peer = MyApplication.peerList.get(MyApplication.getInstance().getUsername());
            String temp_non_peer = MyApplication.nonPeerList.get(MyApplication.getInstance().getUsername());

            //multiple peers will contain ":"
            if (temp_peer.contains(":")) {
                String[] tempArr = temp_peer.split(":");
                tv_peer_name_1.setText(tempArr[0]);
                tv_peer_name_2.setText(tempArr[1]);
                tv_peer_relation_1.setText("PEER");
                tv_peer_relation_2.setText("PEER");

                tempArr = temp_non_peer.split(":");
                tv_peer_name_3.setText(tempArr[0]);
                tv_peer_name_4.setText(tempArr[1]);
                tv_peer_name_5.setText(tempArr[2]);

            } else {
                tv_peer_name_1.setText(temp_peer);
                tv_peer_relation_1.setText("PEER");

                String[] tempArr = temp_non_peer.split(":");
                tv_peer_name_2.setText(tempArr[0]);
                tv_peer_name_3.setText(tempArr[1]);
                tv_peer_name_4.setText(tempArr[2]);
                tv_peer_name_5.setText(tempArr[3]);
            }

            //display of peer status, get username reference from TextView
            //key: username, value: ACTIVATED / STANDBY / OFFLINE
            String[] temp;
            String temp_status;
            temp = peerStatus.get(tv_peer_name_1.getText()).split(":");
            updatePeerStatusImage(tv_peer_relation_1, iv_peer_1, temp[0], temp[1]);

            temp = peerStatus.get(tv_peer_name_2.getText()).split(":");
            updatePeerStatusImage(tv_peer_relation_2, iv_peer_2, temp[0], temp[1]);

            temp = peerStatus.get(tv_peer_name_3.getText()).split(":");
            updatePeerStatusImage(tv_peer_relation_3, iv_peer_3, temp[0], temp[1]);

            temp = peerStatus.get(tv_peer_name_4.getText()).split(":");
            updatePeerStatusImage(tv_peer_relation_4, iv_peer_4, temp[0], temp[1]);

            temp = peerStatus.get(tv_peer_name_5.getText()).split(":");
            updatePeerStatusImage(tv_peer_relation_5, iv_peer_5, temp[0], temp[1]);

        } else {
            //to handle different P1/P2 peer combinations (i.e. 0 peer ,1 peer, 2 peers)
            if (peerStatus.size() == 0) {
                //display nothing
                tv_peer_name_1.setText("N.A");
            } else if (peerStatus.size() == 1) {
                for ( Map.Entry<String, String> entry : peerStatus.entrySet()) {
                    tv_peer_name_1.setText(entry.getKey());
                }

                String[] temp_status;
                temp_status = peerStatus.get(tv_peer_name_1.getText()).split(":");
                updatePeerStatusImage(iv_peer_1, temp_status[0]);
            } else {
                ArrayList<String> temp_arr = new ArrayList<String>();
                for ( Map.Entry<String, String> entry : peerStatus.entrySet()) {
                    temp_arr.add(entry.getKey());
                }

                tv_peer_name_1.setText(temp_arr.get(0));
                String[] temp_status;
                temp_status = peerStatus.get(tv_peer_name_1.getText()).split(":");
                updatePeerStatusImage(iv_peer_1, temp_status[0]);

                tv_peer_name_2.setText(temp_arr.get(1));
                temp_status = peerStatus.get(tv_peer_name_2.getText()).split(":");
                updatePeerStatusImage(iv_peer_2, temp_status[0]);
            }
        }

    }

    //ZN - 20210819
    private void updatePeerStatusImage(ImageView iv, String status) {
        if (status.equalsIgnoreCase(STANDBY))
            iv.setImageResource(R.mipmap.peer_standby);
        else if (status.contains(ACTIVATED)) {
            iv.setImageResource(R.mipmap.peer_activated);
            tv_peer_relation_1.setText(status);
        }
        else
            iv.setImageResource(R.mipmap.peer_offline);
    }

    //ZN - 20220125 consolidate peer readiness method call
    private void updatePeerStatusImage(TextView tv, ImageView iv, String status, String peer) {
        if (status.equalsIgnoreCase(STANDBY)) {
            iv.setImageResource(R.mipmap.peer_standby);
            if (peer.equalsIgnoreCase("Y"))
                tv.setText("PEER");
        }
        else if (status.contains(ACTIVATED)) {
            iv.setImageResource(R.mipmap.peer_activated);
            tv.setText(status);
        }
        else {
            iv.setImageResource(R.mipmap.peer_offline);
            if (peer.equalsIgnoreCase("Y"))
                tv.setText("PEER");
        }

    }

}


