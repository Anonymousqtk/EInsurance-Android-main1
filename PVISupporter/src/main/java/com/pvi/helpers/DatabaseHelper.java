package com.pvi.helpers;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pvi.objects.BeforSurveyObject;
import com.pvi.objects.BeforeSurveyImage;
import com.pvi.objects.BeforeSurveyOfflineImage;
import com.pvi.objects.BeforeSurveyOfflineObject;
import com.pvi.objects.PVIShipObject;

public class DatabaseHelper extends AbstractDatabaseManager {

	public static final String LOG_TAG = DatabaseHelper.class.getSimpleName();

	/* ----------------------------------------------------------------------- */
	/* Static fields */
	/* ----------------------------------------------------------------------- */
	private static final String DATABASE_NAME = "database.db";
	// private static final int DATABASE_VERSION = 1;
	private static final int DATABASE_VERSION = 2;

	// Tables
	private static final String RESUME_TABLE = "RESUME";
	private static final String PICTURE_TABLE = "PICTURE";
	private static final String RESUME_OFFLINE_TABLE = "RESUME_OFFLINE";
	private static final String IMAGE_OFFLINE_TABLE = "IMAGE_OFFLINE";

	private static DatabaseHelper writeAdapter;

	protected DatabaseHelper(Context ctx, boolean allowWrite) {
		super(ctx, allowWrite);
		// TODO Auto-generated constructor stub
	}

	// singleton: writable method
	public static DatabaseHelper getWritableInstance(Context ctx) {
		// TODO: get writable database
		if (writeAdapter == null)
			writeAdapter = new DatabaseHelper(ctx, true);
		return writeAdapter;
	}

