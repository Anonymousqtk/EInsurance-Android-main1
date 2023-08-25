package com.pvi.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class UpdatePasswordActivity extends Activity {
    EditText edt_newpass, edt_comfimpass;
    Button btn_dongy, btn_thoat;
    String old_pass, phone, ma_dv, type, tk_save;
    private String address = "";
    String prefname = "my_data"; // tên file giống kiểu webconfig
    private static String SOAP_ACTION = "http://tempuri.org/update_pass";
    private static String NAMESPACE = "http://tempuri.org/";
    private static String METHOD_NAME = "update_pass";//trong này đã thay đổi thuộc tính change_pass=1
    private static String URL;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_pass);
        getFormWidgets();
        addEvents();

        // lấy dữ liệu từ bulte

        Bundle receive_bundle = this.getIntent().getExtras();
        final String receive_value = receive_bundle.getString("send_passold");

        String[] agent_phone;
        agent_phone = receive_value.split(";");
        phone = agent_phone[0].toString();
        old_pass = agent_phone[1].toString();
        ma_dv = agent_phone[2].toString();
        type = agent_phone[3].toString();
        tk_save = agent_phone[4].toString();


    }

    @Override
    protected void onPause() {
        super.onPause();
        // onpause() chạy sau cùng của activity login nên đc sử dụng để lưu thông tin vào SharedPreferences
        savingPreferences();
    }

    public void savingPreferences() {
        //tạo đối tượng getSharedPreferences
        SharedPreferences pre = getSharedPreferences(prefname, MODE_PRIVATE);
        //tạo đối tượng Editor để lưu thay đổi
        SharedPreferences.Editor editor = pre.edit();
        editor.putString("check_first_login", "1");
        if (tk_save.equals("save")) {
            editor.remove("user");
            editor.remove("pwd");
            editor.remove("checked");
            editor.commit();
            editor.putString("user", phone);
            editor.putString("pwd", edt_newpass.getText().toString());
            editor.putBoolean("checked", true);
        }

        // editor.putString("pwd", edt_newpass.getText().toString());
        //chấp nhận lưu xuống file
        editor.commit();
    }

    @Override
    protected void onResume() {
        // onresume()chạy lúc đầu sau onstart() nên đc dùng để đọc thông tin
        super.onResume();
        restoringPreferences();
    }

    public void restoringPreferences() {
        SharedPreferences pre = getSharedPreferences(prefname, MODE_PRIVATE);
        address = pre.getString("web_url", "103.26.252.13");
        URL = "http://" + address + "/Soap_mobile/Mobile_soap.asmx?WSDL";
        //lấy giá trị checked ra, nếu không thấy thì giá trị mặc định là false
    }

    @Override
    public void onBackPressed() {
//     khi người dùng bấm back button thì không làm gì cả,
//     phải quay lại bằng nút 'quay lại' trên popup để kích hoạt
//     startActivityForResult vì không sử dụng đc intent này trong onBackPressed

    }

    public void addEvents() {
        btn_dongy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (edt_newpass.getText().toString().equals("") || edt_comfimpass.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Chưa nhập đầy đủ thông tin", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                } else {
                    if (edt_newpass.getText().toString().equals(edt_comfimpass.getText().toString())) {
                        if (edt_newpass.getText().toString().equals(old_pass.toString())) {
                            Toast toast = Toast.makeText(getApplicationContext(), "Mật khẩu mới không được trùng với mật khẩu cũ!", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                        } else {
                            if (edt_newpass.getText().toString().length() < 5) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Mật khẩu tối thiểu 5 ký tự", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();
                            } else {   //gọi soap update
                                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                                request.addProperty("phone", phone);
                                request.addProperty("new_pass", edt_newpass.getText().toString());
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
                                            Toast toast = Toast.makeText(getApplicationContext(), "Thay đổi mật khẩu không thành công", Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                            toast.show();
                                        } else {

                                            Toast toast = Toast.makeText(getApplicationContext(), "Cập nhật thành công", Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                            toast.show();

                                            String agent_phone = phone + ";" + ma_dv + ";" + type;
                                            Bundle send_agent_phone = new Bundle();
                                            send_agent_phone.putString("agent_phone", agent_phone);
                                            Intent intent = new Intent(getApplicationContext(), SelectActionActivity.class);
                                            intent.putExtras(send_agent_phone);
                                            startActivity(intent);

                                            finish();
                                        }
                                    }
                                } catch (Exception ex) {


                                }


                            }

                        }

                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), "Xác nhận mật khẩu không đúng", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();
                    }

                }


            }
        });
        btn_thoat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }


        });
    }


    public void getFormWidgets() {
        edt_newpass = (EditText) findViewById(R.id.edt_newpass);
        edt_comfimpass = (EditText) findViewById(R.id.edt_comfimpass);
        btn_dongy = (Button) findViewById(R.id.btn_dongy);
        btn_thoat = (Button) findViewById(R.id.btn_thoat);
    }
}
