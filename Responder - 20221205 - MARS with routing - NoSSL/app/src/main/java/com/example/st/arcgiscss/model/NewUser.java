package com.example.st.arcgiscss.model;

import org.json.JSONObject;

import java.io.Serializable;

public class NewUser implements Serializable {

    private String user_name;
    private String id;
    private String sector;
    private String isEAS;
    private String top_up_fuel;

    public NewUser() {

    }

    public NewUser(JSONObject json) {

        this.user_name = json.optString("user_name");
        this.id = json.optString("id");
//        this.incidentList = new ArrayList<IncidentInfo>();
//        JSONArray arrayIncidentList = json.optJSONArray("incidentList");
//        if (null != arrayIncidentList) {
//            int incidentListLength = arrayIncidentList.length();
//            for (int i = 0; i < incidentListLength; i++) {
//                JSONObject item = arrayIncidentList.optJSONObject(i);
//                if (null != item) {
//                    this.incidentList.add(new IncidentInfo(item));
//                }
//            }
//        }
//        else {
//            JSONObject item = json.optJSONObject("incidentList");
//            if (null != item) {
//                this.incidentList.add(new IncidentInfo(item));
//            }
//        }
        this.sector = json.optString("sector");
        this.isEAS = json.optString("isEAS");
        this.top_up_fuel = json.optString("top_up_fuel");

    }


    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getIsEAS() {
        return isEAS;
    }

    public void setIsEAS(String isEAS) {
        this.isEAS = isEAS;
    }

    public String getTop_up_fuel() {
        return top_up_fuel;
    }

    public void setTop_up_fuel(String top_up_fuel) {
        this.top_up_fuel = top_up_fuel;
    }

}

