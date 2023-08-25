package com.pvi.objects;

import java.io.Serializable;

public class BeforeSurveyOfflineObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String licensePlate, serialNumber;
	private String primaryKey;
	
	public BeforeSurveyOfflineObject(String licensePlate, String serialNumber) {
		this.licensePlate = licensePlate;
		this.serialNumber = serialNumber;
	}
	
	public String getLicensePlate() {
		return this.licensePlate;
	}
	public String getSerialNumber() {
		return this.serialNumber;
	}
	public String getPrimaryKey() {
		return this.primaryKey;
	}
	public void setPrimaryKey(String prKey) {
		this.primaryKey = prKey;
	}

}
