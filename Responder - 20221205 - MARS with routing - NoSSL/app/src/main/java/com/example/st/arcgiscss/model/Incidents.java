package com.example.st.arcgiscss.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;


public class Incidents implements Serializable {

    private String status;
    private ArrayList<String> images;
    private String address;
    private String incidentType;
    private String incidentNo;
    private String incidentTime;
    private String affectConvoy;
    private String lat;
    private String lng;
    private String description;
    private String affectRoute;
    private String create_time;
    private String title;
    private String incidentName;
//    private String isdiss;

	public Incidents() {

	}

   public Incidents(String title, String time,  String lat,String lon) {
        this.title = title;
        this.lat = lat;
        this.lng = lon;
        this.create_time = time;
    }


    public Incidents(JSONObject json) {

        this.status = json.optString("status");
        this.images = new ArrayList<String>();
        JSONArray arrayImages = json.optJSONArray("images");
        if (null != arrayImages) {
            int imagesLength = arrayImages.length();
            for (int i = 0; i < imagesLength; i++) {
                String item = arrayImages.optString(i);
                if (null != item) {
                    this.images.add(item);
                }
            }
        }
        else {
            String item = json.optString("images");
            if (null != item) {
                this.images.add(item);
            }
        }
        this.address = json.optString("address");
        this.incidentType = json.optString("incidentType");
        this.incidentNo = json.optString("incidentNo");
        this.incidentName = json.optString("incidentName");
        this.incidentTime = json.optString("incidentTime");
        this.affectConvoy = json.optString("affectConvoy");
        this.lat = json.optString("lat");
        this.lng = json.optString("lng");
        this.description = json.optString("description");
        this.affectRoute = json.optString("affectRoute");

    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIncidentType() {
        return this.incidentType;
    }

    public void setIncidentType(String incidentType) {
        this.incidentType = incidentType;
    }

    public String getIncidentNo() {
        return this.incidentNo;
    }

    public void setIncidentNo(String incidentNo) {
        this.incidentNo = incidentNo;
    }

    public String getIncidentName() { return incidentName; }

    public void setIncidentName(String incidentName) { this.incidentName = incidentName; }

    public String getIncidentTime() {
        return this.incidentTime;
    }

    public void setIncidentTime(String incidentTime) {
        this.incidentTime = incidentTime;
    }

    public String getAffectConvoy() {
        return this.affectConvoy;
    }

    public void setAffectConvoy(String affectConvoy) {
        this.affectConvoy = affectConvoy;
    }

    public String getLat() {
        return this.lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return this.lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAffectRoute() {
        return this.affectRoute;
    }

    public void setAffectRoute(String affectRoute) {
        this.affectRoute = affectRoute;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Incidents{" +
                "status='" + status + '\'' +
                ", images='" + images + '\'' +
                ", address='" + address + '\'' +
                ", incidentType='" + incidentType + '\'' +
                ", incidentNo='" + incidentNo + '\'' +
                ", incidentName='" + incidentName + '\'' +
                ", incidentTime='" + incidentTime + '\'' +
                ", affectConvoy='" + affectConvoy + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", description='" + description + '\'' +
                ", affectRoute='" + affectRoute + '\'' +
                ", create_time='" + create_time + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
