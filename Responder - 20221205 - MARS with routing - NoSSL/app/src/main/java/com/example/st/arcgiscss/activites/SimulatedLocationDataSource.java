package com.example.st.mobilerouting;

import android.util.Log;

import com.esri.arcgisruntime.geometry.AngularUnit;
import com.esri.arcgisruntime.geometry.AngularUnitId;
import com.esri.arcgisruntime.geometry.GeodeticCurveType;
import com.esri.arcgisruntime.geometry.GeodeticDistanceResult;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.LinearUnit;
import com.esri.arcgisruntime.geometry.LinearUnitId;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.location.LocationDataSource;

import java.util.Timer;
import java.util.TimerTask;

public class SimulatedLocationDataSource extends LocationDataSource {

    private Point mCurrentLocation;
    private final Polyline mRoute;

    private Timer mTimer;

    private double distance = 0.0;
    private static final double distanceInterval = .00025;

    public SimulatedLocationDataSource(Polyline route) {
        mRoute = route;
    }

    @Override
    public void onStop() {
        mTimer.cancel();
    }

    @Override
    protected void onStart() {
        mCurrentLocation = mRoute.getParts().get(0).getStartPoint();
        updateLocation(new Location(mCurrentLocation));
        mTimer = new Timer("SimulatedLocationDataSource Timer", false);
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Point previousPoint = mCurrentLocation;
                mCurrentLocation = GeometryEngine.createPointAlong(mRoute, distance);
                GeodeticDistanceResult distanceResult = GeometryEngine.distanceGeodetic(previousPoint, mCurrentLocation,
                        new LinearUnit(LinearUnitId.METERS), new AngularUnit(AngularUnitId.DEGREES), GeodeticCurveType.GEODESIC);
                updateLocation(new Location(mCurrentLocation, 1, 1, distanceResult.getAzimuth1(), false));
                distance += distanceInterval;
                Log.e("TEST",mCurrentLocation.toString());
            }
        }, 0, 1000);
        onStartCompleted(null);
    }
}
