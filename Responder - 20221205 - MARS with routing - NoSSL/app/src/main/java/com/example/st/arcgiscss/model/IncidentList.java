package com.example.st.arcgiscss.model;

//public class IncidentList {
//
//    private double respCode;
//    private ArrayList<Incidents> respMsg;
//
//
//	public IncidentList() {
//
//	}
//
//    public IncidentList(JSONObject json) {
//
//        this.respCode = json.optDouble("resp_code");
//
//        this.respMsg = new ArrayList<Incidents>();
//        JSONArray arrayRespMsg = json.optJSONArray("resp_msg");
//        if (null != arrayRespMsg) {
//            int respMsgLength = arrayRespMsg.length();
//            for (int i = 0; i < respMsgLength; i++) {
//                JSONObject item = arrayRespMsg.optJSONObject(i);
//                if (null != item) {
//                    this.respMsg.add(new Incidents(item));
//                }
//            }
//        }
//        else {
//            JSONObject item = json.optJSONObject("resp_msg");
//            if (null != item) {
//                this.respMsg.add(new Incidents(item));
//            }
//        }
//
//
//    }
//
//    public double getRespCode() {
//        return this.respCode;
//    }
//
//    public void setRespCode(double respCode) {
//        this.respCode = respCode;
//    }
//
//    public ArrayList<Incidents> getRespMsg() {
//        return this.respMsg;
//    }
//
//    public void setRespMsg(ArrayList<Incidents> respMsg) {
//        this.respMsg = respMsg;
//    }
//
//
//
//}
