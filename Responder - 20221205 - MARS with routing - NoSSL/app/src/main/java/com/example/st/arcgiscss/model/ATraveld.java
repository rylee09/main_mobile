package com.example.st.arcgiscss.model;

import java.io.Serializable;
import java.util.List;

public class ATraveld implements Serializable{

    private List<NavigationInfo> ahead;

    private List<NavigationInfo> traveled;

    private String routeNo;


    public List<NavigationInfo> getAhead() {
        return ahead;
    }

    public void setAhead(List<NavigationInfo> ahead) {
        this.ahead = ahead;
    }

    public List<NavigationInfo> getTraveled() {
        return traveled;
    }

    public void setTraveled(List<NavigationInfo> traveled) {
        this.traveled = traveled;
    }

    public String getRouteNo() {
        return routeNo;
    }

    public void setRouteNo(String routeNo) {
        this.routeNo = routeNo;
    }

    public ATraveld() {
    }

    public ATraveld(List<NavigationInfo> ahead, List<NavigationInfo> traveled, String routeNo) {
        this.ahead = ahead;
        this.traveled = traveled;
        this.routeNo = routeNo;
    }
}
