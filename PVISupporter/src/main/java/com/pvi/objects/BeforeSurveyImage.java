package com.pvi.objects;

import java.io.Serializable;

public class BeforeSurveyImage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String objectID, userName,
			fileName, takenDay, longitude, latitude;
	private String uploadState;

	public BeforeSurveyImage(String objectID, String userName, String fileName, String takenDay,
			String longitude, String latitude, String uploadState) {
		this.objectID = objectID;
		this.userName = userName;
		this.fileName = fileName;
		this.takenDay = takenDay;
		this.longitude = longitude;
		this.latitude = latitude;
		this.uploadState = uploadState;
	}

	public String getObjectID() {
		return this.objectID;
	}
	public String getUserName() {
		return this.userName;
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

	public String getUploadState() {
		return this.uploadState;
	}
	public void setUploadState(String states) {
		this.uploadState = states;
	}

}
