package com.pvi.activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.pvi.objects.EinsuranceObject;
import com.pvi.objects.SMSErrorObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class SerialSeachingActivity extends Activity {
    Button btn_view_tungay, btn_view_dn, btn_back_, btn_tracuu_seri, btn_ct_sms_suss, btn_loopup_notsussview, inputsussviewbt;
    EditText edt_phone_loop;
    TextView tv_kq_suss, tv_notsuss, tv_inputsuss;
    String address, aid_agent;
    public static final int requestCode_fromdate = 11111;
    public static final int resultCode_fromdate = 22222;
    public static final int requestCode_todate = 33333;
    public static final int resultCode_todate = 44444;
    public static final int requestCode_fromdate_pd = 55555;
    public static final int resultCode_fromdate_pd = 66666;
    public static final int requestCode_todate_pd = 77777;
    public static final int resultCode_todate_pd = 88888;
    String prefname = "my_data"; // tên file giống kiểu webconfig
    private static String SOAP_ACTION = "http://tempuri.org/seach_loopup_seri";
    private static String NAMESPACE = "http://tempuri.org/";
    private static String METHOD_NAME = "seach_loopup_seri";
    private static String URL;
    ArrayList<EinsuranceObject> arrseri_bks_date_suss = new ArrayList<EinsuranceObject>();
    ArrayList<EinsuranceObject> arrseri_bks_date_input = new ArrayList<EinsuranceObject>();
    ArrayList<SMSErrorObject> arrseri_bks_date_error = new ArrayList<SMSErrorObject>();
    String[] suss;

    @SuppressLint("SimpleDateFormat")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lookup_seri);
        getFormWidgets();
        addEvents();
        Date curent_date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String curDate = sdf.format(curent_date);
        btn_view_tungay.setText(curDate);
        btn_view_dn.setText(curDate);

        btn_ct_sms_suss.setEnabled(false);
        btn_loopup_notsussview.setEnabled(false);
        inputsussviewbt.setEnabled(false);

        Bundle receive_bundle = this.getIntent().getExtras();
        final String receive_value = receive_bundle.getString("agent_phone");
        String[] agent_phone;
        agent_phone = receive_value.split(";");
        aid_agent = agent_phone[1].toString();
        edt_phone_loop.setText(agent_phone[0].toString());
        if (agent_phone[2].toString().equals("1")) {

            edt_phone_loop.setEnabled(false);
        } else {
            edt_phone_loop.setEnabled(true);
        }

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

    /**
     *
     */
    public void addEvents() {

        btn_view_tungay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String check_date_pd = "get_fromdate_pd";
                Intent intent = new Intent(getApplicationContext(), DatePickerActivity.class);
                intent.putExtra("check_date_pd", check_date_pd);
                startActivityForResult(intent, requestCode_fromdate_pd);// startActivity(intent);
                tv_kq_suss.setText("0");
                tv_notsuss.setText("0");
                tv_inputsuss.setText("0");
                btn_ct_sms_suss.setEnabled(false);
                btn_loopup_notsussview.setEnabled(false);
                inputsussviewbt.setEnabled(false);
            }

        });
        btn_view_dn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String check_date_pd = "get_todate_pd";
                Intent intent = new Intent(getApplicationContext(), DatePickerActivity.class);
                intent.putExtra("check_date_pd", check_date_pd);
                startActivityForResult(intent, requestCode_todate_pd);// startActivity(intent);
                tv_kq_suss.setText("0");
                tv_notsuss.setText("0");
                tv_inputsuss.setText("0");
                btn_ct_sms_suss.setEnabled(false);
                btn_loopup_notsussview.setEnabled(false);
                inputsussviewbt.setEnabled(false);
            }

        });
        btn_ct_sms_suss.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }

        });

        btn_back_.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();

            }

        });

        btn_ct_sms_suss.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //tạo 1 intent dữ liệu arraylist truyền qua listiview_loopupseri

                Bundle suss_array = new Bundle();
                suss_array.putStringArray("suss", suss);
                suss_array.putString("filter_", "_suss");

                Intent intent = new Intent(getApplicationContext(), ListSerialActivity.class);
                intent.putExtras(suss_array);
                startActivity(intent);


            }

        });

        btn_loopup_notsussview.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //tạo 1 intent dữ liệu arraylist truyền qua listiview_loopupseri

                Bundle suss_array = new Bundle();
                suss_array.putStringArray("suss", suss);
                suss_array.putString("filter_", "_notsuss");

                Intent intent = new Intent(getApplicationContext(), ListSerialActivity.class);
                intent.putExtras(suss_array);
                startActivity(intent);


            }

        });


        inputsussviewbt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //tạo 1 intent dữ liệu arraylist truyền qua listiview_loopupseri

                Bundle suss_array = new Bundle();
                suss_array.putStringArray("suss", suss);
                suss_array.putString("filter_", "_input");
                Intent intent = new Intent(getApplicationContext(), ListSerialActivity.class);
                intent.putExtras(suss_array);
                startActivity(intent);


            }

        });

        btn_tracuu_seri.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Date n_bd = new Date(btn_view_tungay.getText().toString());
                Date n_kt = new Date(btn_view_dn.getText().toString());
                if (n_bd.compareTo(n_kt) > 0) //ngày bd lớn hơn ngày kết thúc
                {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Ngày bắt đầu lớn hơn ngày kết thúc", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                } else {

                    // Lấy thông tin tìm kiếm
                    // Gửi kết thông tin lên webservices
                    // Nhận kết quả về và hiển thị thông tin
                    arrseri_bks_date_suss.clear();
                    arrseri_bks_date_input.clear();
                    //java.util.Arrays.fill(suss,"");
                    arrseri_bks_date_error.clear();

                    tv_kq_suss.setText("0");
                    tv_notsuss.setText("0");
                    tv_inputsuss.setText("0");
                    btn_ct_sms_suss.setEnabled(true);
                    btn_loopup_notsussview.setEnabled(true);
                    inputsussviewbt.setEnabled(true);

                    SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                    request.addProperty("from_date", btn_view_tungay.getText().toString());
                    request.addProperty("to_date", btn_view_dn.getText().toString());
                    request.addProperty("phone", edt_phone_loop.getText().toString());
                    request.addProperty("aid", aid_agent);
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.setOutputSoapObject(request);
                    envelope.dotNet = true;
                    try {
                        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 1000 * 60 * 2);
                        androidHttpTransport.call(SOAP_ACTION, envelope);

                        SoapObject result = (SoapObject) envelope.bodyIn;
                        SoapObject soapresults = (SoapObject) result.getProperty(0);
                        if (soapresults.getPropertyCount() > 0) {
                            suss = new String[soapresults.getPropertyCount()];
                            for (int i = 0; i < soapresults.getPropertyCount(); i++) {
                                EinsuranceObject list_suss = new EinsuranceObject();
                                // seri_bks_date list_input_suss=new seri_bks_date();
                                String[] re = soapresults.getProperty(i).toString().split(";");
                                suss[i] = soapresults.getProperty(i).toString();//gán vào mảng suss để gán vào buller khi xem chi tiết
                                if (re[0].toString().equals("suss")) {
                                    list_suss.setid(re[1].toString());
                                    list_suss.setseri(re[2].toString());
                                    list_suss.setbks(re[3].toString());
                                    list_suss.setlhbh(re[4].toString());
                                    list_suss.setdate(re[5].toString());
                                    list_suss.setphone(re[6].toString());
                                    list_suss.setFeeStatus(re[7].toString());
                                    arrseri_bks_date_suss.add(list_suss);
                                }

                                if (re[0].toString().equals("notsuss")) {
                                    SMSErrorObject list_err = new SMSErrorObject();
                                    list_err.setid(re[1].toString());
                                    list_err.setnoidungsms(re[2].toString());
                                    list_err.setngayps(re[3].toString());
                                    list_err.setphone(re[4].toString());
                                    list_err.setphanhoi(re[5].toString());
                                    arrseri_bks_date_error.add(list_err);

                                }


                                if (re[0].toString().equals("input")) {
                                    list_suss.setid(re[1].toString());
                                    list_suss.setseri(re[2].toString());
                                    list_suss.setbks(re[3].toString());
                                    list_suss.setlhbh(re[4].toString());
                                    list_suss.setdate(re[5].toString());
                                    list_suss.setphone(re[6].toString());
                                    list_suss.setFeeStatus(re[7].toString());
                                    arrseri_bks_date_input.add(list_suss);
                                }


                            }
                            tv_kq_suss.setText(String.valueOf(arrseri_bks_date_suss.size()));
                            tv_notsuss.setText(String.valueOf(arrseri_bks_date_error.size()));
                            tv_inputsuss.setText(String.valueOf(arrseri_bks_date_input.size()));

//	                    	  adapter.notifyDataSetChanged();

                        } else {
                            showToast("Không tìm thấy dữ liệu ");
                        }

                    } catch (Exception ex) {
                        showToast("Không tìm thấy dữ liệu ");
                    }

                }
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String get_date = data.getStringExtra("data");
            if (requestCode == requestCode_fromdate) {
                if (resultCode == resultCode_fromdate) {
                    btn_view_tungay.setText(get_date);
                }
            }
            if (requestCode == requestCode_todate) {
                if (resultCode == resultCode_todate) {
                    btn_view_dn.setText(get_date);
                }
            }

            if (requestCode == requestCode_fromdate_pd) {
                if (resultCode == resultCode_fromdate_pd) {
                    btn_view_tungay.setText(get_date);
                }
            }
            if (requestCode == requestCode_todate_pd) {
                if (resultCode == resultCode_todate_pd) {
                    btn_view_dn.setText(get_date);
                }
            }


        }
    }

    public void getFormWidgets() {

        btn_view_tungay = (Button) findViewById(R.id.btn_view_tungay);

        btn_view_dn = (Button) findViewById(R.id.btn_view_dn);

        btn_ct_sms_suss = (Button) findViewById(R.id.btn_ct_sms_suss);
        btn_loopup_notsussview = (Button) findViewById(R.id.btn_loopup_notsussview);
        inputsussviewbt = (Button) findViewById(R.id.inputsussviewbt);


        edt_phone_loop = (EditText) findViewById(R.id.edt_phone_loop);
        btn_back_ = (Button) findViewById(R.id.btn_back_);
        btn_tracuu_seri = (Button) findViewById(R.id.btn_tracuu_seri);
        tv_kq_suss = (TextView) findViewById(R.id.tv_kq_suss);
        tv_notsuss = (TextView) findViewById(R.id.tv_notsuss);
        tv_inputsuss = (TextView) findViewById(R.id.tv_inputsuss);

    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

}
