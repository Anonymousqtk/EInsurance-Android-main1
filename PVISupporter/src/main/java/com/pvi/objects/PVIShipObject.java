package com.pvi.objects;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class PVIShipObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String objectID, userName;
	
	@SerializedName("ten_tau")
	public String shipName;
	
	@SerializedName("so_dky")
	public String shipCode;
	
	@SerializedName("dang_kiem")
	public String registration;

	@SerializedName("ngaydau_hluc")
	public String startDay;
	
	@SerializedName("ngaycuoi_hluc")
	public String endDay;
	
	public PVIShipObject(String shipName, String shipCode, String registration, String objectID, String userName) {
		this.shipCode = shipCode;
		this.shipName = shipName;
		this.registration = registration;
		this.objectID = objectID;
		this.userName = userName;
	}
	
	public String getStartDay() {
		return startDay;
	}
	public String getEndDay() {
		return endDay;
	}
	public String getShipName() {
		return shipName;
	}
	public String getShipCode() {
		return shipCode;
	}
	public String getRegistration() {
		return registration;
	}
	public String getObjectID() {
		return objectID;
	}
	public void setObjectID(String objectID) {
		this.objectID = objectID;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

}
