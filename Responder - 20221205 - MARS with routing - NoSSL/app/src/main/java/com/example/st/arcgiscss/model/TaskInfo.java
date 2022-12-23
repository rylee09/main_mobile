package com.example.st.arcgiscss.model;

import org.json.JSONObject;

import java.io.Serializable;


public class TaskInfo implements Serializable {
	
    private String lng;
    private String lat;
    private String name;
    private String locationName;
    private String completeTime;
    
    
	public TaskInfo() {
		
	}	
        
    public TaskInfo(JSONObject json) {
    
        this.lng = json.optString("lng");
        this.lat = json.optString("lat");
        this.name = json.optString("name");
        this.locationName = json.optString("location_name");
        this.completeTime = json.optString("complete_time");

    }
    
    public String getLng() {
        return this.lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return this.lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocationName() {
        return this.locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getCompleteTime() {
        return this.completeTime;
    }

    public void setCompleteTime(String completeTime) {
        this.completeTime = completeTime;
    }

    @Override
    public String toString() {
        return "TaskInfo{" +
                "lng='" + lng + '\'' +
                ", lat='" + lat + '\'' +
                ", name='" + name + '\'' +
                ", locationName='" + locationName + '\'' +
                ", completeTime='" + completeTime + '\'' +
                '}';
    }
}
