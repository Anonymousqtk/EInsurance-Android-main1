package com.pvi.objects;

import java.io.Serializable;

public class BeforeSurveyOfflineImage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String frKey, licensePlate, fileName, takenDay, longitude, latitude;

	public BeforeSurveyOfflineImage(String frKey,String licensePlate,String fileName,
			String takenDay,String longitude,String latitude) {
		
		this.frKey = frKey;
		this.licensePlate = licensePlate;
		this.fileName = fileName;
		this.takenDay = takenDay;
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	public String getFrKey() {
		return this.frKey;
	}
	public String getLicensePlate() {
		return this.licensePlate;
	}
	public String getFileName() {
		return this.fileName;
	}
	public String getTakenDay() {
		return this.takenDay;
	}
	public String getLongitude() {
		return this.longitude;
	}
	public String getLatitude() {
		return this.latitude;
	}
}
