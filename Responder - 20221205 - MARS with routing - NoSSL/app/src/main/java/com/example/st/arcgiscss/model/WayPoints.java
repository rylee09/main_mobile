package com.example.st.arcgiscss.model;

import org.json.JSONObject;

import java.io.Serializable;


public class WayPoints implements Serializable {
	
    private String name;
    private MyPoint myPoint;
    
    
	public WayPoints () {
		
	}	
        
    public WayPoints (JSONObject json) {
    
        this.name = json.optString("name");
        this.myPoint = new MyPoint(json.optJSONObject("myPoint"));

    }
    
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MyPoint getMyPoint() {
        return this.myPoint;
    }

    public void setMyPoint(MyPoint myPoint) {
        this.myPoint = myPoint;
    }


    
}
