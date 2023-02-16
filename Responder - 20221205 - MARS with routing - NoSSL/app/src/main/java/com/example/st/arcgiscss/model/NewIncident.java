package com.example.st.arcgiscss.model;

import com.esri.arcgisruntime.portal.HelperServices;

import org.json.JSONObject;

import java.io.Serializable;

public class NewIncident implements Serializable {
    private String id;
    private String type;
    private String description;
    private String status;
    private String incidentCondition;
    private String casualties;
    private String incidentLocation;
    private String activationLocation;
    private String timestamp;
    private String lat;
    private String lng;

    //ZN - 20200618
    private String locationType;
    private String CCP_lat;
    private String CCP_lon;

    //ZN - 20210117
    private String POC_Name;
    private String POC_Contact;

    private String TLP_lat;
    private String TLP_lon;

    //ZN - 20210204
    private String POC_Unit;

    //ZN - 20210410
    private String actual_incident_address;

    //ZN - 20210615
    private String responder_remarks;

    //ZN - 20220720 restore activation
    private String current_status;
    private String destination_point_Lat;
    private String destination_point_Lon;
    private String str_toString;

    public NewIncident () {

    }

    public NewIncident (JSONObject json) {

        this.id = json.optString("id");
        this.type = json.optString("type");
        this.description = json.optString("description");
        this.status = json.optString("status");
        this.incidentCondition = json.optString("incidentCondition");
        this.casualties = json.optString("casualties");
        this.incidentLocation = json.optString("incidentLocation");
        this.activationLocation = json.optString("activationLocation");
        this.timestamp = json.optString("timestamp");
        this.lat = json.optString("lat");
        this.lng = json.optString("lng");

        //ZN - 20200618
        this.locationType = json.optString("location_type");
        this.CCP_lat = json.optString("CCP_lat");
        this.CCP_lon = json.optString("CCP_lon");

        //ZN - 20210119
        this.POC_Name = json.optString("POC_name");
        this.POC_Contact = json.optString("POC_contact");

        //ZN - 20210410
        this.POC_Unit = json.optString("POC_unit");
        this.actual_incident_address = json.optString("actual_incident_address");

        //ZN - 20220720 restore activation
        this.current_status = json.optString("current_status");
        this.destination_point_Lat = json.optString("destination_point_Lat");
        this.destination_point_Lon = json.optString("destination_point_Lon");
        this.str_toString = json.toString();
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return this.id;
    }
    public String getLocation(){
        return this.incidentLocation;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActivationLocation() {
        return this.activationLocation;
    }

    public void setActivationLocation(String activationLocation) {
        this.activationLocation = activationLocation;
    }

    public String getIncidentCondition() {
        return this.incidentCondition;
    }

    public void setIncidentCondition(String incidentCondition) {
        this.incidentCondition = incidentCondition;
    }

    public String getCasualties() {
        return this.casualties;
    }

    public void setCasualties(String casualties) {
        this.casualties = casualties;
    }

    public String getLat() {
        return this.lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public String getIncidentID(){
        return this.id;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

//    public String getIncidentLocation() {
//        return this.incidentLocation;
//    }

    public String getLatLon() {
        return this.incidentLocation;
    }

    public void setIncidentLocation(String incidentLocation) {
        this.incidentLocation = incidentLocation;
    }

    public String getLng() {
        return this.lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    //ZN - 20200618
    public String getLocationType() { return this.locationType; }

    public void setLocationType(String locationType) { this.locationType = locationType; }

    public String getCCP_lat() { return this.CCP_lat; }

    public void setCCP_lat(String CCP_lat) { this.CCP_lat = CCP_lat; }

    public String getCCP_lon() { return CCP_lon;}

    public void setCCP_lon(String CCP_lon) { this.CCP_lon = CCP_lon; }
    //ZN - 20200618

    //ZN - 20210117
    public String getPOC_Name() {
        return POC_Name;
    }

    public String getPOC_Contact() {
        return POC_Contact;
    }

    public void setPOC_Name(String POC_Name) {
        this.POC_Name = POC_Name;
    }

    public void setPOC_Contact(String POC_Contact) {
        this.POC_Contact = POC_Contact;
    }

    public String getTLP_lat() {
        return TLP_lat;
    }

    public void setTLP_lat(String TLP_lat) {
        this.TLP_lat = TLP_lat;
    }

    public String getTLP_lon() {
        return TLP_lon;
    }

    public void setTLP_lon(String TLP_lon) {
        this.TLP_lon = TLP_lon;
    }

    //ZN - 20210204
    public String getPOC_Unit() {
        return POC_Unit;
    }

    public void setPOC_Unit(String POC_Unit) {
        this.POC_Unit = POC_Unit;
    }

    //ZN - 2020410
    public String getActual_incident_address() {return actual_incident_address;};

    public void setActual_incident_address(String actual_incident_address) {this.actual_incident_address = actual_incident_address;};

    //ZN - 20210615
    public String getResponderRemarks() { return responder_remarks;    }

    public void setResponderRemarks(String responder_remarks) {  this.responder_remarks = responder_remarks;    }

    //ZN - 20220720 restore activation
    public String getCurrentStatus() {
        return current_status;
    }

    public void setCurrentStatus(String current_status) {
        this.current_status = current_status;
    }

    public String getDestinationPointLat() {
        return destination_point_Lat;
    }

    public void setDestinationPointLat(String destination_point_Lat) {
        this.destination_point_Lat = destination_point_Lat;
    }

    public String getDestinationPointLon() {
        return destination_point_Lon;
    }

    public void setDestinationPointLon(String destination_point_Lon) {
        this.destination_point_Lon = destination_point_Lon;
    }

    public String toString() {
        //return as json type string to write into activationLog; need to include incident current_status, dest lat lon
        String dest_lat_Lon, ret_str;
        if (destination_point_Lon == "" && destination_point_Lat == "")
            dest_lat_Lon = "null";
        else
            dest_lat_Lon = destination_point_Lat + ":" + destination_point_Lon;

        //create new json string to append at end of existing json string
        String extra_json = "," + "\"current_status\":\"" + current_status + "\"," + "\"destination_point_Lat\":\"" + destination_point_Lat + "\"," + "\"destination_point_Lon\":\"" + destination_point_Lon + "\"";
        int index = str_toString.indexOf('}');
        ret_str = str_toString.substring(0, index) + extra_json + str_toString.substring(index);
        return ret_str;
    }



}


