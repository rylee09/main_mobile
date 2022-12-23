package com.example.st.arcgiscss.model;

import org.json.*;

import java.io.Serializable;
import java.util.ArrayList;

public class LocationType implements Serializable {
	
    private double respCode;
    private ArrayList<IncidentLoc> respMsg;
    
    
	public LocationType () {
		
	}	
        
    public LocationType (JSONObject json) {
    
        this.respCode = json.optDouble("resp_code");

        this.respMsg = new ArrayList<IncidentLoc>();
        JSONArray arrayRespMsg = json.optJSONArray("resp_msg");
        if (null != arrayRespMsg) {
            int respMsgLength = arrayRespMsg.length();
            for (int i = 0; i < respMsgLength; i++) {
                JSONObject item = arrayRespMsg.optJSONObject(i);
                if (null != item) {
                    this.respMsg.add(new IncidentLoc(item));
                }
            }
        }
        else {
            JSONObject item = json.optJSONObject("resp_msg");
            if (null != item) {
                this.respMsg.add(new IncidentLoc(item));
            }
        }


    }
    
    public double getRespCode() {
        return this.respCode;
    }

    public void setRespCode(double respCode) {
        this.respCode = respCode;
    }

    public ArrayList<IncidentLoc> getRespMsg() {
        return this.respMsg;
    }

    public void setRespMsg(ArrayList<IncidentLoc> respMsg) {
        this.respMsg = respMsg;
    }


    
}
