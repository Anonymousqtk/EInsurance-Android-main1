package com.pvi.objects;

import java.io.Serializable;

public class BeforSurveyObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String customerName, notes, userName;
	private String  licensePlate, vehicleNumber, serialNumber, objectID,policyInsurance;

	public BeforSurveyObject(String licensePlate, String vehicleNumber, String serialNumber,
			String customerName, String notes, String userName, String policyInsurance) {
		this.licensePlate = licensePlate;
		this.vehicleNumber = vehicleNumber;
		this.serialNumber = serialNumber;
		this.customerName = customerName;
		this.notes = notes;
		this.userName = userName;
		this.policyInsurance = policyInsurance;
	}

	public String getUserName() {
		return userName;
	}
	public String getLicensePlate() {
		return this.licensePlate;
	}
	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getVehicleNumber() {
		return this.vehicleNumber;
	}
	public String getSerialNumber() {
		return this.serialNumber;
	}

	public String getCustomerName() {
		return this.customerName;
	}

	public String getNotes() {
		return this.notes;
	}

	public String getObjectID() {
		return this.objectID;
	}

	public void setObjectID(String objectID) {
		this.objectID = objectID;
	}

	public void setPolicyInsurance(String policyInsurance) {
		this.policyInsurance = policyInsurance;
	}
	public String getPolicyInsurance() {
		return this.policyInsurance;
	}
}
