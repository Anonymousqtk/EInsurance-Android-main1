package com.pvi.activities;

import java.util.List;

import com.pvi.adapter.BeforeSurveyOfflineAdapter;
import com.pvi.helpers.DatabaseHelper;
import com.pvi.helpers.GlobalMethod;
import com.pvi.objects.BeforeSurveyOfflineObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;

public class BeforeSurveyOfflineList extends Activity {
	private Button btn_offline_list;
	private BeforeSurveyOfflineAdapter adapter = null;
	private ListView lv_offline;
	private DatabaseHelper writeHelperoff;
	private int majorType;
	public static final int REQUEST_CODE_INPUT = 1111111;
	public static final int RESULT_CODE_SAVE = 22222222;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.before_survey_offline_list);
		// disable screen timeout while app is running
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		majorType = GlobalMethod.getMajorType(BeforeSurveyOfflineList.this, MODE_PRIVATE);

		writeHelperoff = DatabaseHelper.getWritableInstance(this);
		this.getFormWidgets();
		this.addEvents();
	}

	private void getFormWidgets() {
		btn_offline_list = (Button) findViewById(R.id.btn_offline_list);
		lv_offline = (ListView) findViewById(R.id.lv_offline);
		writeHelperoff.open();
		List<BeforeSurveyOfflineObject> resumelocal = writeHelperoff.getAllOfflineResumes(BeforeSurveyOfflineList.this, MODE_PRIVATE);
		writeHelperoff.close();
		if (resumelocal.size() != 0) {
			adapter = new BeforeSurveyOfflineAdapter(this, R.layout.resume_element_offline, resumelocal);
			adapter.majorType = majorType;
			adapter.notifyDataSetChanged();
			lv_offline.setAdapter(adapter);
		}

	}

	private void addEvents() {
		btn_offline_list.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), BeforeSurveyOfflineCreate.class);
				startActivityForResult(intent, REQUEST_CODE_INPUT);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		try {
			if (data != null) {
				if (requestCode == REQUEST_CODE_INPUT) {
					if (resultCode == RESULT_CODE_SAVE) {
						writeHelperoff.open();
						List<BeforeSurveyOfflineObject> resumelocal = writeHelperoff.getAllOfflineResumes(BeforeSurveyOfflineList.this, MODE_PRIVATE);
						writeHelperoff.close();
						if (resumelocal.size() != 0) {
							adapter = new BeforeSurveyOfflineAdapter(this, R.layout.resume_element_offline, resumelocal);
							adapter.majorType = majorType;
							adapter.notifyDataSetChanged();
							lv_offline.setAdapter(adapter);
						}
					}
				}

			}
		} catch (Exception ex) {

		}

	}

}
