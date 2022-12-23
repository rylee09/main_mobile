package com.example.st.arcgiscss.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;


public class Re_route implements Serializable {

    private String state;
    private ArrayList<MyPoint> route;
    
    
	public Re_route() {
		
	}	
        
    public Re_route(JSONObject json) {

        this.state = json.optString("state");
        this.route = new ArrayList<MyPoint>();
        JSONArray arrayRoute = json.optJSONArray("route");
        if (null != arrayRoute) {
            int routeLength = arrayRoute.length();
            for (int i = 0; i < routeLength; i++) {
                JSONObject item = arrayRoute.optJSONObject(i);
                if (null != item) {
                    this.route.add(new MyPoint(item));
                }
            }
        }
        else {
            JSONObject item = json.optJSONObject("route");
            if (null != item) {
                this.route.add(new MyPoint(item));
            }
        }


    }


    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public ArrayList<MyPoint> getRoute() {
        return this.route;
    }

    public void setRoute(ArrayList<MyPoint> route) {
        this.route = route;
    }

    @Override
    public String toString() {
        return "Re_route{" +
                "state='" + state + '\'' +
                ", route=" + route +
                '}';
    }
}
