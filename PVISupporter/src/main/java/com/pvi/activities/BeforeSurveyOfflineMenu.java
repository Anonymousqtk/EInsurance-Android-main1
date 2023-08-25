package com.pvi.activities;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.pvi.helpers.GlobalMethod;


public class BeforeSurveyOfflineMenu extends Activity  implements AdapterView.OnItemSelectedListener {
	private Button btncreate_offline;
	private Spinner majorSpiner;
	private String[] majorData = { "GIÁM ĐỊNH ĐIỀU KIỆN XE CƠ GIỚI", "GIÁM ĐỊNH ĐIỀU KIỆN TÀU" };


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.before_survey_offline_menu);
		// disable screen timeout while app is running
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		this.getFormWidgets();
		this.addEvents();
	}

	private void getFormWidgets() {
		btncreate_offline = (Button) findViewById(R.id.btnlist_offline);
		majorSpiner = (Spinner) findViewById(R.id.majorSpinner);
		majorSpiner.setAdapter(new CarAdapter(this, R.layout.spinner_row, majorData));
		majorSpiner.setSelection(GlobalMethod.getMajorType(BeforeSurveyOfflineMenu.this, MODE_PRIVATE));
		majorSpiner.setOnItemSelectedListener(this);

	}

	private void addEvents() {
		btncreate_offline.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), BeforeSurveyOfflineList.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
		// TODO Auto-generated method stub
		GlobalMethod.saveMajorType(position, MODE_PRIVATE, BeforeSurveyOfflineMenu.this);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	public class CarAdapter extends ArrayAdapter<String> {
		private String[] data;

		public CarAdapter(Context context, int textViewResourceId, String[] objects) {
			super(context, textViewResourceId, objects);
			data = objects;
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}

		public View getCustomView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.spinner_row, parent, false);
			TextView label = (TextView) row.findViewById(R.id.itemType);
			label.setGravity(Gravity.CENTER);
			label.setText(data[position]);
			return row;
		}
	}

}
