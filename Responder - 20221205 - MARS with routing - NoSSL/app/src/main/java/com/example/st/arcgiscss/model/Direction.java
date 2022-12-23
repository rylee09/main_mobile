package com.example.st.arcgiscss.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Direction implements Serializable {

    private double respCode;
    private ArrayList<NavigationInfo> navigationInfos;


    public Direction () {

    }

    public Direction (JSONObject json) {

        this.respCode = json.optDouble("resp_code");

        this.navigationInfos = new ArrayList<NavigationInfo>();
        JSONArray arrayRespMsg = json.optJSONArray("resp_msg");
        if (null != arrayRespMsg) {
            int respMsgLength = arrayRespMsg.length();
            for (int i = 0; i < respMsgLength; i++) {
                JSONObject item = arrayRespMsg.optJSONObject(i);
                if (null != item) {
                    this.navigationInfos.add(new NavigationInfo(item));
                }
            }
        }
        else {
            JSONObject item = json.optJSONObject("resp_msg");
            if (null != item) {
                this.navigationInfos.add(new NavigationInfo(item));
            }
        }


    }

    public double getRespCode() {
        return this.respCode;
    }

    public void setRespCode(double respCode) {
        this.respCode = respCode;
    }

    public ArrayList<NavigationInfo> getRespMsg() {
        return this.navigationInfos;
    }

    public void setRespMsg(ArrayList<NavigationInfo> friend) {
        this.navigationInfos = friend;
    }

    @Override
    public String toString() {
        return "Direction{" +
                "respCode=" + respCode +
                ", NavigationInfo=" + navigationInfos +
                '}';
    }
}

