package com.pvi.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

public class PublicLiabilityActivity extends Activity implements
		OnItemSelectedListener {
	private String[] groupCar = { "Xe chở người không kinh doanh vận tải",
			"Xe chở người kinh doanh vận tải", "Xe ôtô chở hàng (xe tải)",
			"Xe ôtô chuyên dụng, xe khác" };
	private Spinner groupSpinner, carSpinner;
	private List<Object> itemList = new ArrayList<Object>();
	private String carValues = "";
	private int groupCarIndex = -1;
	private CheckBox taxiCheckbox, trainingCheckbox;
	String seatValue = null;

	private static final int FROM_CAR_OVER_25 = 1000;
	private static final int FROM_CAR_DEFERENCE = 1001;
	private static final int FROM_CAR_BUS = 1002;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_public_liability);
		initComponents();

	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	public void onBackPressed() {
		setResult(Activity.RESULT_CANCELED);
		finish();
	}

	private void initComponents() {
		// init components
		String[] carItems1 = { "Loại xe dưới 6 chỗ ngồi_437000",
				"Loại xe từ 6 đến 11 chỗ ngồi_794000",
				"Loại xe từ 12 đến 24 chỗ ngồi_1270000",
				"Loại xe trên 24 chỗ ngồi_1825000",
				"Xe vừa chở người vừa chở hàng (Pickup, minivan)_933000" };
		String[] carItems2 = { "Dưới 6 chỗ ngồi theo đăng ký_756000",
				"6 chỗ ngồi theo đăng ký_929000",
				"7 chỗ ngồi theo đăng ký_1080000",
				"8 chỗ ngồi theo đăng ký_1253000",
				"9 chỗ ngồi theo đăng ký_1404000",
				"10 chỗ ngồi theo đăng ký_1512000",
				"11 chỗ ngồi theo đăng ký_1656000",
				"12 chỗ ngồi theo đăng ký_1822000",
				"13 chỗ ngồi theo đăng ký_2049000",
				"14 chỗ ngồi theo đăng ký_2221000",
				"15 chỗ ngồi theo đăng ký_2394000",
				"16 chỗ ngồi theo đăng ký_3054000",
				"17 chỗ ngồi theo đăng ký_2718000",
				"18 chỗ ngồi theo đăng ký_2869000",
				"19 chỗ ngồi theo đăng ký_3041000",
				"20 chỗ ngồi theo đăng ký_3191000",
				"21 chỗ ngồi theo đăng ký_3364000",
				"22 chỗ ngồi theo đăng ký_3515000",
				"23 chỗ ngồi theo đăng ký_3688000",
				"24 chỗ ngồi theo đăng ký_4632000",
				"25 chỗ ngồi theo đăng ký_4813000",
				"Trên 25 chỗ ngồi theo đăng ký_4813000" };
		String[] carItems3 = { "Dưới 3 tấn_853000", "Từ 3 đến 8 tấn_1660000",
				"Trên 8 tấn đến 15 tấn_2746000", "Trên 15 tấn_3200000" };
		String[] carItems4 = {
				"Xe cứu thương_1119600",
				"Xe chở tiền_524400",
				"Đầu kéo, rơ-mooc gồm cả đầu kéo và rơ-mooc_4800000",
				"Xe máy chuyên dùng(Công nông, máy kéo...)_1023600",
				"Xe chuyên dùng khác(Xe bồn, xe tải gắn cẩu...)_853000_1660000_2746000_3200000",
				"Xe buýt_437000_794000_1270000_1825000" };

		itemList.add(carItems1);
		itemList.add(carItems2);
		itemList.add(carItems3);
		itemList.add(carItems4);

		groupSpinner = (Spinner) findViewById(R.id.carTypeSpinnerPersonal);
		groupSpinner.setPrompt("Lựa chọn nhóm loại xe");
		groupSpinner.setAdapter(new CarAdapter(this, R.layout.spinner_row,
				groupCar));

		carSpinner = (Spinner) findViewById(R.id.carSpinnerPersonal);
		carSpinner.setPrompt("Lựa chọn loại xe");

		// Spinner click listener
		groupSpinner.setOnItemSelectedListener(this);
		carSpinner.setOnItemSelectedListener(this);

		Button accept = (Button) findViewById(R.id.acceptButtonPersonal);
		accept.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Double money = Double.parseDouble(carValues.split("_")[1].toString());
				// TODO Auto-generated method stub
				if (groupCarIndex == 0 || groupCarIndex == 2) {
					if (trainingCheckbox.isChecked()) {
						money = money * 1.2f;
					}
				} else if (groupCarIndex == 1) {
					if (taxiCheckbox.isChecked()) {
						money = money * 1.7f;
					}
				}
				String _finalValue = carValues.split("_")[0] + "_" + money + "";
				Intent intent = getIntent();
				intent.putExtra("data", _finalValue);
				setResult(CarCalculationActivity.LIABILITY_CODE, intent);
				finish();
			}
		});

		taxiCheckbox = (CheckBox) findViewById(R.id.taxiBox);
		trainingCheckbox = (CheckBox) findViewById(R.id.trainingBox);

	}

	private void loadSpinnerData(int _groupIndex) {
		String[] _itemList = (String[]) itemList.get(_groupIndex);
		carSpinner.setAdapter(new CarAdapter(this, R.layout.spinner_row,
				_itemList));
		taxiCheckbox.setVisibility(View.GONE);
		trainingCheckbox.setVisibility(View.GONE);
		if (_groupIndex == 1) {
			taxiCheckbox.setVisibility(View.VISIBLE);
		} else if (_groupIndex == 0 || _groupIndex == 2) {
			trainingCheckbox.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
			long id) {
		// TODO Auto-generated method stub
		if (arg0.getId() == R.id.carTypeSpinnerPersonal) {
			groupCarIndex = position;
			loadSpinnerData(groupCarIndex);
		} else {
			String[] _itemList = (String[]) itemList.get(groupCarIndex);
			if (groupCarIndex == 1 && position == _itemList.length - 1) {
				getNumberOfSeat(position, FROM_CAR_OVER_25);
			} else if (groupCarIndex == 3 && position == _itemList.length - 2) { // xe chuyên dùng khác
				getNumberOfSeat(position, FROM_CAR_DEFERENCE);
			} else if (groupCarIndex == 3 && position == _itemList.length - 1) { // xe  bus
				getNumberOfSeat(position, FROM_CAR_BUS);
			} else {
				carValues = (String) _itemList[position];
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
	}

	private void getNumberOfSeat(final int position, final int selectType) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				PublicLiabilityActivity.this);
		alertDialog
				.setTitle((selectType == FROM_CAR_OVER_25) ? "Số chỗ ngồi > 25"
						: (selectType == FROM_CAR_DEFERENCE ? "Xe chuyên dùng khác: Số tấn"
								: "Số chỗ ngồi xe buýt"));
		alertDialog
				.setMessage((selectType == FROM_CAR_OVER_25) ? "Nhập vào số chỗ ngồi trên xe tham gia bảo hiểm"
						: (selectType == FROM_CAR_DEFERENCE ? "Nhập vào trọng tải xe chuyên dùng khác theo số tấn"
								: "Nhập vào số chỗ ngồi của xe buýt"));

		final EditText input = new EditText(PublicLiabilityActivity.this);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(2) });
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		input.setLayoutParams(lp);
		alertDialog.setView(input);
		// alertDialog.setIcon(R.drawable.key);

		alertDialog.setPositiveButton("Đồng ý",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						seatValue = input.getText().toString();
						int _money = 0;
						if (seatValue == null || seatValue.length() == 0) {
							showAlertWithMessage("Thông số nhập vào không hợp lệ!");
							return;
						}
						if (selectType == FROM_CAR_OVER_25) {
							if (Integer.parseInt(seatValue) <= 25) {
								showAlertWithMessage("Số chỗ ngồi không hợp lệ!");
								return;
							} else {
								String[] _itemList = (String[]) itemList
										.get(groupCarIndex);
								_money = (Integer.parseInt(seatValue) - 25) * 30000;
								String str[] = ((String) _itemList[position])
										.split("_");
								_money = _money + Integer.parseInt(str[1]);
								carValues = str[0] + "_" + _money + "";
							}
						} else if (selectType == FROM_CAR_DEFERENCE) {
							String[] _itemList = (String[]) itemList
									.get(groupCarIndex);
							String str[] = ((String) _itemList[position])
									.split("_");
							if (Integer.parseInt(seatValue) < 3) {
								_money = Integer.parseInt(str[1])  * 120/100;
							} else if (Integer.parseInt(seatValue) >= 3 && Integer.parseInt(seatValue) <= 8) {
								_money = Integer.parseInt(str[2])  * 120/100;
							} else if (Integer.parseInt(seatValue) > 8 && Integer.parseInt(seatValue) <= 15) {
								_money = Integer.parseInt(str[3])  * 120/100;
							} else if (Integer.parseInt(seatValue) > 15) {
								_money = Integer.parseInt(str[4])  * 120/100;
							} else {
								showAlertWithMessage("Số tấn trọng tải xe không phù hợp!");
								return;
							}
							carValues = str[0] + "_" + _money + "";
						} else if (selectType == FROM_CAR_BUS) {
							String[] _itemList = (String[]) itemList
									.get(groupCarIndex);
							String str[] = ((String) _itemList[position])
									.split("_");
							if (Integer.parseInt(seatValue) < 6) {
								_money = Integer.parseInt(str[1]);
							} else if (Integer.parseInt(seatValue) >= 6 && Integer.parseInt(seatValue) <= 11) {
								_money = Integer.parseInt(str[2]);
							} else if (Integer.parseInt(seatValue) > 11 && Integer.parseInt(seatValue) <= 24) {
								_money = Integer.parseInt(str[3]);
							} else if (Integer.parseInt(seatValue) > 24) {
								_money = Integer.parseInt(str[4]);
							} else {
								showAlertWithMessage("Số tấn trọng tải xe không phù hợp!");
								return;
							}
							carValues = str[0] + "_" + _money + "";
						}
						
					}
				});

		alertDialog.setCancelable(false);
		alertDialog.show();
	}

	private void showAlertWithMessage(String message) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

		TextView title = new TextView(this);
		title.setText("PVI eInsurance");
		title.setPadding(10, 10, 10, 10);
		title.setGravity(Gravity.CENTER);
		title.setTextColor(Color.RED);
		title.setTextSize(20);

		// Setting Dialog Title
		alertDialog.setCustomTitle(title);

		// Setting Dialog Message
		alertDialog.setMessage(message);

		// on pressing cancel button
		alertDialog.setNegativeButton("Thử lại",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		alertDialog.setCancelable(false);
		// Showing Alert Message
		alertDialog.show();
	}

	// Adapter class for spinner control
	public class CarAdapter extends ArrayAdapter<String> {
		private String[] data;

		public CarAdapter(Context context, int textViewResourceId,
				String[] objects) {
			super(context, textViewResourceId, objects);
			data = objects;
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}

		public View getCustomView(int position, View convertView,
				ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.spinner_row, parent, false);
			TextView label = (TextView) row.findViewById(R.id.itemType);
			if (parent.getId() == R.id.carTypeSpinnerPersonal) {
				label.setText(groupCar[position]);
			} else {
				String _values[] = data[position].split("_");
				label.setText(_values[0]);
			}
			return row;
		}
	}
}
