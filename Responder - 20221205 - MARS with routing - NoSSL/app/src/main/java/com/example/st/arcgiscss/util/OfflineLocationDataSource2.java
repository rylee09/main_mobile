package com.example.st.arcgiscss.util;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.location.LocationDataSource;
import com.example.st.arcgiscss.constant.MyApplication;

public class OfflineLocationDataSource2  extends LocationDataSource {

    private Point mCurrentLocation;
    private final Polyline mRoute;


    public OfflineLocationDataSource2(Polyline route) {
        mRoute = route;
    }


    @Override
    public void onStop() {

    }

    @Override
    protected void onStart() {
        onStartCompleted(null);
        // start at the beginning of the route
       UpdateLocation(new Location(new Point(Double.valueOf(MyApplication.getInstance().getCurrentPoint().getLng()),Double.valueOf(MyApplication.getInstance().getCurrentPoint().getLat()), SpatialReferences.getWgs84())));
//        mCurrentLocation = mRoute.getParts().get(0).getStartPoint();
//        UpdateLocation(new LocationDataSource.Location(mCurrentLocation));
    }


    public void UpdateLocation(LocationDataSource.Location location){
        this.updateLocation(location);
    }

}
