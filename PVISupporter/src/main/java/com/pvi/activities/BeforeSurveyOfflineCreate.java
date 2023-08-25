package com.pvi.activities;

import com.pvi.helpers.DatabaseHelper;
import com.pvi.helpers.GlobalMethod;
import com.pvi.objects.BeforeSurveyOfflineObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class BeforeSurveyOfflineCreate extends Activity {
	private DatabaseHelper writeHelperOff;
	private BeforeSurveyOfflineObject resume;

	private Button btnsave_offline;
	private EditText txtbks_create_offline, txtso_seri_create_offline;
	private int majorType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.before_survey_offline_create);
		// disable screen timeout while app is running
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		majorType = GlobalMethod.getMajorType(BeforeSurveyOfflineCreate.this, MODE_PRIVATE);
		writeHelperOff = DatabaseHelper.getWritableInstance(this);
		this.getFormWidgets();
		this.addEvents();
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	public void getFormWidgets() {
		btnsave_offline = (Button) findViewById(R.id.btnsave_offline);
		txtbks_create_offline = (EditText) findViewById(R.id.txtbks_create_offline);
		txtso_seri_create_offline = (EditText) findViewById(R.id.txtso_seri_create_offline);
		TextView lisencePlate = (TextView) findViewById(R.id.lisencePlateLabel);
		TextView optionLabel = (TextView) findViewById(R.id.optionLabel);
		if (majorType == GlobalMethod.SHIP_MAJOR) {
			lisencePlate.setText("Số đăng ký tàu");
			optionLabel.setText("Tên tàu");
			txtso_seri_create_offline.setInputType(InputType.TYPE_CLASS_TEXT);
		}
	}

	private void addEvents() {
		btnsave_offline.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				resume = new BeforeSurveyOfflineObject(txtbks_create_offline.getText().toString(), txtso_seri_create_offline.getText().toString());
				if (txtbks_create_offline.getText().toString().length() != 0) {
					try {
						writeHelperOff.open();
						writeHelperOff.insertOfflineResume(BeforeSurveyOfflineCreate.this, MODE_PRIVATE, resume);
						writeHelperOff.close();

						Intent intent = getIntent();
						setResult(BeforeSurveyOfflineList.RESULT_CODE_SAVE, intent);
						close();
					} catch (Exception ex) {
						showToast(ex.getMessage() + "Có lỗi trong quá trình lưu dữ liệu!");
					}

				} else {
					showToast("Bạn phải nhập vào biển kiểm soát!");
				}
			}
		});
	}

	private void close() {
		this.finish();
	}

	private void showToast(String message) {
		Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();
	}

}
