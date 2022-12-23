package com.example.st.arcgiscss.model;

import java.io.Serializable;

public class RequestModel implements Serializable {

    private String requestName;
    private String requestparams;

    public String getRequestName() {
        return requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    public String getRequestparams() {
        return requestparams;
    }

    public void setRequestparams(String requestparams) {
        this.requestparams = requestparams;
    }
}
