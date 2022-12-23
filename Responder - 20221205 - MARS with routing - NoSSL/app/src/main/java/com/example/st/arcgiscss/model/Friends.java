package com.example.st.arcgiscss.model;

import org.json.*;

import java.io.Serializable;
import java.util.ArrayList;

public class Friends implements Serializable {
	
    private double respCode;
    private ArrayList<Friend> friend;
    
    
	public Friends () {
		
	}	
        
    public Friends (JSONObject json) {
    
        this.respCode = json.optDouble("resp_code");

        this.friend = new ArrayList<Friend>();
        JSONArray arrayRespMsg = json.optJSONArray("resp_msg");
        if (null != arrayRespMsg) {
            int respMsgLength = arrayRespMsg.length();
            for (int i = 0; i < respMsgLength; i++) {
                JSONObject item = arrayRespMsg.optJSONObject(i);
                if (null != item) {
                    this.friend.add(new Friend(item));
                }
            }
        }
        else {
            JSONObject item = json.optJSONObject("resp_msg");
            if (null != item) {
                this.friend.add(new Friend(item));
            }
        }


    }
    
    public double getRespCode() {
        return this.respCode;
    }

    public void setRespCode(double respCode) {
        this.respCode = respCode;
    }

    public ArrayList<Friend> getRespMsg() {
        return this.friend;
    }

    public void setRespMsg(ArrayList<Friend> friend) {
        this.friend = friend;
    }

    @Override
    public String toString() {
        return "Friends{" +
                "respCode=" + respCode +
                ", friend=" + friend +
                '}';
    }
}
