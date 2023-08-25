package com.pvi.activities;

import java.util.List;

import com.pvi.adapter.BeforeSuveyAdapter;
import com.pvi.adapter.ShipAdapter;
import com.pvi.helpers.AsyncTaskManager;
import com.pvi.helpers.DatabaseHelper;
import com.pvi.helpers.GlobalData;
import com.pvi.helpers.GlobalMethod;
import com.pvi.objects.BeforSurveyObject;
import com.pvi.objects.PVIShipObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ListView;
import android.widget.Toast;

public class BeforeSurveyListActivity extends Activity {
	private DatabaseHelper readHelper;
	private ListView listView;
	private int majorType;
	private List<BeforSurveyObject> cars;
	private List<PVIShipObject> ships;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.before_survey_list_activity);
		listView = (ListView) findViewById(R.id.before_survey_listview);
		majorType = GlobalMethod.getMajorType(BeforeSurveyListActivity.this, MODE_PRIVATE);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (GlobalData.getInstance().getUsername() == null) {
			this.finish();
		}
		
		readHelper = DatabaseHelper.getReadableInstance(this);
		readHelper.open();
		if (majorType == GlobalMethod.CAR_MAJOR || majorType == GlobalMethod.MOTO_MAJOR) {
			cars = readHelper.getAllResumeWithUser(GlobalData.getInstance().getUsername(), majorType);
			if (cars.size() != 0) {
				BeforeSuveyAdapter adapter = new BeforeSuveyAdapter(this, R.layout.before_survey_cell, cars);
				adapter.notifyDataSetChanged();
				listView.setAdapter(adapter);
			} else {
				showToastMessgae("Không có hồ sơ nào!");
			}
		} else if (majorType == GlobalMethod.SHIP_MAJOR) {
			ships = readHelper.getShipResumesWithUsername(GlobalData.getInstance().getUsername());
			if (ships.size() != 0) {
				ShipAdapter adapter =  new ShipAdapter(BeforeSurveyListActivity.this, R.layout.ship_row, ships);
				adapter.isFromListview = true;
				adapter.notifyDataSetChanged();
				listView.setAdapter(adapter);
			} else {
				showToastMessgae("Không có hồ sơ nào!");
			}
		}
		readHelper.close();
	}

	private  void showToastMessgae(String message) {
		Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();
	}
}
