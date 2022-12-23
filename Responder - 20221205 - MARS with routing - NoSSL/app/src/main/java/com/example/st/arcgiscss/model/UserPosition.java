package com.example.st.arcgiscss.model;

import java.io.Serializable;

public class UserPosition implements Serializable {

    private String userId;
    private String lat;
    private String lng;
    private String activationLocation;
    private String timestamp;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getActivationLocation() {
        return activationLocation;
    }

    public void setActivationLocation(String activationLocation) {
        this.activationLocation = activationLocation;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
