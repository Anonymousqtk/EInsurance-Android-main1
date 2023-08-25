package com.pvi.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Reachability {

	/**
	 * Checks if internet (mobile data or wifi) is available or not
	 * 
	 * @return
	 */
	public static boolean isInternetAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] infos = cm.getAllNetworkInfo();
		if (infos != null && infos.length > 0) {
			for (NetworkInfo info : infos) {
				if (info.isConnected()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks if Wifi network is available or not
	 * 
	 * @return true when wifi available, false for other
	 */
	public static boolean isWifiAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		Log.i("test", "isWifiConnected: " + (info.isConnected() ? "true" : "false"));
		return (info != null && info.isConnected());
	}
}
