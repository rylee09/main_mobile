package com.example.st.arcgiscss.model;

import org.json.JSONObject;

import java.io.Serializable;

public class POCUpdate implements Serializable {

    private String contact, unit, name;

    public POCUpdate(JSONObject json) {
        this.contact = json.optString("POC_contact");
        this.unit = json.optString("POC_unit");
        this.name = json.optString("POC_name");
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
