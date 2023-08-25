package com.pvi.helpers;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.Uri;

public class GlobalMethod {
	private static final String kMajor 	= "kMajor";
	public static final int CAR_MAJOR 	= 0;
	public static final int SHIP_MAJOR 	= 1;
	public static final int MOTO_MAJOR 	= 2;
	public static final String GDDK_STORE = "GDDK_STORE";
	
	public static void installNewUpdate(Context cx, String path) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
		cx.startActivity(intent);
	}

	/**
	 * check version from server and current version
	 * 
	 * @param ctx
	 * @param newVersion
	 * @return
	 */
	public static boolean needUpdateNewVersion(Context ctx, String newVersion) {
		String currentVersion = "0";
		try {
			currentVersion = ctx.getPackageManager().getPackageInfo(
					ctx.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean needForUpdate = false;
		String s1 = normalisedVersion(currentVersion);
		String s2 = normalisedVersion(newVersion);
		int compareValue = s1.compareTo(s2);

		if (compareValue < 0) {
			needForUpdate = true;
		}

		return needForUpdate;
	}

	private static String normalisedVersion(String version) {
		return normalisedVersion(version, ".", 4); // max version string length
													// is 4 (eg. 1.1.0.1)
	}

	private static String normalisedVersion(String version, String sep,
			int maxWidth) {
		String[] split = Pattern.compile(sep, Pattern.LITERAL).split(version);
		StringBuilder sb = new StringBuilder();
		for (String s : split) {
			sb.append(String.format("%" + maxWidth + 's', s));
		}
		return sb.toString();
	}
	
	public static void saveMajorType(int majorType, int mode, Context context) {
		SharedPreferences pre = context.getSharedPreferences(GDDK_STORE, mode);
		SharedPreferences.Editor editor = pre.edit();
		editor.putInt(kMajor, majorType);
		editor.commit();
	}
	public static int getMajorType(Context context, int mode) {
		SharedPreferences pre = context.getSharedPreferences(GDDK_STORE, mode);
		int value = pre.getInt(kMajor, CAR_MAJOR);
		return value;
	}

	public static boolean isServiceRunning(Class<?> serviceClass, Activity ctx) {
		ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public static boolean isMobileNetworkConnected(Activity ctx) {
		boolean mobileDataEnabled = false; // Assume disabled
		ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		try {
			Class cmClass = Class.forName(cm.getClass().getName());
			Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
			method.setAccessible(true); // Make the method callable
			// get the setting for "mobile data"
			mobileDataEnabled = (Boolean)method.invoke(cm);
		} catch (Exception e) {
			// Some problem accessible private API
			// TODO do whatever error handling you want here
		}
		return mobileDataEnabled;
	}
}
