package com.pvi.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.pvi.helpers.FileHelper;
import com.pvi.helpers.GlobalData;
import com.pvi.helpers.ZoomImageViewHelper;

public class BeforeSurveyImageViewerActivity  extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullview_image);
		
		// disable screen timeout while app is running
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// get intent data
		Intent i = getIntent();

		// Selected image id
		String path = i.getExtras().getString("PATH");

		ZoomImageViewHelper imageView = (ZoomImageViewHelper) findViewById(R.id.fullviewImage);
		imageView.setImageBitmap(FileHelper.loadImageFromPath(path));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (GlobalData.getInstance().getUsername() == null) {
			this.finish();
		}

	}

}
