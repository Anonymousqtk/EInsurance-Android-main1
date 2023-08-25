package com.pvi.activities;


import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class SerialEditorActivity extends Activity {
    EditText e_seri, e_bks;
    Button bt_back, bt_edit, bt_save;
    CheckBox c1, c2, c3, c4, c5;
    String id_sr;
    String address;
    String prefname = "my_data";
    int position;
    int edit_ = 0;
    private static String SOAP_ACTION = "http://tempuri.org/update_seri_edit";
    private static String NAMESPACE = "http://tempuri.org/";
    private static String METHOD_NAME = "update_seri_edit";
    private static String URL;

    private Spinner spinner;
    private String[] dataFee = { "Đã thu phí", "Chưa thu phí", "Theo hợp đồng" };
    private String feeStatus;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_edit_seri);
        getFormWidgets();
        addEvents();


        Bundle receive_bundle = this.getIntent().getExtras();
        final String receive_value = receive_bundle.getString("send_value");
        String[] arrseri_bks_date1;
        arrseri_bks_date1 = receive_value.split(";");
        id_sr = arrseri_bks_date1[0].toString();
        e_seri.setText(arrseri_bks_date1[1].toString());
        e_bks.setText(arrseri_bks_date1[2].toString());

        if (arrseri_bks_date1[3].toString().indexOf("0") != -1) {
            c1.setChecked(true);
        }
        if (arrseri_bks_date1[3].toString().indexOf("1") != -1) {
            c2.setChecked(true);
        }
        if (arrseri_bks_date1[3].toString().indexOf("2") != -1) {
            c3.setChecked(true);
        }
        if (arrseri_bks_date1[3].toString().indexOf("3") != -1) {
            c4.setChecked(true);
        }
        if (arrseri_bks_date1[3].toString().indexOf("4") != -1) {
            c5.setChecked(true);
        }
        position = Integer.parseInt(arrseri_bks_date1[4].toString());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dataFee);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spinner = (Spinner) findViewById(R.id.typeUnitSpinnerEdit);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    feeStatus = "C";
                } else if (position == 1) {
                    feeStatus = "K";
                } else if (position == 2) {
                    feeStatus = "HD";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        feeStatus = arrseri_bks_date1[6].toString();
        if (feeStatus.equals("C")) {
            spinner.setSelection(0);
        } else if (feeStatus.equals("K")) {
            spinner.setSelection(1);
        } else if (feeStatus.equals("HD")) {
            spinner.setSelection(2);
        }

        setDisable();

        // xử lý nếu ấn chỉ có số điện thoại = số điện thoại đăng nhập mới cho sửa
        // hoặc số điện thoại là quản trị mới hiển thị nút sửa để sửa và trong thời gian sửa
        if (arrseri_bks_date1[5].toString().equals(arrseri_bks_date1[6].toString()) || arrseri_bks_date1[7].toString() == "0") {
            //arrseri_bks_date1[5].toString()==arrseri_bks_date1[6].toString() || arrseri_bks_date1[7].toString()=="0"
            // số điện thoại đăng nhập =số điện thoại nhắn hoặc nhập seri
            //arrseri_bks_date1[7].toString();
            bt_edit.setEnabled(true);

        } else {
            bt_edit.setEnabled(false);
        }

    }

    @Override
    public void onBackPressed() {
//     khi người dùng bấm back button thì không làm gì cả,
//     phải quay lại bằng nút 'quay lại' trên popup để kích hoạt
//     startActivityForResult vì không sử dụng đc intent này trong onBackPressed

    }

    @Override
    protected void onResume() {
        // onresume()chạy lúc đầu sau onstart() nên đc dùng để đọc thông tin
        super.onResume();
        SharedPreferences pre = getSharedPreferences(prefname, MODE_PRIVATE);
        address = pre.getString("web_url", "103.26.252.13");
        URL = "http://" + address + "/Soap_mobile/Mobile_soap.asmx";
//        URL = "http://103.26.252.13/soapmobiletest/Mobile_soap.asmx";
    }

    public void setEnable() {
        e_seri.setEnabled(true);
        e_bks.setEnabled(true);
        c1.setEnabled(true);
        c2.setEnabled(true);
        c3.setEnabled(true);
        c4.setEnabled(true);
        c5.setEnabled(true);
        bt_save.setEnabled(true);
        spinner.setEnabled(true);
    }

    public void setDisable() {
        e_seri.setEnabled(false);
        e_bks.setEnabled(false);
        bt_save.setEnabled(false);
        c1.setEnabled(false);
        c2.setEnabled(false);
        c3.setEnabled(false);
        c4.setEnabled(false);
        c5.setEnabled(false);
        spinner.setEnabled(false);
    }


    public void addEvents() {
        bt_edit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setEnable();
            }
        });

        bt_save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // 1 kiến tra các thông tin đã phù hợp để lưu chưa
                if (check_inut()) {
                    save_anchi();
                    setDisable();
                }
                // 2 nếu ổn thì tiến hành lưu dữ liệu và sét visble=true
            }
        });


    }

    public void save_anchi() {
        String lhbh = loaihinh_bh();

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("seri", e_seri.getText().toString());
        request.addProperty("bks_sk", e_bks.getText().toString());
        request.addProperty("loai_hinhbh", lhbh);
        request.addProperty("id_tr", id_sr);
        request.addProperty("feeStatus", feeStatus);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;
        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 1000 * 60 * 2);
            androidHttpTransport.call(SOAP_ACTION, envelope);

            SoapObject result = (SoapObject) envelope.bodyIn;
            if (result.getPropertyCount() > 0) {
                String res = result.getProperty(0).toString();
                if (res.equals("1")) {
                    edit_ = 1;
                    showToast("Đã hết thời gian chỉnh sửa");

                } else if (res.equals("2")) {
                    edit_ = 1; //bắt lại trạng thái không thay đổi gì trên listview
                    showToast("Sửa phát sinh ấn chỉ không thành công");

                } else if (res.equals("3")) {
                    edit_ = 1;
                    showToast("Chưa nhập chỉnh sửa phát sinh");
                } else if (res.equals("4")) {
                    showToast("Chỉ cập nhật tình trạng thu phí thành công");
                } else if (res.equals("5")) {
                    showToast("Không cho phép sửa từ đã thu phí thành chưa thu phí/hợp đồng");
                } else {
                    edit_ = 0;
                    showToast("Sửa phát sinh ấn chỉ thành công");
                }
            } else {
                showToast("Có lỗi trong quá trình nhập");

            }

        } catch (Exception ex) {
            showToast("Có lỗi trong quá trình nhập");
        }
    }

    public String loaihinh_bh() {
        String lhbh = "";
        if (c1.isChecked() != false || c2.isChecked() != false || c3.isChecked() != false || c4.isChecked() != false || c5.isChecked() != false) {
            if (c1.isChecked() != false) {
                lhbh = lhbh + "0";
            }
            if (c2.isChecked() != false) {
                lhbh = lhbh + "1";
            }
            if (c3.isChecked() != false) {
                lhbh = lhbh + "2";
            }
            if (c4.isChecked() != false) {
                lhbh = lhbh + "3";
            }
            if (c5.isChecked() != false) {
                lhbh = lhbh + "4";
            }

        }
        return lhbh;
    }

    public void thoat(View v) {
        String lh_bh = loaihinh_bh();
        String send_value = e_seri.getText() + ";" + e_bks.getText() + ";" + lh_bh + ";" + position + ";" + String.valueOf(edit_);
        Intent intent = getIntent();
        intent.putExtra("data", send_value);
        setResult(SerialFindingActivity.RESULT_CODE_SAVE, intent);
        finish();
    }


    public boolean check_inut() {
        boolean rtn = true;
        if (e_seri.length() == 0) {
            showToast("Bạn phải nhập ấn chỉ!");
            rtn = false;
        }
        if (e_bks.length() == 0) {
            showToast("Bạn phải nhập BKS(SK)!");
            rtn = false;
        }
        if (c1.isChecked() == false && c2.isChecked() == false && c3.isChecked() == false && c4.isChecked() == false && c5.isChecked() == false) {
            showToast("Chưa chọn loại hình bảo hiểm!");
            rtn = false;
        }
        return rtn;
    }

    public void getFormWidgets() {
        bt_back = (Button) findViewById(R.id.bt_back);
        bt_edit = (Button) findViewById(R.id.bt_edit);
        bt_save = (Button) findViewById(R.id.bt_save);
        e_seri = (EditText) findViewById(R.id.e_seri);
        e_bks = (EditText) findViewById(R.id.e_bks);
        c1 = (CheckBox) findViewById(R.id.c1);
        c2 = (CheckBox) findViewById(R.id.c2);
        c3 = (CheckBox) findViewById(R.id.c3);
        c4 = (CheckBox) findViewById(R.id.c4);
        c5 = (CheckBox) findViewById(R.id.c5);
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }
}