	// singleton Readable method
	public static DatabaseHelper getReadableInstance(Context ctx) {
		// TODO: Get readable database
		return new DatabaseHelper(ctx, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.pvi.helpers.AbstractDatabaseManager#onCreateDatabase(android.database
	 * .sqlite.SQLiteDatabase): is called whenever the app is freshly installed.
	 */
	@Override
	public void onCreateDatabase(SQLiteDatabase db) {
		// TODO: Create database and index tables
		super.onCreateDatabase(db);
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.pvi.helpers.AbstractDatabaseManager#onUpgradeDatabase(android.database
	 * .sqlite.SQLiteDatabase, int, int): is called whenever the app is upgraded
	 * and launched and the database version is not the same.
	 */
	@Override
	public void onUpgradeDatabase(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		// update version from 1 to 2
		// Steps:
		// - create temp table
		// - insert all data to temp table
		// - create new table
		// - move data to new table
		// - drop temp table
		db.execSQL("DROP TABLE IF EXISTS tmp_" + RESUME_TABLE);
		String sqlTmp = getSQLCreateTable("tmp_" + RESUME_TABLE, new SQLPair("id", "INTEGER"), new SQLPair("objectID", "TEXT"),
				new SQLPair("lisencePlate", "TEXT"), new SQLPair("vehicleNumber", "TEXT"),
				new SQLPair("serialNumber", "TEXT"), new SQLPair("customerName", "TEXT"), new SQLPair("notes", "TEXT"),
				new SQLPair("userName", "TEXT"));
		db.execSQL(sqlTmp);
		db.execSQL("INSERT INTO " + "tmp_" + RESUME_TABLE + " SELECT * FROM " + RESUME_TABLE);
		db.execSQL("DROP TABLE " + RESUME_TABLE);

		String sqlResumeTable = getSQLCreateTable(RESUME_TABLE, new SQLPair("id", "INTEGER PRIMARY KEY"), new SQLPair("objectID", "TEXT"),
				new SQLPair("lisencePlate", "TEXT"), new SQLPair("vehicleNumber", "TEXT"), new SQLPair("serialNumber", "TEXT"),
				new SQLPair("customerName", "TEXT"),
				new SQLPair("notes", "TEXT"), new SQLPair("userName", "TEXT"),
				new SQLPair("majorType", "TEXT"), new SQLPair("shipCode", "TEXT"),
				new SQLPair("shipName", "TEXT"), new SQLPair("registrationInfo", "TEXT"));
		db.execSQL(sqlResumeTable);
		db.execSQL("INSERT INTO " + RESUME_TABLE + " SELECT *, 'AUTO', \"0\", \"0\", \"0\" FROM " + "tmp_" + RESUME_TABLE);
		db.execSQL("DROP TABLE tmp_" + RESUME_TABLE);

		/*------------ ResumeOffline table------------*/
		db.execSQL("DROP TABLE IF EXISTS tmp_" + RESUME_OFFLINE_TABLE);
		String sqlResumeOfflineTmp = getSQLCreateTable("tmp_" + RESUME_OFFLINE_TABLE, new SQLPair("id", "INTEGER"),
				new SQLPair("licensePlate", "TEXT"), new SQLPair("serialNumber", "TEXT"));
		db.execSQL(sqlResumeOfflineTmp);
		db.execSQL("INSERT INTO " + "tmp_" + RESUME_OFFLINE_TABLE + " SELECT * FROM " + RESUME_OFFLINE_TABLE);
		db.execSQL("DROP TABLE " + RESUME_OFFLINE_TABLE);

		String sqlResumeOfflineTable = getSQLCreateTable(RESUME_OFFLINE_TABLE,
				new SQLPair("id", "INTEGER PRIMARY KEY"), new SQLPair("licensePlate", "TEXT"), new SQLPair("serialNumber", "TEXT"),
				new SQLPair("majorType", "TEXT"));
		db.execSQL(sqlResumeOfflineTable);
		db.execSQL("INSERT INTO " + RESUME_OFFLINE_TABLE + " SELECT *, 'AUTO' FROM " + "tmp_" + RESUME_OFFLINE_TABLE);
		db.execSQL("DROP TABLE tmp_" + RESUME_OFFLINE_TABLE);

	};

	@Override
	public String getDatabaseName() {
		// TODO Auto-generated method stub
		return DATABASE_NAME;
	}

	@Override
	public int getDatabaseVersion() {
		// TODO Auto-generated method stub
		return DATABASE_VERSION;
	}

	@Override
	protected String[] getSQLCreateTables() {
		// TODO Auto-generated method stub
		// tuyenpt: reference here https://www.sqlite.org/autoinc.html to get
		// more about pr_key
		/*
		 * String sqlResumeTable = getSQLCreateTable(RESUME_TABLE, new SQLPair(
		 * "id", "INTEGER PRIMARY KEY"), new SQLPair( "objectID", "TEXT"), new
		 * SQLPair("lisencePlate", "TEXT"), new SQLPair("vehicleNumber",
		 * "TEXT"), new SQLPair( "serialNumber", "TEXT"), new
		 * SQLPair("customerName", "TEXT"), new SQLPair("notes", "TEXT"), new
		 * SQLPair( "userName", "TEXT"));
		 */
		String sqlResumeTable = getSQLCreateTable(RESUME_TABLE, new SQLPair("id", "INTEGER PRIMARY KEY"), new SQLPair("objectID", "TEXT"),
				new SQLPair("lisencePlate", "TEXT"), new SQLPair("vehicleNumber", "TEXT"), new SQLPair("serialNumber", "TEXT"),
				new SQLPair("customerName", "TEXT"),
				new SQLPair("notes", "TEXT"), new SQLPair("userName", "TEXT"),
				new SQLPair("majorType", "TEXT"), new SQLPair("shipCode", "TEXT"),
				new SQLPair("shipName", "TEXT"), new SQLPair("registrationInfo", "TEXT"));

		String sqlPictureTable = getSQLCreateTable(PICTURE_TABLE, new SQLPair("id", "INTEGER PRIMARY KEY"), new SQLPair("objectID", "TEXT"),
				new SQLPair("userName", "TEXT"),
				new SQLPair("fileName", "TEXT"),
				new SQLPair("takenDay", "TEXT"), new SQLPair("longitude", "TEXT"),
				new SQLPair("latitude", "TEXT"), new SQLPair("uploadState", "TEXT"));
		/*
		 * String sqlResumeOfflineTable =
		 * getSQLCreateTable(RESUME_OFFLINE_TABLE, new SQLPair("id",
		 * "INTEGER PRIMARY KEY"), new SQLPair("licensePlate", "TEXT"), new
		 * SQLPair( "serialNumber", "TEXT"));
		 */
		String sqlResumeOfflineTable = getSQLCreateTable(RESUME_OFFLINE_TABLE,
				new SQLPair("id", "INTEGER PRIMARY KEY"), new SQLPair("licensePlate", "TEXT"),
				new SQLPair("serialNumber", "TEXT"), new SQLPair("majorType", "TEXT"));

		String sqlImageOfflineTable = getSQLCreateTable(IMAGE_OFFLINE_TABLE,
				new SQLPair("id", "INTEGER PRIMARY KEY"), new SQLPair("frKey", "INTEGER"), new SQLPair("licensePlate", "TEXT"),
				new SQLPair("fileName", "TEXT"),
				new SQLPair("takenDay", "TEXT"), new SQLPair("longitude", "TEXT"), new SQLPair("latitude", "TEXT"));

		return new String[] { sqlResumeTable, sqlPictureTable, sqlResumeOfflineTable, sqlImageOfflineTable };
	}

	@Override
	protected String[] getTableNames() {
		// TODO Auto-generated method stub
		return new String[] { RESUME_TABLE, RESUME_OFFLINE_TABLE, PICTURE_TABLE, IMAGE_OFFLINE_TABLE };
	}

	/* ----------------------------------------------------------------------- */
	/* Instance methods */
	/* ----------------------------------------------------------------------- */
	@Override
	public synchronized void execSQL(String sql) {
		// TODO Auto-generated method stub
		super.execSQL(sql);
	}

	/** --------------------- RESUME table manipulation ------------------------ **/
	public List<BeforSurveyObject> getAllResumeWithUser(String userName, int majorType ) {
		String typeVerhice = "AUTO";
		if (majorType == GlobalMethod.MOTO_MAJOR){
			typeVerhice = "MOTO";
		}

		List<BeforSurveyObject> entries = new ArrayList<BeforSurveyObject>();
		Cursor c = null;
		// Cursor cursor = sqLiteDatabase.query(tableName, tableColumns,
		// whereClause, whereArgs, groupBy, having, orderBy);

		String whereClause = "userName = ? and majorType = ?";
		String[] whereArgs = new String[] { userName, typeVerhice };
		try {
			c = db.query(RESUME_TABLE, new String[] { "id", "objectID",
					"lisencePlate", "vehicleNumber", "serialNumber",
					"customerName", "notes", "userName" }, whereClause,
					whereArgs, null, null, null);

			if (c.moveToFirst()) {
				do {
					String[] seperated = c.getString(4).split("_");
					String serial = "",policy = "";
					if (seperated.length > 1) {
						serial = seperated[0];
						policy = seperated[1];
					} else {
						serial = seperated[0];
					}
					BeforSurveyObject p = new BeforSurveyObject(c.getString(2),
							c.getString(3), serial, c.getString(5),
							c.getString(6), c.getString(7), policy);
					p.setObjectID(c.getString(1));
					entries.add(p);

				} while (c.moveToNext());
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (c != null) {
				c.close();
			}
		}

		return entries;
	}

	public List<PVIShipObject> getShipResumesWithUsername(String userName) {
		List<PVIShipObject> entries = new ArrayList<PVIShipObject>();
		Cursor c = null;
		// Cursor cursor = sqLiteDatabase.query(tableName, tableColumns,
		// whereClause, whereArgs, groupBy, having, orderBy);

		String whereClause = "userName = ? and majorType = ?";
		String[] whereArgs = new String[] { userName, "HULL" };
		try {
			c = db.query(RESUME_TABLE, new String[] { "id", "objectID",
					"userName", "shipCode", "shipName", "registrationInfo" },
					whereClause, whereArgs, null, null, null);

			if (c.moveToFirst()) {
				do {
					PVIShipObject p = new PVIShipObject(c.getString(4),
							c.getString(3), c.getString(5), c.getString(1),
							c.getString(2));
					entries.add(p);

				} while (c.moveToNext());
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return entries;
	}

	/**
	 * INSERT RESUME TO TABLE
	 * 
	 * @param resume
	 */
	public void insertResume(BeforSurveyObject resume, int majorType) {
		String typeVerhice = "AUTO";
		if (majorType == GlobalMethod.MOTO_MAJOR){
			typeVerhice = "MOTO";
		}

		String[] columns = new String[] { "objectID", "lisencePlate",
				"vehicleNumber", "serialNumber", "customerName", "notes",
				"userName", "majorType" };

		Object[] obj = new Object[] { resume.getObjectID(),
				resume.getLicensePlate(), resume.getVehicleNumber(),
				resume.getSerialNumber()+ "_" + resume.getPolicyInsurance(), resume.getCustomerName(),
				resume.getNotes(), resume.getUserName(), typeVerhice };
		this.insert(RESUME_TABLE, columns, obj);

	}

	public void insertShipResume(PVIShipObject resume) {
		String[] columns = new String[] { "objectID", "shipCode", "shipName",
				"registrationInfo", "userName", "majorType" };

		Object[] obj = new Object[] { resume.getObjectID(), resume.getShipCode(), resume.getShipName(), resume.getRegistration(), resume.getUserName(), "HULL" };
		this.insert(RESUME_TABLE, columns, obj);
	}

	public void updateResume(BeforSurveyObject resume) {
		String[] columns = new String[] { "lisencePlate", "vehicleNumber",
				"serialNumber" };
		Object[] object = new Object[] { resume.getLicensePlate(), resume.getVehicleNumber(), resume.getSerialNumber() + "_" + resume.getPolicyInsurance() };

		String whereClause = "objectID = ? ";
		String[] whereArgs = new String[] { resume.getObjectID() };

		this.update(RESUME_TABLE, columns, object, whereClause, whereArgs);
	}

	public void deleteResume(BeforSurveyObject resume, int majorType) {
		String typeVerhice = "AUTO";
		if (majorType == GlobalMethod.MOTO_MAJOR){
			typeVerhice = "MOTO";
		}

		String _whereClause = "objectID = ? and userName = ? and majorType = ?";
		String[] _whereArgs = new String[] { resume.getObjectID(),
				resume.getUserName(), typeVerhice };
		this.delete(RESUME_TABLE, _whereClause, _whereArgs);

		// remove images physic files
		List<BeforeSurveyImage> images = getAllPicturesWithObjectID(resume.getObjectID());
		for (BeforeSurveyImage pviImageEvent : images) {
			FileHelper.removeFileAtPath(pviImageEvent.getFileName());
		}

		String whereClause = "objectID = ?";
		String[] whereArgs = new String[] { resume.getObjectID() };
		this.delete(PICTURE_TABLE, whereClause, whereArgs);
	}
	
	public void deleteShipResume(PVIShipObject resume) {
		String _whereClause = "objectID = ? and userName = ? and majorType = ?";
		String[] _whereArgs = new String[] { resume.getObjectID(), resume.getUserName(), "HULL" };
		this.delete(RESUME_TABLE, _whereClause, _whereArgs);

		// remove images physic files
		List<BeforeSurveyImage> images = getAllPicturesWithObjectID(resume.getObjectID());
		for (BeforeSurveyImage pviImageEvent : images) {
			FileHelper.removeFileAtPath(pviImageEvent.getFileName());
		}

		String whereClause = "objectID = ?";
		String[] whereArgs = new String[] { resume.getObjectID() };
		this.delete(PICTURE_TABLE, whereClause, whereArgs);
	}

	/**
	 * not javadoc
	 * get all pictures of a resume
	 * 
	 * @param oid
	 * @return array of Picture for a resume
	 */
	public List<BeforeSurveyImage> getAllPicturesWithObjectID(String oid) {
		List<BeforeSurveyImage> entries = new ArrayList<BeforeSurveyImage>();
		Cursor c = null;
		String whereClause = "objectID = ?";
		String[] whereArgs = new String[] { oid };
		try {
			c = db.query(PICTURE_TABLE, new String[] { "id", "objectID",
					"userName", "fileName", "takenDay", "longitude",
					"latitude", "uploadState" }, whereClause, whereArgs, null,
					null, null);
			if (c.moveToFirst()) {
				do {
					BeforeSurveyImage p = new BeforeSurveyImage(c.getString(1),
							c.getString(2), c.getString(3), c.getString(4),
							c.getString(5), c.getString(6), c.getString(7));
					entries.add(p);

				} while (c.moveToNext());
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return entries;
	}

	/**
	 * update picture states when uploaded
	 * 
	 * @param image
	 */
	public void updatePicture(BeforeSurveyImage image) {
		String[] columns = new String[] { "uploadState" };
		Object[] object = new Object[] { image.getUploadState() };

		String whereClause = "objectID = ? AND takenDay = ? AND userName = ? AND fileName = ?";
		String[] whereArgs = new String[] { image.getObjectID() + "", image.getTakenDay(), image.getUserName(), image.getFileName() };

		this.update(PICTURE_TABLE, columns, object, whereClause, whereArgs);
	}

	/**
	 * insert a picture to table
	 * 
	 * @param image
	 */
	public void insertPicture(BeforeSurveyImage image) {
		String[] columns = new String[] { "objectID", "userName", "fileName", "takenDay", "longitude", "latitude", "uploadState" };
		Object[] object = new Object[] { image.getObjectID(),
				image.getUserName(), image.getFileName(), image.getTakenDay(),
				image.getLongitude(), image.getLatitude(),
				image.getUploadState() };

		this.insert(PICTURE_TABLE, columns, object);
	}

	/**
	 * --------- RESUME_OFFLINE_TABLE table manipulation ----------------
	 **/

	public void insertOfflineResume(Context context, int mode, BeforeSurveyOfflineObject resume) {
		int majorIndex = GlobalMethod.getMajorType(context, mode);
		String ma_ctu = "AUTO";
		if (majorIndex == GlobalMethod.MOTO_MAJOR){
			ma_ctu = "MOTO";
		} else if (majorIndex == GlobalMethod.SHIP_MAJOR) {
			ma_ctu = "HULL";
		}
		String[] columns = new String[] { "licensePlate", "serialNumber", "majorType" };
		Object[] object = new Object[] { resume.getLicensePlate(), resume.getSerialNumber(), ma_ctu };
		this.insert(RESUME_OFFLINE_TABLE, columns, object);
	}

	public void deleteOfflineResumeWithKey(String resumeKey) {

		// remove images physic files
		List<BeforeSurveyOfflineImage> images = getOfflineImagesWithFrkey(resumeKey);
		for (BeforeSurveyOfflineImage pviImageEvent : images) {
			FileHelper.removeFileAtPath(pviImageEvent.getFileName());
		}

		String _whereClause = "frKey = ?";
		String[] whereArgs = new String[] { resumeKey };
		this.delete(IMAGE_OFFLINE_TABLE, _whereClause, whereArgs);

		String whereClause = "id = ?";
		this.delete(RESUME_OFFLINE_TABLE, whereClause, whereArgs);
	}

	public List<BeforeSurveyOfflineObject> getAllOfflineResumes(Context context, int mode) {
		int majorIndex = GlobalMethod.getMajorType(context, mode);
		String ma_ctu = "AUTO";
		if (majorIndex == GlobalMethod.MOTO_MAJOR){
			ma_ctu = "MOTO";
		}else if (majorIndex == GlobalMethod.SHIP_MAJOR){
			ma_ctu = "HULL";
		}

		List<BeforeSurveyOfflineObject> entries = new ArrayList<BeforeSurveyOfflineObject>();
		Cursor c = null;
		String whereClause = "majorType = ?";
		String[] whereArgs = new String[] { ma_ctu };
		try {
			c = db.query(RESUME_OFFLINE_TABLE, new String[] { "id", "licensePlate", "serialNumber" }, whereClause, whereArgs, null, null, null);
			if (c.moveToFirst()) {
				do {
					BeforeSurveyOfflineObject p = new BeforeSurveyOfflineObject(c.getString(1), c.getString(2));
					p.setPrimaryKey(Integer.toString(c.getInt(0)));
					entries.add(p);
				} while (c.moveToNext());
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (c != null) {
				c.close();
			}
		}

		return entries;
	}

	/**
	 * ------ IMAGE_OFFLINE_TABLE table manipulation -------------------------
	 **/

	public void insertOfflineImage(BeforeSurveyOfflineImage picture) {
		String[] columns = new String[] { "frKey", "licensePlate", "fileName", "takenDay", "longitude", "latitude" };
		Object[] values = new Object[] { picture.getFrKey(), picture.getLicensePlate(), picture.getFileName(), picture.getTakenDay(), picture.getLongitude(), picture.getLatitude() };

		this.insert(IMAGE_OFFLINE_TABLE, columns, values);
	}

	public void deleteOfflineImagesWithResumeID(String resumeID) {
		String whereClause = "frKey = ?";
		String[] whereArgs = new String[] { resumeID };
		delete(IMAGE_OFFLINE_TABLE, whereClause, whereArgs);

	}

	public List<BeforeSurveyOfflineImage> getOfflineImagesWithFrkey(String frKey) {
		List<BeforeSurveyOfflineImage> entries = new ArrayList<BeforeSurveyOfflineImage>();
		Cursor c = null;

		String whereClause = "frKey = ?";
		String[] whereArgs = new String[] { frKey };
		try {
			c = db.query(IMAGE_OFFLINE_TABLE, new String[] { "id", "frKey",
					"licensePlate", "fileName", "takenDay", "longitude",
					"latitude" }, whereClause, whereArgs, null, null, null);
			if (c.moveToFirst()) {
				do {
					BeforeSurveyOfflineImage p = new BeforeSurveyOfflineImage(
							c.getString(1), c.getString(2), c.getString(3),
							c.getString(4), c.getString(5), c.getString(6));
					entries.add(p);

				} while (c.moveToNext());
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (c != null) {
				c.close();
			}
		}

		return entries;
	}
}
