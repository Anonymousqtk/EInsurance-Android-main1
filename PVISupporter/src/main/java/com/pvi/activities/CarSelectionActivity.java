package com.pvi.activities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

public class CarSelectionActivity extends Activity implements OnItemSelectedListener {
    private String[] groupCar = {"Nhóm A: Nhóm xe rủi ro thấp (không KDVT)",
            "Nhóm B: Nhóm xe chuyên dùng (TCVN 7271)",
            "Nhóm C: Nhóm xe rủi ro cao", "Nhóm D: Nhóm xe giá trị thấp"};
    private Spinner groupSpinner, carSpinner;
    private int groupCarIndex = -1;
    private List<Object> itemList = new ArrayList<Object>();
    private String carValues = "";
    private CheckBox uberCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_selections);
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

    @Override
    public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
        // TODO Auto-generated method stub

        // set uber checkbox to default value
        uberCheckbox.setVisibility(View.GONE);
        uberCheckbox.setChecked(false);

        if (arg0.getId() == R.id.carTypeSpinner) {
            groupCarIndex = position;
            loadSpinnerData(groupCarIndex);
        } else {
            String[] _itemList = (String[]) itemList.get(groupCarIndex);
            carValues = (String) _itemList[position];

            if (groupCarIndex == 2 && position == 5) {// chỉ show khi chọn nhóm xe là nhóm C + chọn xe là C-2.1.Xe chở khách theo hợp đồng (≤ 24 chỗ)
                uberCheckbox.setVisibility(View.VISIBLE);
                uberCheckbox.setChecked(false);
            }
            if (groupCarIndex == 3) {
                this.showAlertWithMessage("Tính theo biểu phí loại xe tương ứng như các mục trên\nChú ý: Phí tối thiểu không nhỏ hơn 2.5 triệu đồng");
                return;
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

    private void initComponents() {
        // init components
        String[] carItems1 = {
                "A-1.Xe chở người ≤ 24 chỗ, xe chở tiền, xe buýt nội tỉnh_1.5",
                "A-2.Xe chở người > 24 chỗ_1.65",
                "A-3.Xe bán tải (pick-up)_1.6",
                "A-4.Xe tải VAN_1.95",
                "A-5.Xe điện hoạt động trong sân Golf, khu du lịch_0.5"};
        String[] carItems2 = {
                "B-1.Xe chở xăng, dầu, khí hóa lỏng, nhựa đường, nhiên liệu_1.65",
                "B-2.Xe tải gắn cẩu, xe gắn thiết bị khoan, xe cẩu tự hành (được phép lưu hành trên đường bộ), xe trộn/bơm bê tông_1.65",
                "B-3.Xe cứu thương, cứu hỏa, xe thang, xe vệ sinh, xe quét đường, xe téc chở chất lỏng (trừ chất dễ cháy)_1.55"};
        String[] carItems3 = {
                "C-1.1.Xe ôtô vận tải hàng hóa_1.7",
                "C-1.2.Xe tải/rơ-mooc chở hàng đông lạnh/gắn thùng bảo ôn, xe hoạt động trên khai trường, công trường_2.6",
                "C-1.3.Xe đầu kéo, xe chở hàng siêu trường, siêu trọng_2.6",
                "C-1.4.Rơ mooc_1.1",
                "C-1.5.Rơ mooc có gắn thiết bị chuyên dụng_2.0",
                "C-2.1.Xe chở khách theo hợp đồng (≤ 24 chỗ)_1.6",
                "C-2.2.Xe chở khách theo hợp đồng (> 24 chỗ)_1.75",
                "C-2.3.Xe chở người (xe buýt, xe khách liên tỉnh, xe giường nằm, xe khách tuyến cố định)_2.0",
                "C-2.4.Xe taxi_3.9"};
        String[] carItems4 = {"D.Từ 100 triệu đến 200 triệu đồng"};

        itemList.add(carItems1);
        itemList.add(carItems2);
        itemList.add(carItems3);
        itemList.add(carItems4);

        groupSpinner = (Spinner) findViewById(R.id.carTypeSpinner);
        groupSpinner.setPrompt("Lựa chọn nhóm loại xe");
        groupSpinner.setAdapter(new CarAdapter(this, R.layout.spinner_row, groupCar));

        carSpinner = (Spinner) findViewById(R.id.carSpinner);
        carSpinner.setPrompt("Lựa chọn loại xe");

        // Spinner click listener
        groupSpinner.setOnItemSelectedListener(this);
        carSpinner.setOnItemSelectedListener(this);

        Button accept = (Button) findViewById(R.id.acceptButton);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (uberCheckbox.isChecked()) {
//                    Float money = Float.parseFloat(carValues.split("_")[1].toString());
//                    money = money * 1.3f;
//                    money = round(money, 3);
                    carValues = carValues.split("_")[0].toString() + " và tham gia Uber/Grab" + "_" + carValues.split("_")[1].toString() + "";
                }
                String additionalValues = getAdditionalValues();
                Intent intent = getIntent();
                intent.putExtra("data", groupCarIndex + "_" + carValues + additionalValues + "_" + (uberCheckbox.isChecked() ? "1" : "0") + "");
                setResult(CarCalculationActivity.CAR_CODE, intent);
                finish();
            }
        });

        uberCheckbox = (CheckBox) findViewById(R.id.uberBox);
    }

    private void loadSpinnerData(int _groupIndex) {
        String[] _itemList = (String[]) itemList.get(_groupIndex);
        carSpinner.setAdapter(new CarAdapter(this, R.layout.spinner_row, _itemList));
    }

    private String getAdditionalValues() {
        String _values = "_0,";
        CheckBox checbox9 = (CheckBox) findViewById(R.id.checkBox9);
        if (checbox9.isChecked()) {
            _values = _values + "2,";
        }
        CheckBox checbox4 = (CheckBox) findViewById(R.id.checkBox4);
        if (checbox4.isChecked()) {
            _values = _values + "3,";
        }
        CheckBox checbox5 = (CheckBox) findViewById(R.id.checkBox5);
        if (checbox5.isChecked()) {
            _values = _values + "6,";
        }
        CheckBox checbox6 = (CheckBox) findViewById(R.id.checkBox6);
        if (checbox6.isChecked()) {
            _values = _values + "7,";
        }
        CheckBox checbox7 = (CheckBox) findViewById(R.id.checkBox7);
        if (checbox7.isChecked()) {
            _values = _values + "8,";
        }
        CheckBox checbox8 = (CheckBox) findViewById(R.id.checkBox8);
        if (checbox8.isChecked()) {
            _values = _values + "11,";
        }
        return _values;
    }


    private float round(float value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.floatValue();
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
        alertDialog.setNegativeButton("Thử lại", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.setCancelable(false);
        // Showing Alert Message
        alertDialog.show();
    }

    // Adapter class for spinner control
    private class CarAdapter extends ArrayAdapter<String> {
        private String[] data;

        private CarAdapter(Context context, int textViewResourceId, String[] objects) {
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

        private View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_row, parent, false);
            TextView label = (TextView) row.findViewById(R.id.itemType);
            if (parent.getId() == R.id.carTypeSpinner) {
                label.setText(groupCar[position]);
            } else {
                String _values[] = data[position].split("_");
                label.setText(_values[0]);
            }
            return row;
        }
    }

}
