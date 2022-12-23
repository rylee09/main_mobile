package com.example.st.arcgiscss.model;

import org.json.*;

import java.io.Serializable;


public class Schedule implements Serializable {
	
    private String state;
    private String convoyNo;
    private String startTime;
    
    
	public Schedule () {
		
	}	
        
    public Schedule (JSONObject json) {
    
        this.state = json.optString("state");
        this.convoyNo = json.optString("convoyNo");
        this.startTime = json.optString("startTime");

    }
    
    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getConvoyNo() {
        return this.convoyNo;
    }

    public void setConvoyNo(String convoyNo) {
        this.convoyNo = convoyNo;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }


    
}
