package com.example.st.arcgiscss.model;

import org.json.JSONObject;

import java.io.Serializable;

public class FuelTopUp implements Serializable {

    private String user_id;
    private String approval_status;

    public static final int APPROVED = 1;
    public static final int REJECTED = 2;
    public static final int PENDING = 3;

    public FuelTopUp(JSONObject json) {
        this.user_id = json.optString("user_id");
        this.approval_status = json.optString("maintenance");
    }

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public String getApprovalStatus() {
        return approval_status;
    }

    public void setApprovalStatus(String approval_status) {
        this.approval_status = approval_status;
    }
}
