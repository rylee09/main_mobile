package com.example.st.arcgiscss.model;

import org.json.*;

import java.io.Serializable;


public class Convoy implements Serializable {
    private String vehicleUser;
    private String state;
    private String endTime;
    private String routeNo;
    private double vehicleQty;
    private String convoyNo;
    private String vehicleType;
    private String vehicleCompany;
    private String startTime;
    private String vehicleNo;
    private String convoyName;


    public Convoy () {

    }

    public Convoy (JSONObject json) {

        this.vehicleUser = json.optString("vehicleUser");
        this.state = json.optString("state");
        this.endTime = json.optString("endTime");
        this.routeNo = json.optString("routeNo");
        this.vehicleQty = json.optDouble("vehicleQty");
        this.convoyNo = json.optString("convoyNo");
        this.vehicleType = json.optString("vehicleType");
        this.vehicleCompany = json.optString("vehicleCompany");
        this.startTime = json.optString("startTime");
        this.vehicleNo = json.optString("vehicleNo");
        this.convoyName = json.optString("convoyName");

    }

    public String getVehicleUser() {
        return this.vehicleUser;
    }

    public void setVehicleUser(String vehicleUser) {
        this.vehicleUser = vehicleUser;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getRouteNo() {
        return this.routeNo;
    }

    public void setRouteNo(String routeNo) {
        this.routeNo = routeNo;
    }

    public double getVehicleQty() {
        return this.vehicleQty;
    }

    public void setVehicleQty(double vehicleQty) {
        this.vehicleQty = vehicleQty;
    }

    public String getConvoyNo() {
        return this.convoyNo;
    }

    public void setConvoyNo(String convoyNo) {
        this.convoyNo = convoyNo;
    }

    public String getVehicleType() {
        return this.vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleCompany() {
        return this.vehicleCompany;
    }

    public void setVehicleCompany(String vehicleCompany) {
        this.vehicleCompany = vehicleCompany;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getVehicleNo() {
        return this.vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getConvoyName() {
        return this.convoyName;
    }

    public void setConvoyName(String convoyName) {
        this.convoyName = convoyName;
    }


}
