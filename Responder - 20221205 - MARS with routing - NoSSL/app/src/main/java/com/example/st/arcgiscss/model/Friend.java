package com.example.st.arcgiscss.model;

import org.json.JSONObject;

import java.io.Serializable;


public class Friend implements Serializable {
	
    private String lat;
    private String vehicleNo;
    private String lng;
    
    
	public Friend() {
		
	}	
        
    public Friend(JSONObject json) {
    
        this.lat = json.optString("lat");
        this.vehicleNo = json.optString("vehicleNo");
        this.lng = json.optString("lng");

    }
    
    public String getLat() {
        return this.lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getVehicleNo() {
        return this.vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getLng() {
        return this.lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }


    @Override
    public String toString() {
        return "Friend{" +
                "lat='" + lat + '\'' +
                ", vehicleNo='" + vehicleNo + '\'' +
                ", lng='" + lng + '\'' +
                '}';
    }
}
