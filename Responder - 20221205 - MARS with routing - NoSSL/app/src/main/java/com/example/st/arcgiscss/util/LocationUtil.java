package com.example.st.arcgiscss.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.util.List;

public class LocationUtil {

    public static Location getBestLocation(LocationManager locationManager,Context context) {
        Location result = null;
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//        }
//        Criteria c=new Criteria();
//        c.setAccuracy(Criteria.ACCURACY_FINE);
//        c.setAltitudeRequired(false);
//        c.setBearingRequired(false);
//        c.setCostAllowed(true);
//        c.setPowerRequirement(Criteria.POWER_LOW);
//        String provider = locationManager.getBestProvider(c, true);
//        Location location = locationManager.getLastKnownLocation(provider);
//        return location;

        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.e("TEST", "Unauthorized");
            }

            String  provider;
            List<String> providerList = locationManager.getProviders(true);
            if (providerList.contains(LocationManager.GPS_PROVIDER)) {
                provider = LocationManager.GPS_PROVIDER;
            } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
                provider = LocationManager.NETWORK_PROVIDER;
            } else {
                Toast.makeText(context, "No available location provider", Toast.LENGTH_SHORT).show();
                return result;
            }
            result = locationManager
                    .getLastKnownLocation(provider);
        }
        return result;
    }



//    public static Location getLastKnownLocation(LocationManager locationManager, Context context) {
//        List<String> providers = locationManager.getProviders(true);
//        Location bestLocation = null;
//        for (String provider : providers) {
//            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//            }
//            Location l = locationManager.getLastKnownLocation(provider);
//            if (l == null) {
//                continue;
//            }
//            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
//                // Found best last known location: %s", l);
//                bestLocation = l;
//            }
//        }
//        return bestLocation;
//    }
}
