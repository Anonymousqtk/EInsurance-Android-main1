package com.pvi.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

public class DatePickerActivity extends Activity {
	Button btn_dp_select, btn_dp_cancel;
	DatePicker dp_select_date;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.datepicker);
		getFormWidgets();
		addEvents();

	}

	public void addEvents() {
		btn_dp_select.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				int day = dp_select_date.getDayOfMonth();
				int month = dp_select_date.getMonth() + 1;
				int year = dp_select_date.getYear();

				String curDate = String.valueOf(day) + "/"
						+ String.valueOf(month) + "/" + String.valueOf(year);

				String check_date = "";
				Intent intent = getIntent();
				check_date = intent.getStringExtra("check_date_pd");
				if (check_date.equals("get_fromdate_pd")) {
					intent.putExtra("data", curDate);
					setResult(SerialSeachingActivity.resultCode_fromdate_pd, intent);
					finish();
				}
				if (check_date.equals("get_todate_pd")) {
					intent.putExtra("data", curDate);
					setResult(SerialSeachingActivity.resultCode_todate_pd, intent);
					finish();
				}

			}
		});

		btn_dp_cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}

	public void getFormWidgets() {

		btn_dp_select = (Button) findViewById(R.id.btn_dp_select);
		btn_dp_cancel = (Button) findViewById(R.id.btn_dp_cancel);
		dp_select_date = (DatePicker) findViewById(R.id.dp_select_date);

	}

	@Override
	public void onBackPressed() {
		// khi người dùng bấm back button thì không làm gì cả,
		// phải quay lại bằng nút 'quay lại' trên popup để kích hoạt
		// startActivityForResult vì không sử dụng đc intent này trong
		// onBackPressed

	}

}
