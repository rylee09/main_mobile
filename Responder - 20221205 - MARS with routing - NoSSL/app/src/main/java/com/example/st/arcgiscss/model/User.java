package com.example.st.arcgiscss.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
	
    private String username;
    private String id;
    private ArrayList<NewIncident> incidentList;
    private String activationLocation;
    private String type;
    private String pwd;

    //ZN - 20210410
    private String fullusername;
    private String login_time;
    private String maintenance;

    //ZN - 20210606
    private String token;

    //ZN - 20210611
    private double activation_lat;
    private double activation_lon;
    private String activation_latlon;

    //ZN - 20210630
    private String status = "";
    private String system_ready = "";

	public User() {
		
	}	
        
    public User(JSONObject json) {
        Log.i("USER", "constructor");
        this.username = json.optString("username");
//        this.id = json.optDouble("id");
        this.id = json.optString("id");

        this.incidentList = new ArrayList<NewIncident>();
        JSONArray arrayIncidentList = json.optJSONArray("incidentList");
        if (null != arrayIncidentList) {
            int incidentListLength = arrayIncidentList.length();
            for (int i = 0; i < incidentListLength; i++) {
                JSONObject item = arrayIncidentList.optJSONObject(i);
                if (null != item) {
                    this.incidentList.add(new NewIncident(item));
                }
            }
        }
        else {
            JSONObject item = json.optJSONObject("incidentList");
            if (null != item) {
                this.incidentList.add(new NewIncident(item));
            }
        }

        this.activationLocation = json.optString("activation_location");
        this.type = json.optString("type");
        this.pwd = json.optString("pwd");

        //ZN - 20210410
        this.fullusername = json.optString("fullusername");
        this.login_time = json.optString("login_time");
        this.maintenance = json.optString("maintenance");

        //ZN - 20210606
        this.token = json.optString("token");

        //ZN - 20210611
        this.activation_lat = json.optDouble("activation_lat");
        this.activation_lon = json.optDouble("activation_lon");
        this.activation_latlon = json.optString("activation_latlon");
    }
    
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<NewIncident> getIncidentList() {
        return this.incidentList;
    }

    public void setIncidentList(ArrayList<NewIncident> incidentList) {
        this.incidentList = incidentList;
    }

    public String getActivationLocation() {
        return this.activationLocation;
    }

    public void setActivationLocation(String activationLocation) {
        this.activationLocation = activationLocation;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPwd() {
        return this.pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    //ZN - 20210410
    public String getFullusername() {
        return fullusername;
    }

    public void setFullusername(String fullusername) {
        this.fullusername = fullusername;
    }

    public String getLogin_time() {
        return login_time;
    }

    public void setLogin_time(String login_time) {
        this.login_time = login_time;
    }

    public String getMaintenance() {
        return maintenance;
    }

    public void setMaintenance(String maintenance) {
        this.maintenance = maintenance;
    }
    //ZN - 20210410

    //ZN - 20210606
    public String getToken() { return token; }

    public void setToken(String token) { this.token = token; }

    //ZN - 20210611
    public double getActivation_lon() { return activation_lon;    }

    public void setActivation_lon(double activation_lon) { this.activation_lon = activation_lon;    }

    public double getActivation_lat() { return activation_lat;    }

    public void setActivation_lat(double activation_lat) { this.activation_lat = activation_lat;    }

    public String getActivation_latlon() { return activation_latlon;    }

    public void setActivation_latlon(String activation_latlon) { this.activation_latlon = activation_latlon;    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSystemReady() { return system_ready;    }

    public void setSystemReady(String system_ready) {
	    Log.i("USER", "setSystemReady: " + system_ready);
	    this.system_ready = system_ready;    }
}
