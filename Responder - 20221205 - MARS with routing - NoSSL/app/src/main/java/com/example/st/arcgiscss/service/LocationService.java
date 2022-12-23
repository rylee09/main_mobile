package com.example.st.arcgiscss.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.GpsStatus.NmeaListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.st.arcgiscss.constant.Constants;
import com.example.st.arcgiscss.constant.MyApplication;
import com.example.st.arcgiscss.model.MyPoint;
import com.example.st.arcgiscss.util.ACache;
import com.example.st.arcgiscss.util.FileTools;
import com.example.st.arcgiscss.util.LocationUtil;

import java.net.DatagramSocket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class LocationService extends Service implements LocationListener {
	private static final String TAG = "GpsActivity";
	private boolean isSend = true;
	private MyApplication application;
	private LocationManager locManager;
	private String gps = LocationManager.GPS_PROVIDER;
	private Handler handler = null;
	private Context context;
	private String provider;
	private String net = LocationManager.NETWORK_PROVIDER;
	private ACache cache;
	private String nmea;
	private FileTools fileUtils;
	private WakeLock wakeLock;
	//Socket communication based on UDP protocol
	private DatagramSocket ds;
	//	public static String host = "";
//	public static int port = 0;
	private static String deviceId;

	private Thread socketThread = null;

	private boolean gps_enabled = false;
	private boolean network_enabled = false;
	private String current_provider;

	//ZN - 2021022
	private boolean is_GPS_active = false;

	private GpsStatus.NmeaListener mNmeaListener = new NmeaListener() {
		@Override
		public void onNmeaReceived(long timestamp, String nmea) {
			if (nmea.contains("$GPRMC")) {
//				System.out.println(nmea + "----------------------");
				setNMEA(nmea);
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onCreate() {
		application = MyApplication.getInstance();
//		host = application.getSOCKET_SERVER();
//		port = application.getPORT();
//		fileUtils = FileTools.getInstance(this);
//		if (fileUtils.readSharedPreference("ip_socket_server") != null &&
//				fileUtils.readSharedPreference("port_socket_server") != null ) {
//			if(fileUtils.readSharedPreference("port_socket_server") != null && !fileUtils.readSharedPreference("port_socket_server").trim().equals(""))
//			{
//				host = fileUtils.readSharedPreference("ip_socket_server");
//				port = Integer.parseInt(fileUtils.readSharedPreference("port_socket_server"));
//			}
//		}else{
//			host = application;
//			port = URL.PORT;
//		}
		acquireWakeLock();
		System.out.println("Location service on");
		cache = ACache.get(this);
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		gps_enabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		network_enabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		System.out.println("GPS enabled: " + gps_enabled);
		System.out.println("Network enabled: " + network_enabled);

		createExt();
		TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		deviceId = telManager.getSubscriberId();
//		deviceId = get(context, "android.telephony.TelephonyProperties.PROPERTY_IMSI");
		super.onCreate();
//		System.out.println("-----------" + deviceId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		isSend = true;
		Log.i("Service", "[LocationService] services started");
		System.out.println("LocationService implement onStartCommand");
		//	locManager.removeUpdates(this);

		//ZN - 20210129 to include network for location info
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		//criteria.setAltitudeRequired(true);
		//criteria.setBearingRequired(true);
		//criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_MEDIUM);

		//ZN - 20210129 get best provider on app start
		String bestProvider = locManager.getBestProvider(criteria, true);
		Toast.makeText(this, "Best provider on start: " + bestProvider, Toast.LENGTH_LONG).show();
		System.out.println("Best provider on start: " + bestProvider);
		current_provider = bestProvider;

//		System.out.println("LocationService best provider on start: " + bestProvider);
//		System.out.println("LocationService using GPS: " + locManager.isProviderEnabled(gps));
//		System.out.println("LocationService using Network: " + locManager.isProviderEnabled(net));

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
		}

		//ZN - 20210131 - register with best location provider on startup
		//locManager.requestLocationUpdates(bestProvider, 1000, 5, this);
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, this);
		locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3333, 5, this);

//		System.out.println("GPS enabled: " + gps_enabled);

		//	ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		ConnectivityManager mgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		//get network (wifi, cellular)
//		NetworkInfo netInfo = mgr.getActiveNetworkInfo();
//
//		if (netInfo != null) {
//			System.out.println("NETWORK INFO IS NOT NULL");
//			network_enabled = true;
//			gps_enabled = false;
//
//			locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 5, this);
//		} else {
//			if (gps_enabled) {
//				System.out.println("GPS IS ENABLED");
//				gps_enabled = true;
//				network_enabled = false;
//
//				locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, this);
//			} else {
//				System.out.println("NO NETWORK CAN BE FOUND!!!!");
//			}
//		}

		//	if(network_enabled) {
		//	}

		locManager.addNmeaListener(mNmeaListener);

		//ZN - 20210129 removed
//		if (locManager.isProviderEnabled(gps) || locManager.isProviderEnabled(net)) {
//
////            provider = locManager.getBestProvider(criteria, true);
////            Toast.makeText(this, provider, Toast.LENGTH_SHORT).show();
////            System.out.println(provider);
////            locManager.requestLocationUpdates(provider, 1000, 5, this);
//			if (locManager.isProviderEnabled(gps)) {
//				if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//					// TODO: Consider calling
//					//    ActivityCompat#requestPermissions
//					// here to request the missing permissions, and then overriding
//					//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//					//                                          int[] grantResults)
//					// to handle the case where the user grants the permission. See the documentation
//					// for ActivityCompat#requestPermissions for more details.
//
//				}
//
//				locManager.requestLocationUpdates(gps, 1000, 5, this);
//				locManager.addNmeaListener(mNmeaListener);
//				Toast.makeText(this, "Gps location has been opened.", Toast.LENGTH_SHORT).show();
//			} else {
//				if (locManager.isProviderEnabled(net)) {
//
//					locManager.requestLocationUpdates(net, 1000, 5, this);
//					Toast.makeText(this, "Network location has been opened.", Toast.LENGTH_SHORT).show();
//				}
//			}
//		} else {
//			System.out.println("LocationService No position information");
//			Toast.makeText(this, "Unable to obtain position.", Toast.LENGTH_SHORT).show();
//		}

		//ZN - 20210129 removed
//		socketThread = new Thread() {
//			@Override
//			public void run() {
//				Looper.prepare();
//				sendNMEA();
//				Looper.loop();
//			}
//		};
//		socketThread.start();

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		releaseWakeLock();
		System.out.println("Location service shutdown");
		isSend = false;
		locManager.removeUpdates(this);
//		Toast.makeText(this, "LocationService is closed!", Toast.LENGTH_SHORT).show();
		Log.i("Service", "[LocationService] services stopped");
		super.onDestroy();
//		try {
//			socketThread.interrupt();
//			socketThread.join();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

	}

	@Override
	public void onLocationChanged(Location location) {
		System.out.println("---------onLocationChanged by: " + location.getProvider());
		//if (location.getProvider().equalsIgnoreCase(LocationManager.NETWORK_PROVIDER && isGPSactive));
		//ZN - 20210131 check if location is better location, if not better then ignore
//		Location currentLoc = MyApplication.getInstance().getCurrentLocation();
//		if (!isBetterLocation(location, currentLoc))
//			return;

		Intent intent = new Intent();
		intent.setAction(Constants.LOCSERVER);
		Bundle bundle = new Bundle();
		bundle.putParcelable("location", location);
		intent.putExtras(bundle);
//		STNApplication.getSTNApplication().setCurrentPoint(new GeoPoint(location));

		MyApplication.getInstance().setCurrentPoint(new MyPoint(location.getLatitude() + "", location.getLongitude() + ""));
		MyApplication.getInstance().getLocalBroadcastManager().sendBroadcast(intent);
//		cache.put("location", new GeoPoint(location));

		//ZN - 20210131 set current location object for subsequent update comparison of better location
		MyApplication.getInstance().setCurrentLocation(location);
		nmea = generateNmea(location);
		System.out.println("-------------------" + nmea);

//		if (!locManager.isProviderEnabled(gps)) {
//			if (locManager.isProviderEnabled(net)) {
//				nmea = generateNmea(location);
//			}
//		}
//		if (nmea == null || nmea.contains(",V,")) {
//			nmea = generateNmea(location);
//		}
//		if (nmea == null && locManager != null) {
//			nmea = generateNmea(LocationUtil.getBestLocation(locManager, this));
//		}
//		System.out.println("-------------------" + nmea);

		//ZN - 20210219 get best provider now
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		//criteria.setAltitudeRequired(true);
		//criteria.setBearingRequired(true);
		//criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
		String bestProvider = locManager.getBestProvider(criteria, true);

		//System.out.println("---------onLocationChanged");
		System.out.println("Best Provider: " + bestProvider);
		System.out.println("Current Provider: " + current_provider);

		//ZN - 20210219 replace provider is getBestProvider is different from currentProvider
		if (!bestProvider.equalsIgnoreCase(current_provider)) {
			System.out.println("Change provider on location change: " + current_provider + " -> " + bestProvider);
			Toast.makeText(this, "Change provider on location change: " + current_provider + " -> " + bestProvider , Toast.LENGTH_LONG).show();
			current_provider = bestProvider;
			locManager.removeUpdates(this);
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return;
			}
			locManager.requestLocationUpdates(current_provider, 1000, 5, this);
		}
	}

	/**
	 * Triggered when GPS status changes
	 */
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
//		Toast.makeText(this, "status changed:" + status, Toast.LENGTH_SHORT).show();
		switch (status) {
			case LocationProvider.OUT_OF_SERVICE:
				//When GPS status is outside the service area
				Log.i(TAG, "Current GPS status is out of service.");
				Toast.makeText(this, "Current GPS status is out of service.", Toast.LENGTH_LONG).show();
				break;
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				//When GPS status is out of service
				Log.i(TAG, "The current GPS status is suspended");
				Toast.makeText(this, "Current GPS status is unavailable", Toast.LENGTH_LONG).show();
				break;
			case LocationProvider.AVAILABLE:
				//When GPS status is visible
				Log.i(TAG, "Current GPS status is available.");
				Toast.makeText(this, "The current GPS status is visible", Toast.LENGTH_SHORT).show();
				break;
		}
	}

	/**
	 * Triggered when GPS is on
	 */
	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "The current provider is enabled: " + provider, Toast.LENGTH_SHORT).show();
		//Turning on GPS monitoring
		//remove existing network listener
		//locManager.removeUpdates(this);
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		locManager.requestLocationUpdates(gps, 1000, 5, this);
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		locManager.addNmeaListener(mNmeaListener);
	}

	@Override
	public void onProviderDisabled(String provider) {
		//This means GPS is turned off
		//Turn on network monitoring
		Toast.makeText(this, "The current provider is disabled: " + provider, Toast.LENGTH_SHORT).show();
		locManager.removeNmeaListener(mNmeaListener);
		locManager.removeUpdates(this);
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}

		if(network_enabled) {
			gps_enabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

			if(gps_enabled) {
			//	locManager.requestLocationUpdates(net, 1000, 5, this);
				locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, this);
			}
		}
	}

	public void createExt() {
		try {
			ds = new DatagramSocket();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (locManager.isProviderEnabled(gps)) {
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return;
			}
			locManager.addNmeaListener(mNmeaListener);
		}
//		else{
//			Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
//		}
	}

	public void setNMEA(String nmea) {
		this.nmea = nmea;
	}

	public void sendNMEA() {

		handler = new Handler();
		context = this;
//		Runnable runnable = new Runnable() {
//
//			@Override
//			public void run() {
//				handler.postDelayed(this, application.getFrequency() * 1000);
//				try {
////					System.out.println(application.getSOCKET_SERVER() + "---------------------" + application.getPORT());
////					System.out.println("--------" + "$DEVID,0x" + deviceId + "*99" + nmea);
//					if (nmea != null && nmea.contains(",A,")) {
//						DatagramPacket dp = new DatagramPacket(("$DEVID,0x" + deviceId + "*99" + nmea).getBytes(), ("$DEVID,0x" + deviceId + "*99" + nmea).getBytes().length, InetAddress.getByName(application.getSOCKET_SERVER()), application.getPORT());
//						if (checkNet(context)) {
//							ds.send(dp);
//						}
//					}
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//
//			}
//		};
//		runnable.run();

//		while (isSend){
//			try {
//				String frequency = application.getFrequency() * 1000 + "";
//				if(frequency != null && !"".equals(application.getFrequency())){
//					Thread.currentThread().sleep(application.getFrequency() * 1000);
//				}
//				System.out.println(application.getSOCKET_SERVER() + "---------------------" + application.getPORT());
//				System.out.println("--------" + nmea);
//				if(nmea != null && nmea.contains(",A,"))
//				{
//					DatagramPacket dp = new DatagramPacket(("$DEVID,0x" + deviceId + "*99" + nmea).getBytes(), ("$DEVID,0x" + deviceId + "*99" + nmea).getBytes().length, InetAddress.getByName(application.getSOCKET_SERVER()), application.getPORT());
//					if(checkNet(this))
//					{
//						ds.send(dp);
//					}
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
	}

	public String getLocaldeviceId(Context _context) {
		TelephonyManager tm = (TelephonyManager) _context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.

		}
		String deviceId = tm.getDeviceId();
		if (deviceId == null
				|| deviceId.trim().length() == 0) {
			deviceId = String.valueOf(System
					.currentTimeMillis());
		}
		return deviceId ;
	}
	public String generateNmea(Location location)
	{
		SimpleDateFormat sdf1 = new SimpleDateFormat("hhmmss.sss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("ddMMyy");
		sdf1.setTimeZone(TimeZone.getTimeZone("Etc/GMT+0"));
		sdf2.setTimeZone(TimeZone.getTimeZone("Etc/GMT+0"));
		StringBuilder sb = new StringBuilder("$GPRMC,");
		long timestamp = location.getTime();
//		System.out.println("----------------" + timestamp);
		Date date = new Date(timestamp);
//		System.out.println("----------------" + date);
		//System.out.println("----------------" + timestamp);
		//System.out.println("----------------" + date);
		sb.append(sdf1.format(date) + ",");
		sb.append("A,");
		sb.append((int)location.getLatitude() + "" + (60 * (location.getLatitude() - (int)location.getLatitude())) + ",");
		sb.append((location.getLatitude() >= 0 ? "N" : "S") + ",");
		sb.append((int)location.getLongitude() + "" + (60 * (location.getLongitude() - (int)location.getLongitude())) + ",");
		sb.append((location.getLongitude() >= 0 ? "E" : "W") + ",");
		sb.append(location.getSpeed() + ",,");
		sb.append(sdf2.format(date) + ",");
		sb.append(location.getBearing() + ",");
		sb.append(((location.getBearing() >= 0) ? "E" : "W") + ",");
		sb.append("E*99");
		return sb.toString();
	}
//	private Location getBestLocation(LocationManager locationManager) {  
//        Location result = null;  
//        if (locationManager != null) {  
//            result = locationManager  
//                    .getLastKnownLocation(gps);  
//            if (result != null) {  
//                return result;  
//            } else {  
//                result = locationManager  
//                        .getLastKnownLocation(net);  
//                return result;  
//            }  
//        }  
//        return result;  
//    }
	private boolean checkNet(Context context) {
		try {  
			ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {  
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {  

					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;  
					}  
				}  
			}  
		} catch (Exception e) {
			return false;  
		}  
		return false;  
	}
	private void acquireWakeLock() {       
		if (wakeLock == null) {         
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getCanonicalName());
			wakeLock.acquire();     
		}   
	}
	private void releaseWakeLock() {     
		if (wakeLock != null && wakeLock.isHeld()) {      
			wakeLock.release();      
			wakeLock = null;   
		} 
	}

	//ZN - 20210131 - Determines whether one Location reading is better than the current Location fix
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > 2;
		boolean isSignificantlyOlder = timeDelta < -2;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
			return true;
		}
		return false;
	}

	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

//	public static String get(Context context, String key) {
//	    String ret = "";
//
//	    try {
//	        ClassLoader cl = context.getClassLoader(); 
//	        @SuppressWarnings("rawtypes")
//	        Class SystemProperties = cl.loadClass("android.os.SystemProperties");
//
//	        //Parameters Types
//	        @SuppressWarnings("rawtypes")
//	        Class[] paramTypes= new Class[1];
//	        paramTypes[0]= String.class;
//
//	        Method get = SystemProperties.getMethod("get", paramTypes);
//
//	        //Parameters
//	        Object[] params = new Object[1];
//	        params[0] = new String(key);
//
//	        ret = (String) get.invoke(SystemProperties, params);
//	    } catch(Exception e) {
//	        ret = "";
//	    }
//
//	    return ret;
//	}
}
