package com.example.st.arcgiscss.model;

import org.json.*;

import java.io.Serializable;


public class IncidentLoc implements Serializable {
	
    private String incidentLocation;
    private String activationLocation;
    
    
	public IncidentLoc () {
		
	}	
        
    public IncidentLoc (JSONObject json) {
    
        this.incidentLocation = json.optString("incidentLocation");
        this.activationLocation = json.optString("activationLocation");

    }
    
    public String getIncidentLocation() {
        return this.incidentLocation;
    }

    public void setIncidentLocation(String incidentLocation) {
        this.incidentLocation = incidentLocation;
    }

    public String getActivationLocation() {
        return this.activationLocation;
    }

    public void setActivationLocation(String activationLocation) {
        this.activationLocation = activationLocation;
    }


    
}
