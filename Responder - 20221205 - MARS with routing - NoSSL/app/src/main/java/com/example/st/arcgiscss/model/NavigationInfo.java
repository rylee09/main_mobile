package com.example.st.arcgiscss.model;

import org.json.JSONObject;

import java.io.Serializable;


public class NavigationInfo implements Serializable {
	
    private String content;
    private String next;
    private double distance;
    private MyPoint point;
    
    
	public NavigationInfo() {
		
	}	
        
    public NavigationInfo(JSONObject json) {
    
        this.content = json.optString("content");
        this.next = json.optString("next");
        this.distance = json.optDouble("distance");
        this.point = new MyPoint(json.optJSONObject("point"));

    }
    
    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNext() {
        return this.next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public double getDistance() {
        return this.distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public MyPoint getPoint() {
        return this.point;
    }

    public void setPoint(MyPoint point) {
        this.point = point;
    }


    @Override
    public String toString() {
        return "NavigationInfo{" +
                "content='" + content + '\'' +
                ", next='" + next + '\'' +
                ", distance=" + distance +
                ", point=" + point +
                '}';
    }
}
