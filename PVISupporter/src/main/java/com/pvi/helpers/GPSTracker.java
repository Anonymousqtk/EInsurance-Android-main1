package com.pvi.helpers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

public class GPSTracker {

	private final Context mContext;

	// flag for GPS status
	boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;

	// flag for GPS status
	boolean canGetLocation = false;

	private Location location; // location
	private double latitude; // latitude
	private double longitude; // longitude
	private String dateFormatted;

	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 3000; // 3 seconds

	// Declaring a Location Manager
	protected LocationManager locationManager;

	public GPSTracker(Context context) {
		this.mContext = context;
		getLocation();
	}

	public Location getLocation() {
		try {
			locationManager = (LocationManager) mContext
					.getSystemService(Context.LOCATION_SERVICE);

			// getting GPS status
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled || !isNetworkEnabled) {
				// no network provider is enabled
				this.canGetLocation = false;
			} else {
				this.canGetLocation = true;
				if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerNetwork);
					Log.d("Network", "Network");
					if (locationManager != null) {
						this.location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (this.location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					if (this.location == null) {
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerGps);
						Log.d("GPS Enabled", "GPS Enabled");
						if (locationManager != null) {
							this.location = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (this.location != null) {
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return this.location;
	}

	/**
	 * Stop using GPS listener Calling this function will stop using GPS in your
	 * app
	 * */
	public void stopUsingGPS() {
		if (locationManager != null) {
			locationManager.removeUpdates(locationListenerGps);
			locationManager.removeUpdates(locationListenerNetwork);
		}
	}

	/**
	 * Function to get latitude
	 * */
	public String getLatitude() {
		if (location != null) {
			latitude = location.getLatitude();
		}

		// return latitude
		return Double.toString(latitude);
	}

	/**
	 * Function to get longitude
	 * */
	public String getLongitude() {
		if (location != null) {
			longitude = location.getLongitude();

		}

		// return longitude
		return Double.toString(longitude);
	}
	
	/**
	 * Function to get time
	 * */
	public String getTime() {
		if (location != null) {
			DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", java.util.Locale.US);
			dateFormatted = format.format(location.getTime());
		}
		
		return dateFormatted;
	}
	
	public String getTimeForImageName() {
		if (location != null) {
			DateFormat format = new SimpleDateFormat("ddMMyyyy HHmmss", Locale.US);
			Date date = new Date(location.getTime());
			dateFormatted = format.format(date);
		}
		
		return dateFormatted;
	}

	/**
	 * Function to check GPS/wifi enabled
	 * 
	 * @return boolean
	 * */
	public boolean canGetLocation() {
		return this.canGetLocation;
	}

	/**
	 * Function to show settings alert dialog On pressing Settings button will
	 * lauch Settings Options
	 * */
	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

		// Setting Dialog Title
		alertDialog.setTitle("Cài đặt GPS");

		// Setting Dialog Message
		alertDialog
				.setMessage("Định vị GPS chưa được bật. Bạn muốn bật định vị để tiếp tục thao tác này?");

		// On pressing Settings button
		alertDialog.setPositiveButton("Cài đặt",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						mContext.startActivity(intent);
						 ((Activity)mContext).finish();
					}
				});

		// on pressing cancel button
		alertDialog.setNegativeButton("Hủy",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		alertDialog.setCancelable(false);
		// Showing Alert Message
		alertDialog.show();
	}

	// ***************** Override method *************************//
	
	LocationListener locationListenerGps = new LocationListener() {
		public void onLocationChanged(Location location) {
			setLocation(location);
			locationManager.removeUpdates(this);
			locationManager.removeUpdates(locationListenerNetwork);
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	LocationListener locationListenerNetwork = new LocationListener() {
		public void onLocationChanged(Location location) {
			setLocation(location);
			locationManager.removeUpdates(this);
			locationManager.removeUpdates(locationListenerGps);
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};
	
	private void setLocation(Location location) {
		this.location = location;
	}

}
