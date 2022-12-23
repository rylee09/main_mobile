package com.example.st.arcgiscss.util;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.location.LocationDataSource;
import com.example.st.arcgiscss.constant.MyApplication;

public class CustomDataSource extends LocationDataSource {
    @Override
    protected void onStart() {
        onStartCompleted(null);
        UpdateLocation(new Location(new Point(Double.valueOf(MyApplication.getInstance().getCurrentPoint().getLng()),Double.valueOf(MyApplication.getInstance().getCurrentPoint().getLat()), SpatialReferences.getWgs84())));
    }

    @Override
    protected void onStop() {

    }

    public void UpdateLocation(LocationDataSource.Location location){
        this.updateLocation(location);
    }
}
