package com.pvi.helpers;

import java.util.ArrayList;
import java.util.List;

public class GlobalData {

	private static GlobalData _instance;

	private String userName, currentPhotoPath, unitCode, typeUser, version, url, currentDownloadPath;
	public List<Integer> uploadIndexes = new ArrayList<Integer>();
	private boolean isChecked;

	protected GlobalData() {}
	// create singleton instance
	public static synchronized GlobalData getInstance() {
		if (_instance == null) {
			_instance = new GlobalData();
		}
		return _instance;
	}

	public String getUnitCode() {
		return this.unitCode;
	}
	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}
	
	public String getUsername() {
		return this.userName;
	}
	public void setUsername(String userName) {
		this.userName = userName;
	}


	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	
	public String getCurrentPhotoPath() {
		return currentPhotoPath;
	}

	public void setCurrentPhotoPath(String currentPhotoPath) {
		this.currentPhotoPath = currentPhotoPath;
	}

	public String getTypeUser() {
		return typeUser;
	}

	public void setTypeUser(String typeUser) {
		this.typeUser = typeUser;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCurrentDownloadPath() {
		return currentDownloadPath;
	}

	public void setCurrentDownloadPath(String currentDownloadPath) {
		this.currentDownloadPath = currentDownloadPath;
	}
}
