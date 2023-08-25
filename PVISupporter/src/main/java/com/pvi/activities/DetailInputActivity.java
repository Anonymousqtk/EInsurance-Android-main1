package com.pvi.activities;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class DetailInputActivity extends Activity {
    private Button btn_new, btn_edit, btn_save, btn_back;
    private EditText edt_seri, edt_bks_sk;
    private CheckBox chk0, chk1, chk2, chk3, chk4;
    String address;
    String prefname = "my_data";
    private static String SOAP_ACTION = "http://tempuri.org/insert_seri";
    private static String NAMESPACE = "http://tempuri.org/";
    private static String METHOD_NAME = "insert_seri";
    private static String URL;
    public int id_transaction = 0;
    private Spinner spinner;
    private String[] dataFee = { "Đã thu phí", "Chưa thu phí", "Theo hợp đồng" };
    private String feeStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detaiview_input);
        getFormWidgets();
        addEvents();

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dataFee);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spinner = (Spinner) findViewById(R.id.typeUnitSpinner);
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

        feeStatus = "C";
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

    public void addEvents() {
        btn_new.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doXoaTrang();

            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (check_inut()) {
                    if (id_transaction == 0) {
                        save_anchi();
                        setDisable();
                    } else {
                        update_anchi(id_transaction);
                        setDisable();
                    }
                }
            }
        });
        btn_edit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setEnable();
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        edt_bks_sk.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (edt_bks_sk.getText().length() < 5) {
                        showToast("BKS(số khung) tối thiểu 4 ký tự");
                    }
                    return true;
                }
                return false;
            }
        });
    }

    public void doXoaTrang() {
        edt_seri.setText("");
        edt_bks_sk.setText("");
        chk0.setChecked(false);
        chk1.setChecked(false);
        chk2.setChecked(false);
        chk3.setChecked(false);
        chk4.setChecked(false);
        edt_seri.requestFocus();
        id_transaction = 0;
        setEnable();//sau khi lưu thi các control bị disable đi do vậy khi nhập mới cần phải enable nó lên
    }

    public void getFormWidgets() {
        btn_new = (Button) findViewById(R.id.btn_thoat0);
        btn_edit = (Button) findViewById(R.id.btn_edit);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_back = (Button) findViewById(R.id.btn_back);
        edt_seri = (EditText) findViewById(R.id.edt_seri);
        edt_bks_sk = (EditText) findViewById(R.id.edt_bks_sk);
        chk0 = (CheckBox) findViewById(R.id.chk_0);
        chk1 = (CheckBox) findViewById(R.id.chk1);
        chk2 = (CheckBox) findViewById(R.id.chk2);
        chk3 = (CheckBox) findViewById(R.id.chk3);
        chk4 = (CheckBox) findViewById(R.id.chk4);

    }

    public void setEnable() {
        edt_seri.setEnabled(true);
        edt_bks_sk.setEnabled(true);
        chk0.setEnabled(true);
        chk1.setEnabled(true);
        chk2.setEnabled(true);
        chk3.setEnabled(true);
        chk4.setEnabled(true);
        spinner.setEnabled(true);
    }

    public void setDisable() {
        edt_seri.setEnabled(false);
        edt_bks_sk.setEnabled(false);
        chk0.setEnabled(false);
        chk1.setEnabled(false);
        chk2.setEnabled(false);
        chk3.setEnabled(false);
        chk4.setEnabled(false);
        spinner.setEnabled(false);
    }

    public boolean check_inut() {
        boolean rtn = true;
        if (edt_seri.length() == 0) {
            showToast("Bạn phải nhập ấn chỉ!");
            rtn = false;
        }
        if (edt_bks_sk.length() == 0) {
            showToast("Bạn phải nhập BKS(SK)!");
            rtn = false;
        }

        if (chk0.isChecked() == false && chk1.isChecked() == false && chk2.isChecked() == false && chk3.isChecked() == false && chk4.isChecked() == false) {
            showToast("Chưa chọn loại hình bảo hiểm!");
            rtn = false;
        }
        return rtn;
    }

    public String loaihinh_bh() {
        String lhbh = "";
        if (chk0.isChecked() != false || chk1.isChecked() != false || chk2.isChecked() != false || chk3.isChecked() != false || chk4.isChecked() != false) {
            if (chk0.isChecked() != false) {
                lhbh = lhbh + "0";
            }
            if (chk1.isChecked() != false) {
                lhbh = lhbh + "1";
            }
            if (chk2.isChecked() != false) {
                lhbh = lhbh + "2";
            }
            if (chk3.isChecked() != false) {
                lhbh = lhbh + "3";
            }
            if (chk4.isChecked() != false) {
                lhbh = lhbh + "4";
            }

        }
        return lhbh;
    }

    public void update_anchi(int id_tr) {
        String lhbh = loaihinh_bh();
        Bundle receive_bundle = this.getIntent().getExtras();
        final String receive_value = receive_bundle.getString("agent_phone");
        String[] agent_phone;
        agent_phone = receive_value.split(";");
        String aid_agent = agent_phone[1].toString();
        SoapObject request = new SoapObject(NAMESPACE, "update_seri");
        request.addProperty("seri", edt_seri.getText().toString());
        request.addProperty("bks_sk", edt_bks_sk.getText().toString());
        request.addProperty("loai_hinhbh", lhbh);
        request.addProperty("aid", aid_agent);
        request.addProperty("id_tr", id_tr);
        request.addProperty("feeStatus", feeStatus);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;
        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 1000 * 60 * 2);
            androidHttpTransport.debug = true;
            androidHttpTransport.call("http://tempuri.org/update_seri", envelope);
            Log.d("---------------", "HTTP REQUEST:\n" + androidHttpTransport.requestDump);
            Log.d("---------------", "HTTP RESPONSE:\n" + androidHttpTransport.responseDump);

            SoapObject result = (SoapObject) envelope.bodyIn;
            if (result.getPropertyCount() > 0) {
                String res = result.getProperty(0).toString();
                if (res.equals("1")) {
                    showToast("Quá thời hạn sửa phát sinh ấn chỉ");
                } else if (res.equals("2")) {
                    showToast("Sửa phát sinh ấn chỉ không thành công");
                } else if (res.equals("3")) {
                    showToast("Chưa nhập chỉnh sửa phát sinh");
                } else if (res.equals("4")) {
                    showToast("Chỉ cập nhật tình trạng thu phí thành công");
                } else if (res.equals("5")) {
                    showToast("Không cho phép sửa từ đã thu phí thành chưa thu phí/hợp đồng");
                } else if (res.equals("0")) {
                    showToast("Đã sửa phát sinh xong");
                }
            } else {
                showToast("Có lỗi trong quá trình chỉnh sửa");
            }

        } catch (Exception ex) {
            showToast("Có lỗi trong quá trình chỉnh sửa");
        }

    }

    public int check_seri(String seri) {
        boolean hasLetters = false;
        boolean hasDigits = false;
//        boolean hasSomethingElse = false;
        int resul = 0;
        for (int i = 0; i < seri.length(); i++) {
            char c = seri.charAt(i);
            if (Character.isLetter(c))
                hasLetters = true;
            else if (Character.isDigit(c))
                hasDigits = true;

        }

        if (hasLetters && hasDigits) {
            resul = 0; // chuỗi gồm cả chữ và số
        } else if (hasLetters == true && hasDigits == false) {
            resul = 1;// kỹ tự toàn chuỗi
        } else if (hasLetters == false && hasDigits == true) {
            resul = 2;// kỹ tự toàn số
        } else if (hasLetters == false && hasDigits == false) {
            resul = 3;// không có chữ chẳng có số
        }

        return resul;
    }


    public void save_anchi() {
        String lhbh = loaihinh_bh();
        Bundle receive_bundle = this.getIntent().getExtras();
        final String receive_value = receive_bundle.getString("agent_phone");
        String[] agent_phone;
        agent_phone = receive_value.split(";");
        String aid_agent = agent_phone[1].toString();
        String phone = agent_phone[0].toString();
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("seri", edt_seri.getText().toString());
        request.addProperty("bks_sk", edt_bks_sk.getText().toString());
        request.addProperty("loai_hinhbh", lhbh);
        request.addProperty("phone", phone);
        request.addProperty("aid", aid_agent);
        request.addProperty("feeStatus", feeStatus);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;
        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 1000 * 60 * 2);
            androidHttpTransport.debug = true;
            androidHttpTransport.call(SOAP_ACTION, envelope);
            Log.d("---------------", "HTTP REQUEST:\n" + androidHttpTransport.requestDump);
            Log.d("---------------", "HTTP RESPONSE:\n" + androidHttpTransport.responseDump);

            SoapObject result = (SoapObject) envelope.bodyIn;
            if (result.getPropertyCount() > 0) {
                String res = result.getProperty(0).toString();
                if (res.equals("1")) {
                    showToast("Ấn chỉ đã được sử dụng không lưu được");
                } else if (res.equals("2")) {
                    showToast("Nhập ấn chỉ không thành công");
                } else {
                    showToast("Nhập ấn chỉ thành công");
                    id_transaction = Integer.parseInt(res);
                }
            } else {
                showToast("Có lỗi trong quá trình nhập");
            }

        } catch (Exception ex) {
            showToast("Có lỗi trong quá trình nhập");
        }
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

}
