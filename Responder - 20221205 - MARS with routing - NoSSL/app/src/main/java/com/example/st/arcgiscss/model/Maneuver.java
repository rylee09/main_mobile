package com.example.st.arcgiscss.model;

import java.io.Serializable;

public class Maneuver implements Serializable {


    private String describute;
    private MyPoint point;
    private int turn;

    private int turnType;
    private String narrative;
    private MyPoint startPoint;
    private String total_distance;


    public Maneuver() {

    }



    public int getTurnType() {
        return turnType;
    }

    public void setTurnType(int turnType) {
        this.turnType = turnType;
    }

    public String getNarrative() {
        return narrative;
    }

    public void setNarrative(String narrative) {
        this.narrative = narrative;
    }

    public MyPoint getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(MyPoint startPoint) {
        this.startPoint = startPoint;
    }
}
