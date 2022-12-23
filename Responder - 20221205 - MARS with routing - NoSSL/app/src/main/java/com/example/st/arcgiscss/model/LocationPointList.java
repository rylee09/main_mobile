package com.example.st.arcgiscss.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class LocationPointList {
    private ArrayList<LocationPoint> locationPoints;

    public ArrayList<LocationPoint> getLocationPoints() {
        return locationPoints;
    }

    public void setLocationPoints(ArrayList<LocationPoint> locationPoints) {
        this.locationPoints = locationPoints;
    }

    @Override
    public String toString() {
        return "LocationPointList{" +
                "locationPoints=" + locationPoints +
                '}';
    }

    public LocationPointList(JSONObject json) {

        this.locationPoints = new ArrayList<LocationPoint>();
        JSONArray arrayRespMsg = json.optJSONArray("resp_msg");
        if (null != arrayRespMsg) {
            int respMsgLength = arrayRespMsg.length();
            for (int i = 0; i < respMsgLength; i++) {
                JSONObject item = arrayRespMsg.optJSONObject(i);
                if (null != item) {
                    this.locationPoints.add(new LocationPoint(item));
                }
            }
        }
        else {
            JSONObject item = json.optJSONObject("resp_msg");
            if (null != item) {
                this.locationPoints.add(new LocationPoint(item));
            }
        }

    }


    public static class LocationPoint{

        private String locationName;
        private String lat;
        private String lng;
        private String selectStatus;
        public LocationPoint(JSONObject json) {
            this.locationName = json.optString("location_name");
            this.lat = json.optString("lat");
            this.lng = json.optString("lng");
            this.selectStatus = "0";
        }

        public String getLocationName() {
            return locationName;
        }

        public void setLocationName(String locationName) {
            this.locationName = locationName;
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

        public LocationPoint(String locationName, String lat, String lng) {
            this.locationName = locationName;
            this.lat = lat;
            this.lng = lng;
        }

        public LocationPoint(String locationName, String selectStatus) {
            this.locationName = locationName;
            this.selectStatus = selectStatus;
        }

        public String getSelectStatus() {
            return selectStatus;
        }

        public void setSelectStatus(String selectStatus) {
            this.selectStatus = selectStatus;
        }

        @Override
        public String toString() {
            return "LocationPoint{" +
                    "locationName='" + locationName + '\'' +
                    ", lat='" + lat + '\'' +
                    ", lng='" + lng + '\'' +
                    ", selectStatus='" + selectStatus + '\'' +
                    '}';
        }
    }
}


