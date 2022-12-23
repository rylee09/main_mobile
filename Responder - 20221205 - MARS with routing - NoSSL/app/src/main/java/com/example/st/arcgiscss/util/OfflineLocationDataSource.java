package com.example.st.arcgiscss.util;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.location.LocationDataSource;
import com.example.st.arcgiscss.constant.MyApplication;

/**
     * A LocationDataSource that simulates movement along the specified route. Upon start of the
     * SimulatedLocationDataSource, a timer is started, which updates the location along the route at fixed
     * intervals.
     */
    public class OfflineLocationDataSource extends LocationDataSource {

        private Polyline mRoute;

//        private Point mCurrentLocation;
//
//        private RouteWay mRouteWay;
//
//        private Context mContext;
//
//        private Boolean isStart = true;
//
//        private Boolean isEnd = true;
//
//        private List<String> mWaypointLat = new ArrayList<>();
//
//        public OfflineLocationDataSource(Polyline route, RouteWay routeWay, Context context) {
//            mRoute = route;
//            mRouteWay = routeWay;
//            mContext = context;
//       }

//        private Timer mTimer;
//        private double distance = 0.0;
//        private static final double distanceInterval = .00025;

        public OfflineLocationDataSource(Polyline route) {
            mRoute = route;
        }


    public OfflineLocationDataSource() { }

    @Override
        public void onStop() {
//            mTimer.cancel();
        }

        @Override
        protected void onStart() {
            onStartCompleted(null);
            // start at the beginning of the route
            UpdateLocation(new Location(new Point(Double.valueOf(MyApplication.getInstance().getCurrentPoint().getLng()),Double.valueOf(MyApplication.getInstance().getCurrentPoint().getLat()), SpatialReferences.getWgs84())));
//         if (mRoute != null){
//             mCurrentLocation = mRoute.getParts().get(0).getStartPoint();
//             UpdateLocation(new LocationDataSource.Location(mCurrentLocation));
//             mTimer = new Timer("OfflineLocationDataSource Timer", false);
//             mTimer.scheduleAtFixedRate(new TimerTask() {
//                 @Override
//                 public void run() {
//                     // get a reference to the previous point
//                     Point previousPoint = mCurrentLocation;
//                     // update current location by moving [distanceInterval] meters along the route
//                     mCurrentLocation = GeometryEngine.createPointAlong(mRoute, distance);
//                     MyApplication.getInstance().setCurrentPoint(PointChange.getMyPointFromPoint(mCurrentLocation));
//                     // get the geodetic distance result between the two points
//                     GeodeticDistanceResult distanceResult = GeometryEngine.distanceGeodetic(previousPoint, mCurrentLocation,
//                             new LinearUnit(LinearUnitId.METERS), new AngularUnit(AngularUnitId.DEGREES), GeodeticCurveType.GEODESIC);
//                     // update the location with the current location and use the geodetic distance result to get the azimuth
////                    mCurrentLocation = isNearestRoutePoint(mCurrentLocation);
//                     updateLocation(new LocationDataSource.Location(mCurrentLocation, 1, 1, distanceResult.getAzimuth1(), false));
//
////                    if (CacheUtils.getAppoint(mContext.getApplicationContext()).equals("OIC")){
////                        for (int j=0;j<mRouteWay.getWayPoints().size();j++){
////                            WayPoints wayPoints = mRouteWay.getWayPoints().get(j);
////                            if (isNearWayPoint(mCurrentLocation,wayPoints.getMyPoint())&&mWaypointLat.contains(wayPoints.getMyPoint().getLat())){
////                                tellServerArrivePoint(wayPoints.getName());
////                                mWaypointLat.remove(wayPoints.getMyPoint().getLat());
////                                break;
////                            }
////                        }
////                        if (isEnd&&isNearRoutePoint(mCurrentLocation, MyApplication.getInstance().getEndPoint())){
////                            Log.e("TEST",isEnd+"~~~~");
////                            tellServerArrived();
////                        }
////                        if (isStart&&MyApplication.getInstance().getStartPoint()!=null
////                                &&isNearStrartPoint(mCurrentLocation,MyApplication.getInstance().getStartPoint())
////                                &&isNearRoute(mCurrentLocation)){
////                            tellServerStart();
////                        }
////                    }
//                     // increment the distance
//                     distance += distanceInterval;
//                 }
//             }, 0, 1000);
//         }

////             this method must be called by the subclass once the location data source has finished its starting process
//
        }


        public void UpdateLocation(LocationDataSource.Location location){
            this.updateLocation(location);
        }



//    private boolean isNearStrartPoint(Point aPoint, MyPoint bPoint){
//        android.location.Location a=new android.location.Location("pointA");
//        a.setLatitude(aPoint.getY());
//        a.setLongitude(aPoint.getX());
//
//        android.location.Location b=new android.location.Location("pointB");
//        b.setLatitude(Double.valueOf(bPoint.getLat()));
//        b.setLongitude(Double.valueOf(bPoint.getLng()));
//
//        double distance=a.distanceTo(b);
//        if(distance>100){
//            return true;
//        }
//        return false;
//    }
//
//    private boolean isNearRoutePoint(Point aPoint, MyPoint bPoint){
//        android.location.Location a=new android.location.Location("pointA");
//        a.setLatitude(aPoint.getY());
//        a.setLongitude(aPoint.getX());
//
//        android.location.Location b=new android.location.Location("pointB");
//        b.setLatitude(Double.valueOf(bPoint.getLat()));
//        b.setLongitude(Double.valueOf(bPoint.getLng()));
//
//        double distance=a.distanceTo(b);
//        if(distance<50){
//            return true;
//        }
//        return false;
//    }
//
//
//
//    private boolean isNearWayPoint(Point aPoint, MyPoint bPoint){
//        android.location.Location a=new android.location.Location("pointA");
//        a.setLatitude(aPoint.getY());
//        a.setLongitude(aPoint.getX());
//
//        android.location.Location b=new android.location.Location("pointB");
//        b.setLatitude(Double.valueOf(bPoint.getLat()));
//        b.setLongitude(Double.valueOf(bPoint.getLng()));
//
//        double distance=a.distanceTo(b);
//        if(distance<100){
//            return true;
//        }
//        return false;
//    }
//
//    private boolean isNearRoute(Point loc){
//        for(int point=0;point<mRouteWay.getLine().size();point++){
//            Point previousPoint = PointChange.getMapPoint(mRouteWay.getLine().get(point));
//            GeodeticDistanceResult distanceResult = GeometryEngine.distanceGeodetic(previousPoint,loc,
//                    new LinearUnit(LinearUnitId.METERS), new AngularUnit(AngularUnitId.DEGREES), GeodeticCurveType.GEODESIC);
//            if (distanceResult.getDistance()<50){
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private void tellServerArrivePoint(String arrived_name) {
//        Map<String, String> maps = new HashMap<>();
//        maps.put("convoy_number", mRouteWay.getConvoy().getConvoyNo());
//        maps.put("arrived_time", System.currentTimeMillis() + "");
//        maps.put("arrived_name",arrived_name);
//        Gson gson = new Gson();
//        RequestModel requestModel = new RequestModel();
//        requestModel.setRequestName("arrivedPoint");
//        requestModel.setRequestparams(TextUtil.transMapToString(maps));
//        HttpRequestDBHepler.getInstance(mContext).saveHttpRequestMsg(requestModel);
//        ((OfflineNavigationActivity)mContext).runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(mContext,"You have reached \"+arrived_name+\" wayPoint.",Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//
//    private void tellServerArrived() {
//        Map<String, String> maps = new HashMap<>();
//        maps.put("convoy_number", mRouteWay.getConvoy().getConvoyNo());
//        maps.put("arrived_time", System.currentTimeMillis() + "");
//        Gson gson = new Gson();
//        RequestModel requestModel = new RequestModel();
//        requestModel.setRequestName("arrived");
//        requestModel.setRequestparams(TextUtil.transMapToString(maps));
//        HttpRequestDBHepler.getInstance(mContext).saveHttpRequestMsg(requestModel);
//        ((OfflineNavigationActivity)mContext).runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(mContext,"You have arrived at your destination",Toast.LENGTH_SHORT).show();
//            }
//        });
//        isEnd = false;
//    }
//
//
//
//    private void tellServerStart() {
//        Map<String, String> maps = new HashMap<>();
//        maps.put("convoy_number", mRouteWay.getConvoy().getConvoyNo());
//        maps.put("start_time", System.currentTimeMillis() + "");
//        Gson gson = new Gson();
//        RequestModel requestModel = new RequestModel();
//        requestModel.setRequestName("start");
//        requestModel.setRequestparams(TextUtil.transMapToString(maps));
//        HttpRequestDBHepler.getInstance(mContext).saveHttpRequestMsg(requestModel);
//        ((OfflineNavigationActivity)mContext).runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(mContext,"You've left the starting point",Toast.LENGTH_SHORT).show();
//            }
//        });
//        isStart = false;
//      }
//
//    private Point isNearestRoutePoint(Point location) {
//        if (mRoute != null){
//            ProximityResult proximityResult = GeometryEngine.nearestCoordinate(mRoute, location);
//            if (proximityResult.getDistance()<20){
//                return proximityResult.getCoordinate();
//            }
//        }
//        return location;
//    }

    }