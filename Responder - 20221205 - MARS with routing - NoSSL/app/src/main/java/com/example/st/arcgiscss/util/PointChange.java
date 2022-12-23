package com.example.st.arcgiscss.util;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.example.st.arcgiscss.model.MyPoint;

public class PointChange {
    public static Point getMapPoint( MyPoint point) {
        return  new Point(Double.valueOf(point.getLng()),Double.valueOf(point.getLat()), SpatialReferences.getWgs84());
    }

    public static Point getMapPointLatAndLng(String lat, String lng) {
        return  new Point(Double.valueOf(lng),Double.valueOf(lat), SpatialReferences.getWgs84());
    }


    public static MyPoint getMyPointFromPoint( Point point) {
        return  new MyPoint(point.getY()+"",point.getX()+"");
    }

}
