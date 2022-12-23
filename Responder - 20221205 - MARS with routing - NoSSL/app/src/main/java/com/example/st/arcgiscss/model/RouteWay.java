package com.example.st.arcgiscss.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class RouteWay implements Serializable {


    private String time;
    private String status;
    private String routeName;
    private Convoy convoy;
    private String color;
    private String routeNo;
    private FromPoint fromPoint;
    private String toAddress;
    private ToPoint toPoint;
    private ArrayList<MyPoint> line;
    private String fromAddress;
    private ArrayList<WayPoints> wayPoints;

//  private List<Maneuver> maneuvers;

    public RouteWay () {

    }

    public RouteWay (JSONObject json) {

        this.status = json.optString("status");
        this.routeName = json.optString("routeName");
        this.convoy = new Convoy(json.optJSONObject("convoy"));
        this.color = json.optString("color");
        this.routeNo = json.optString("routeNo");
        this.fromPoint = new FromPoint(json.optJSONObject("fromPoint"));
        this.toAddress = json.optString("toAddress");
        this.time = json.optString("time");
        this.toPoint = new ToPoint(json.optJSONObject("toPoint"));

        this.line = new ArrayList<MyPoint>();
        JSONArray arrayLine = json.optJSONArray("line");
        if (null != arrayLine) {
            int lineLength = arrayLine.length();
            for (int i = 0; i < lineLength; i++) {
                JSONObject item = arrayLine.optJSONObject(i);
                if (null != item) {
                    this.line.add(new MyPoint(item));
                }
            }
        }
        else {
            JSONObject item = json.optJSONObject("line");
            if (null != item) {
                this.line.add(new MyPoint(item));
            }
        }

        this.fromAddress = json.optString("fromAddress");

        this.wayPoints = new ArrayList<WayPoints>();
        JSONArray arrayWayPoints = json.optJSONArray("wayPoints");
        if (null != arrayWayPoints) {
            int wayPointsLength = arrayWayPoints.length();
            for (int i = 0; i < wayPointsLength; i++) {
                JSONObject item = arrayWayPoints.optJSONObject(i);
                if (null != item) {
                    this.wayPoints.add(new WayPoints(item));
                }
            }
        }
        else {
            JSONObject item = json.optJSONObject("wayPoints");
            if (null != item) {
                this.wayPoints.add(new WayPoints(item));
            }
        }


    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRouteName() {
        return this.routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public Convoy getConvoy() {
        return this.convoy;
    }

    public void setConvoy(Convoy convoy) {
        this.convoy = convoy;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getRouteNo() {
        return this.routeNo;
    }

    public void setRouteNo(String routeNo) {
        this.routeNo = routeNo;
    }

    public FromPoint getFromPoint() {
        return this.fromPoint;
    }

    public void setFromPoint(FromPoint fromPoint) {
        this.fromPoint = fromPoint;
    }

    public String getToAddress() {
        return this.toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public ToPoint getToPoint() {
        return this.toPoint;
    }

    public void setToPoint(ToPoint toPoint) {
        this.toPoint = toPoint;
    }

    public ArrayList<MyPoint> getLine() {
        return this.line;
    }

    public void setLine(ArrayList<MyPoint> line) {
        this.line = line;
    }

    public String getFromAddress() {
        return this.fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public ArrayList<WayPoints> getWayPoints() {
        return this.wayPoints;
    }

    public void setWayPoints(ArrayList<WayPoints> wayPoints) {
        this.wayPoints = wayPoints;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

//    public List<Maneuver> getManeuvers() {
//        return maneuvers;
//    }
//
//    public void setManeuvers(List<Maneuver> maneuvers) {
//        this.maneuvers = maneuvers;
//    }
}
