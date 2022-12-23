package com.example.st.arcgiscss.upload;

import java.util.List;

public class UserCoordinate {

	private String imei;

	private String write_time;

	private String distance;

	private String convoyNumber;

	private String Longitude;

	private String Latitude;

	private int signalStrength = 0;

	private int satellite_count = 0;

	private int batteryCount = 0;

	private String appoint;

	private String timeRemaining;

	private String userId;

	private String pointId;

	private long locationtime;

	private boolean locChange = true;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTimeRemaining() {
		return timeRemaining;
	}

	public void setTimeRemaining(String timeRemaining) {
		this.timeRemaining = timeRemaining;
	}

	public UserCoordinate() {
	}

	public UserCoordinate(String distance, String convoyNumber) {

		this.distance = distance;
		this.convoyNumber = convoyNumber;
	}

	public String getConvyNumber() {
		return convoyNumber;
	}

	public void setConvyNumber(String convyNumber) {
		this.convoyNumber = convyNumber;
	}

	public String getWrite_time() {
		return write_time;
	}

	public void setWrite_time(String write_time) {
		this.write_time = write_time;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}


	public String getLongitude() {
		return Longitude;
	}

	public void setLongitude(String longitude) {
		Longitude = longitude;
	}

	public String getLatitude() {
		return Latitude;
	}

	public void setLatitude(String latitude) {
		Latitude = latitude;
	}


	public int getSignalStrength() {
		return signalStrength;
	}

	public void setSignalStrength(int signalStrength) {
		this.signalStrength = signalStrength;
	}

	public int getSatellite_count() {
		return satellite_count;
	}

	public void setSatellite_count(int satellite_count) {
		this.satellite_count = satellite_count;
	}

	public int getBatteryCount() {
		return batteryCount;
	}

	public void setBatteryCount(int batteryCount) {
		this.batteryCount = batteryCount;
	}

	public String getAppoint() {
		return appoint;
	}

	public void setAppoint(String appoint) {
		this.appoint = appoint;
	}


	public String getConvoyNumber() {
		return convoyNumber;
	}

	public void setConvoyNumber(String convoyNumber) {
		this.convoyNumber = convoyNumber;
	}


	public String getPointId() {
		return pointId;
	}

	public void setPointId(String pointId) {
		this.pointId = pointId;
	}

//	public List<NewLocaitonPoint> getNewLocaitonPoints() {
//		return newLocaitonPoints;
//	}
//
//	public void setNewLocaitonPoints(List<NewLocaitonPoint> newLocaitonPoints) {
//		this.newLocaitonPoints = newLocaitonPoints;
//	}

	public long getLocationtime() {
		return locationtime;
	}

	public void setLocationtime(long locationtime) {
		this.locationtime = locationtime;
	}


	public boolean isLocChange() {
		return locChange;
	}

	public void setLocChange(boolean locChange) {
		this.locChange = locChange;
	}
}



//package com.example.st.arcgiscss.upload;
//
//public class UserCoordinate {
//
//	private String imei;
//
//	private String write_time;
//
//	private String distance;
//
//	private String convoyNumber;
//
//	private String Longitude;
//
//	private String Latitude;
//
//	private int signalStrength = 0;
//
//	private int satellite_count = 0;
//
//	private int batteryCount = 0;
//
//	private String vehicleNo;
//
//	private String appoint;
//
//	private String timeRemaining;
//
//	public String getTimeRemaining() {
//		return timeRemaining;
//	}
//
//	public void setTimeRemaining(String timeRemaining) {
//		this.timeRemaining = timeRemaining;
//	}
//
//	public UserCoordinate() {
//	}
//
//	public UserCoordinate(String distance, String convoyNumber) {
//
//		this.distance = distance;
//		this.convoyNumber = convoyNumber;
//	}
//
//	public String getConvyNumber() {
//		return convoyNumber;
//	}
//
//	public void setConvyNumber(String convyNumber) {
//		this.convoyNumber = convyNumber;
//	}
//
//	public String getWrite_time() {
//		return write_time;
//	}
//
//	public void setWrite_time(String write_time) {
//		this.write_time = write_time;
//	}
//
//	public String getImei() {
//		return imei;
//	}
//
//	public void setImei(String imei) {
//		this.imei = imei;
//	}
//
//	public String getDistance() {
//		return distance;
//	}
//
//	public void setDistance(String distance) {
//		this.distance = distance;
//	}
//
//
//
//
//	public String getVehicleNo() {
//		return vehicleNo;
//	}
//
//	public void setVehicleNo(String vehicleNo) {
//		this.vehicleNo = vehicleNo;
//	}
//
//
//	public String getLongitude() {
//		return Longitude;
//	}
//
//	public void setLongitude(String longitude) {
//		Longitude = longitude;
//	}
//
//	public String getLatitude() {
//		return Latitude;
//	}
//
//	public void setLatitude(String latitude) {
//		Latitude = latitude;
//	}
//
//
//	public int getSignalStrength() {
//		return signalStrength;
//	}
//
//	public void setSignalStrength(int signalStrength) {
//		this.signalStrength = signalStrength;
//	}
//
//	public int getSatellite_count() {
//		return satellite_count;
//	}
//
//	public void setSatellite_count(int satellite_count) {
//		this.satellite_count = satellite_count;
//	}
//
//	public int getBatteryCount() {
//		return batteryCount;
//	}
//
//	public void setBatteryCount(int batteryCount) {
//		this.batteryCount = batteryCount;
//	}
//
//	public String getAppoint() {
//		return appoint;
//	}
//
//	public void setAppoint(String appoint) {
//		this.appoint = appoint;
//	}
//}
