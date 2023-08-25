package com.pvi.activities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.pvi.adapter.GridImageOffAdapter;
import com.pvi.helpers.DatabaseHelper;
import com.pvi.helpers.FileHelper;
import com.pvi.helpers.GPSTracker;
import com.pvi.helpers.GlobalData;
import com.pvi.helpers.GlobalMethod;
import com.pvi.helpers.TimeService;
import com.pvi.objects.BeforeSurveyOfflineImage;
import com.pvi.objects.BeforeSurveyOfflineObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.AdapterView.OnItemLongClickListener;

public class BeforeSurveyImageOffline extends BaseActivity {
	private int majorType;
	private DatabaseHelper writeHelper;
	private GPSTracker gps;
	private static final int CAMERA_REQUEST_CODE = 1000;
	public static final int REQUEST_CODE_INPUT = 99999;
	public static final int RESULT_CODE_SAVE = 88888;
	private Button captureButton;
	ProgressDialog progress;
	private BeforeSurveyOfflineObject resume;
	private GridView gridView;
	private ArrayList<BeforeSurveyOfflineImage> gridArray;
	private GridImageOffAdapter gridAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
		StrictMode.setVmPolicy(builder.build());

		writeHelper = DatabaseHelper.getWritableInstance(this);
		setContentView(R.layout.before_survey_image_offline);
		// disable screen timeout while app is running
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		Intent i = getIntent();
		resume = (BeforeSurveyOfflineObject) i.getSerializableExtra("BeforeSurveyOfflineObject");
		this.getFormWidgets();
		this.addEvents();
		majorType = GlobalMethod.getMajorType(BeforeSurveyImageOffline.this, MODE_PRIVATE);
	}

	protected void onResume() {
		super.onResume();
		if (!GlobalMethod.isServiceRunning(TimeService.class, this)) {
			showAlertWithMessage("Không thể đồng bộ thời gian. Hãy đăng nhập lại ở trạng thái trực tuyến và vào chi tiết ảnh chụp của 1 hồ sơ bất kỳ để đồng bộ thời gian!");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	private void getFormWidgets() {
		captureButton = (Button) findViewById(R.id.btn_chupoff);
		gridView = (GridView) findViewById(R.id.gridViewImageOff);
		gps = new GPSTracker(BeforeSurveyImageOffline.this);
		writeHelper.open();
		gridArray = (ArrayList<BeforeSurveyOfflineImage>) writeHelper.getOfflineImagesWithFrkey(resume.getPrimaryKey());
		writeHelper.close();

		gridAdapter = new GridImageOffAdapter(this, R.layout.image_row_item, gridArray);
		gridView.setAdapter(gridAdapter);

	}

	private void addEvents() {
		captureButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				capture();

			}
		});
		gridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				gotoFullView(position);
				// TODO Auto-generated method stub
				return false;
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {

				progress = ProgressDialog.show(this, "Đang xử lý ảnh", "Hãy chờ...", true);
				new Thread(new Runnable() {
					@Override
					public void run() {
						// do the thing that takes a long time
						// stop using GPS tracker
						gps.stopUsingGPS();
						String filePath = FileHelper.compressImage(GlobalData.getInstance().getCurrentPhotoPath(), getApplicationContext(), true, gps.getTimeForImageName(), majorType);
						FileHelper.addWatermark(filePath, GlobalData.getInstance().getUsername() != null ? GlobalData.getInstance().getUsername() : "", "Thời gian: " + currentTime, "");

						// create BeforeSurveyOfflineImage
						// write to local database
						BeforeSurveyOfflineImage image = new BeforeSurveyOfflineImage(
								resume.getPrimaryKey(),
								resume.getLicensePlate(), filePath, currentTime,
								gps.getLongitude(), gps.getLatitude());
						writeHelper.open();
						writeHelper.insertOfflineImage(image);
						writeHelper.close();

						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (progress != null)
									progress.dismiss();
								// update view
								reloadGridview();

								// re-get current location
								gps.getLocation();

								// delete external file
								FileHelper.deleteTempContent();

								// recall camera
								capture();

							}
						});
					}
				}).start();

			} else {
				super.onActivityResult(requestCode, resultCode, data);
				FileHelper.deleteTempContent();
				reloadGridview();
				// stop using GPS tracker
				gps.stopUsingGPS();
				return;
			}
		}
	}

	private void capture() {
		if (gps.canGetLocation()) {
			// can not get current location
			if (gps.getLocation() == null) {
				this.showAlertWithMessage("Không lấy được vị trí!");
				return;
			}

			Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// Ensure that there's a camera activity to handle the intent
			if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
				// Create the File where the photo should go
				File photoFile = null;
				try {
					photoFile = FileHelper.createImageFile();
				} catch (IOException ex) {
					// Error occurred while creating the File
					Log.e("Can't create file", ex.getLocalizedMessage());
				}

				Uri photoUri = Uri.fromFile(photoFile);

				// Continue only if the File was successfully created
				if (photoFile != null) {
					GlobalData.getInstance().setCurrentPhotoPath(photoFile.getAbsolutePath());

					takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
					startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
				}
			}

		} else {
			gps.showSettingsAlert();
		}

	}

	private void reloadGridview() {
		gridArray.clear();
		writeHelper.open();
		gridArray = (ArrayList<BeforeSurveyOfflineImage>) writeHelper.getOfflineImagesWithFrkey(resume.getPrimaryKey());
		writeHelper.close();

		try {
			gridAdapter = new GridImageOffAdapter(this, R.layout.image_row_item, gridArray);
			gridView.setAdapter(gridAdapter);

		} catch (NullPointerException e) {
			// TODO: handle exception
			gridAdapter = new GridImageOffAdapter(this, R.layout.image_row_item, gridArray);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (progress != null)
			progress.dismiss();
	}

	private void gotoFullView(int index) {
		BeforeSurveyOfflineImage image = gridArray.get(index);
		Intent i = new Intent(this, BeforeSurveyImageViewerActivity.class);
		i.putExtra("PATH", image.getFileName());
		startActivity(i);
	}

	private void showAlertWithMessage(String message) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(BeforeSurveyImageOffline.this);

		// Setting Dialog Title
		alertDialog.setTitle("PVI E-Insurance");

		// Setting Dialog Message
		alertDialog.setMessage(message);

		// On pressing Settings button
		alertDialog.setPositiveButton("Thử lại",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						// go back
						finish();
					}
				});
		alertDialog.setCancelable(false);
		// Showing Alert Message
		alertDialog.show();
	}
}
