package com.example.st.arcgiscss.model;

import org.json.JSONObject;

import java.io.Serializable;

public class UserType implements Serializable {

   private String id;
   private String username;
   private String pwd;
   private String type;


    public UserType(JSONObject json) {

        this.id = json.optString("id");
        this.username = json.optString("username");
        this.pwd = json.optString("pwd");
        this.type = json.optString("type");

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
