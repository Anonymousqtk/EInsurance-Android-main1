package com.pvi.activities;


import java.util.ArrayList;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.pvi.adapter.MyArrayAdapter;
import com.pvi.objects.EinsuranceObject;

import android.widget.AdapterView.OnItemClickListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class SerialFindingActivity extends Activity {

    ArrayList<EinsuranceObject> arrseri_bks_date = new ArrayList<EinsuranceObject>();
    MyArrayAdapter adapter = null;
    ListView list_ketqua = null;
    EditText edt_dseri, edt_sbks, edt_phone;
    Button btn_seach;
    String aid_agent;
    String quantri;
    String phone, address;
    String prefname = "my_data";
    public static final int REQUEST_CODE_INPUT = 99999;
    public static final int RESULT_CODE_SAVE = 115;
    private static String SOAP_ACTION = "http://tempuri.org/seach_seri";
    private static String NAMESPACE = "http://tempuri.org/";
    private static String METHOD_NAME = "seach_seri";
    private static String URL;

    private Spinner spinner;
    private String[] dataFee = { "Tất cả", "Đã thu phí", "Chưa thu phí", "Theo hợp đồng" };
    private String feeStatus;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seach_seri);
        getFormWidgets();
        addEvents();

        Bundle receive_bundle = this.getIntent().getExtras();
        final String receive_value = receive_bundle.getString("agent_phone");
        String[] agent_phone;
        agent_phone = receive_value.split(";");
        phone = agent_phone[0].toString();
        aid_agent = agent_phone[1].toString();
        quantri = agent_phone[2].toString();

        if (quantri.equals("1")) {    //quantri=1 số điện thoại cán bộ đại lý
            edt_phone.setEnabled(false);
        } else {    //quantri=1 số điện thoại quản trị xem và nhập đc các số điện thoại
            edt_phone.setEnabled(true);
        }


        list_ketqua = (ListView) findViewById(R.id.list_ketqua);
        arrseri_bks_date = new ArrayList<EinsuranceObject>();
        adapter = new MyArrayAdapter(this, R.layout.seri_list, arrseri_bks_date);
        list_ketqua.setAdapter(adapter);

        //thiết lập số điện thoại mặc định cho edittext phone
        edt_phone.setText(phone);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dataFee);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spinner = (Spinner) findViewById(R.id.typeSpinnerMoney);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    feeStatus = "0";
                } else if (position == 1) {
                    feeStatus = "C";
                } else if (position == 2) {
                    feeStatus = "K";
                } else if (position == 3) {
                    feeStatus = "HD";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        feeStatus = "0";
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (data != null) {
                super.onActivityResult(requestCode, resultCode, data);
                String send_value = data.getStringExtra("data");
                String[] re = send_value.split(";");
                if (Integer.parseInt(re[4].toString()) == 0) {
                    if (requestCode == REQUEST_CODE_INPUT) {
                        if (resultCode == RESULT_CODE_SAVE) {
                            arrseri_bks_date.get(Integer.parseInt(re[3].toString())).setseri(re[0].toString());
                            arrseri_bks_date.get(Integer.parseInt(re[3].toString())).setbks(re[1].toString());
                            arrseri_bks_date.get(Integer.parseInt(re[3].toString())).setlhbh(re[2].toString());
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        } catch (Exception ex) {

        }

    }


    public void addEvents() {
        btn_seach.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                arrseri_bks_date.clear();
                if (edt_dseri.getText().toString() != "" || edt_sbks.getText().toString() != "" || edt_phone.getText().toString() != "") {
                    SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                    request.addProperty("seri", edt_dseri.getText().toString());
                    request.addProperty("bks_sk", edt_sbks.getText().toString());
                    request.addProperty("phone", edt_phone.getText().toString());
                    request.addProperty("aid", aid_agent);
                    request.addProperty("feeStatus", feeStatus);
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.setOutputSoapObject(request);
                    envelope.dotNet = true;
                    try {
                        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 1000 * 60 * 2);
                        androidHttpTransport.call(SOAP_ACTION, envelope);

                        SoapObject result = (SoapObject) envelope.bodyIn;
                        SoapObject soapresults = (SoapObject) result.getProperty(0);
                        if (soapresults.getPropertyCount() > 0) {
                            for (int i = 0; i < soapresults.getPropertyCount(); i++) {
                                EinsuranceObject seri_bks_ = new EinsuranceObject();
                                String[] re = soapresults.getProperty(i).toString().split(";");
                                seri_bks_.setid(re[0].toString());
                                seri_bks_.setseri(re[1].toString());
                                seri_bks_.setbks(re[2].toString());
                                seri_bks_.setlhbh(re[3].toString());
                                seri_bks_.setdate(re[4].toString());
                                seri_bks_.setphone(re[5].toString());
                                seri_bks_ .setFeeStatus(re[6].toString());
                                arrseri_bks_date.add(seri_bks_);

                            }
                            adapter.notifyDataSetChanged();

                            //tìm xong thì ẩn bàn phím
                            InputMethodManager dseri = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            dseri.hideSoftInputFromWindow(edt_dseri.getWindowToken(), 0);
                            InputMethodManager sbks = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            sbks.hideSoftInputFromWindow(edt_sbks.getWindowToken(), 0);
                        } else {
                            showToast("Không tìm thấy dữ liệu ");
                        }

                    } catch (Exception ex) {
                        showToast("Không tìm thấy dữ liệu ");
                    }

                } else {
                    showToast("Hãy Nhập thông tin tìm kiếm");
                }
                //  btn_seach.setFocusable(true);


            }

        });


        list_ketqua.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EinsuranceObject a = new EinsuranceObject();
                a = arrseri_bks_date.get(position);
                String send_value = a.getid() + ";" + a.getseri() + ";" + a.getbks() + ";" + a.getlhbh() + ";" + position + ";" + a.getphone() + ";" + phone + ";" + quantri + ";" + a.getdate();

                Bundle send_info = new Bundle();
                send_info.putString("send_value", send_value);
                Intent intent = new Intent(getApplicationContext(), SerialEditorActivity.class);
                intent.putExtras(send_info);
                startActivityForResult(intent, REQUEST_CODE_INPUT);// startActivity(intent);

            }
        });

    }

    public void getFormWidgets() {
        btn_seach = (Button) findViewById(R.id.btn_seach);
        edt_dseri = (EditText) findViewById(R.id.edt_dseri);
        edt_phone = (EditText) findViewById(R.id.edt_phone);
        edt_sbks = (EditText) findViewById(R.id.edt_sbks);
        list_ketqua = (ListView) findViewById(R.id.list_ketqua);

    }

    public void thoat(View v) {
        finish();

    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }


}
