package com.example.st.arcgiscss.model;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public class MyPoint implements Serializable {
	
    private String lat;
    private String lng;
    
    
	public MyPoint(double v, double v1) {
		
	}	

	public MyPoint(String lat,String lng){
	    this.lat = lat;
	    this.lng = lng;
    }


    public MyPoint(JSONObject json) {
    
        this.lat = json.optString("lat");
        this.lng = json.optString("lng");

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

    @Override
    public String toString() {
        return "MyPoint{" +
                "lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                '}';
    }


    /**
     * @param obj
     * @return T
     * @author Muscleape
     * @date 2018/8/10 14:39
     */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T customerClone(T obj) {
        T clonedObj = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.close();

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            clonedObj = (T) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clonedObj;
    }
}
