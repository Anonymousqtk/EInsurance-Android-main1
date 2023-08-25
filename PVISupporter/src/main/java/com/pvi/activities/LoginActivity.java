package com.pvi.activities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.pvi.activities.LoginActivity;
import com.pvi.activities.UpdateSoapURLActivity;
import com.pvi.activities.SelectActionActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
    private static final String PVI_AUTOCARE = "pvi_autocare";
    private static final String OLD_DAY_SAVED = "old_day_saved";
    private static final String RECOVER_DAY = "recover_day";
    private static final int SESSION_DAYS = 30;
    private String address = "";
    private Button btn_dn;
    private EditText edt_tk, edt_mk;
    private CheckBox chk_luupass;
    String prefname = "my_data";
    private String change_pass;
    private static String SOAP_ACTION = "http://tempuri.org/Check_DK";
    private static String NAMESPACE = "http://tempuri.org/";
    private static String METHOD_NAME = "Check_DK";
    private static String URL;

    private static String FAILED = "0";
    private static String PHONE_LOCKED = "-1";
    private static String OWNER_LOCKED = "-2";
    private static String NOT_AVAILABLE = "-3";
    private static String INVALIDATE = "-4";
    private static String SUCCESS = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
        getFormWidgets();
        addEvents();
    }

    // để hiểu onpause() và onresume thì do vòng đời của activity:
    // running->oncreate()->onstart()->onresume()->onpause->destroy()
    @Override
    protected void onPause() {
        super.onPause();
        // onpause() chạy sau cùng của activity login nên đc sử dụng để lưu
        // thông tin vào SharedPreferences
        savingPreferences();
    }

    @Override
    protected void onResume() {
        // onresume()chạy lúc đầu sau onstart() nên đc dùng để đọc thông tin
        super.onResume();
        restoringPreferences();
    }

    public void savingPreferences() {
        // tạo đối tượng getSharedPreferences
        SharedPreferences pre = getSharedPreferences(prefname, MODE_PRIVATE);
        String address_check = pre.getString("web_url", "");
        // tạo đối tượng Editor để lưu thay đổi
        SharedPreferences.Editor editor = pre.edit();
        String user = edt_tk.getText().toString();
        String pwd = edt_mk.getText().toString();
        boolean bchk = chk_luupass.isChecked();
        if (address_check.equals("")) {
            editor.putString("web_url", address);
        }
        if (!bchk) {
            // xóa mọi lưu trữ trước đó
            // editor.clear();
            editor.remove("user");
            editor.remove("pwd");
            editor.remove("checked");
        } else {
            // lưu vào editor
            editor.putString("user", user);
            editor.putString("pwd", pwd);
            editor.putBoolean("checked", bchk);
        }
        // chấp nhận lưu xuống file
        editor.commit();
    }

    /**
     * hàm đọc trạng thái đã lưu trước đó
     */
    public void restoringPreferences() {
        SharedPreferences pre = getSharedPreferences(prefname, MODE_PRIVATE);
        address = pre.getString("web_url", "103.26.252.13");
        URL = "http://" + address + "/Soap_mobile/Mobile_soap.asmx";
//        URL = "http://103.26.252.13/soapmobiletest/Mobile_soap.asmx";
        // lấy giá trị checked ra, nếu không thấy thì giá trị mặc định là false
        boolean bchk = pre.getBoolean("checked", false);
        if (bchk) { // lấy user, pwd, nếu không thấy giá trị mặc định là rỗng
            String user = pre.getString("user", "");
            String pwd = pre.getString("pwd", "");
            edt_tk.setText(user);
            edt_mk.setText(pwd);
        }
        chk_luupass.setChecked(bchk);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_login, menu);
        return true;
    }

    public void call_choose_action(View v) {
        Intent intent = new Intent(LoginActivity.this, SelectActionActivity.class);
        startActivity(intent);
    }

    public void thoat(View v) {
        finish();
        System.exit(0);
    }

    private void addEvents
            () {
        btn_dn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (check_input() == true && isNetworkConnected() == true) {
                    SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                    request.addProperty("phone", edt_tk.getText().toString());
                    request.addProperty("matkhau", edt_mk.getText().toString());
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
                            String[] aid_type;
                            String res, type = "";
                            String res_type = result.getProperty(0).toString();
                            if (res_type.indexOf(";") != -1) {
                                aid_type = res_type.split(";");
                                res = aid_type[0].toString();
                                type = aid_type[1].toString();
                                change_pass = aid_type[2].toString();
                            } else {
                                res = res_type;
                            }

                            if (res.equals("1")) {
                                showToast("Số điện thoại đã bị khóa");
                            } else if (res.equals("2")) {
                                showToast("Đơn vị chủ quản bị khóa");
                            } else if (res.equals("3")) {
                                showToast("Sai mật khẩu");
                            } else if (res.equals("4")) {
                                showToast("Không tồn tại số điện thoại");
                            } else {
                                // save current Date
                                SharedPreferences pre = getSharedPreferences(PVI_AUTOCARE, MODE_PRIVATE);
                                String oldDay = pre.getString(OLD_DAY_SAVED, null);
                                if (oldDay == null || (MenuActivity.getDateDiff(oldDay, TimeUnit.DAYS) >= SESSION_DAYS)) {
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                                    oldDay = sdf.format(new Date());
                                    SharedPreferences.Editor editor = pre.edit();
                                    editor.putString(OLD_DAY_SAVED, oldDay);
                                    editor.commit();
                                }

                                // operation for password
                                if (change_pass.equals("0")) {
                                    boolean save_tk = chk_luupass.isChecked();
                                    String tk_save = "";
                                    if (save_tk) {
                                        tk_save = "save";
                                    } else {
                                        tk_save = "nosave";
                                    }

                                    String send_passold = edt_tk.getText().toString() + ";" + edt_mk.getText().toString() + ";" + res + ";" + type + ";" + tk_save;
                                    Bundle send_oldpass = new Bundle();
                                    send_oldpass.putString("send_passold", send_passold);
                                    Intent intent = new Intent(getApplicationContext(), UpdatePasswordActivity.class);
                                    intent.putExtras(send_oldpass);
                                    startActivity(intent);

                                } else { // đã thay đổi mật khẩu
                                    String agent_phone = edt_tk.getText().toString() + ";" + res + ";" + type;
                                    Bundle send_agent_phone = new Bundle();
                                    send_agent_phone.putString("agent_phone", agent_phone);
                                    Intent intent = new Intent(getApplicationContext(), SelectActionActivity.class);
                                    intent.putExtras(send_agent_phone);
                                    startActivity(intent);
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Có lỗi trong quá trình đăng nhập", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception ex) {
                        // trường hợp có thay đổi địa chỉ web services khi kết
                        // nối không đc sẽ bật ra form thay đổi địa chỉ
                        Intent intent = new Intent(getApplicationContext(), UpdateSoapURLActivity.class);
                        startActivity(intent);

                    }

                }

            }
        });
    }

    private void getFormWidgets() {
        btn_dn = (Button) findViewById(R.id.btn_dn);
        edt_tk = (EditText) findViewById(R.id.edt_tk);
        edt_mk = (EditText) findViewById(R.id.edt_mk);
        chk_luupass = (CheckBox) findViewById(R.id.chk_luupass);
        TextView textView = (TextView) findViewById(R.id.forgotPassword);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pre = getSharedPreferences(PVI_AUTOCARE, MODE_PRIVATE);
                String oldDay = pre.getString(RECOVER_DAY, null);
                if (oldDay == null || (MenuActivity.getDateDiff(oldDay, TimeUnit.DAYS) >= 1)) {
                    showInputValueAlert();
                } else {
                    showToast("Thiết bị này đã được dùng để khôi phục mật khẩu trong vòng 1 ngày. Hãy thử lại lần sau.");
                }
            }
        });
    }

    private boolean check_input() {
        boolean rt = true;
        if (edt_tk.length() == 0 || edt_mk.length() == 0) {
            Toast toast = Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin đăng nhập!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            rt = false;
        } else if (edt_tk.length() != 0 && edt_mk.length() != 0) {
            rt = true;
        }
        return rt;

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            Toast toast = Toast.makeText(this, "Chưa kết nối Internet", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            return false;
        } else
            return true;
    }

    private void showInputValueAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
        alertDialog.setIcon(R.drawable.ic_launcher);
        alertDialog.setTitle("PVI eInsurance");
        alertDialog.setMessage("Nhập số điện thoại cần khôi phục mật khẩu.\nChú ý: mỗi số điện thoại chỉ được khôi phục mật khẩu tối đa 1 lần/ngày!");

        final EditText input = new EditText(LoginActivity.this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("09X XXX XXXX");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String phoneNumber = input.getText().toString();
                sendSMS(phoneNumber);
            }
        });
        alertDialog.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void sendSMS(String phoneNumber) {
        String method = "resetPassword";
        String action = "http://tempuri.org/resetPassword";

        SoapObject request = new SoapObject(NAMESPACE, method);
        request.addProperty("phoneNumber", phoneNumber);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;
        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 1000 * 60 * 2);
            androidHttpTransport.call(action, envelope);
            androidHttpTransport.debug = true;
            SoapObject result = (SoapObject) envelope.bodyIn;
            if (result.getPropertyCount() > 0) {
                String value = result.getProperty(0).toString();
                if (value.equals(FAILED)) {
                    showToast("Không thể thực hiện yêu cầu! Vui lòng liên hệ hotline để được hỗ trợ");
                } else if (value.equals(PHONE_LOCKED)) {
                    showToast("Số điện thoại bị khóa!");
                } else if (value.equals(OWNER_LOCKED)) {
                    showToast("Đơn vị chủ quản bị khóa");
                } else if (value.equals(NOT_AVAILABLE)) {
                    showToast("Không tồn tại số điện thoại trên hệ thống báo phát sinh");
                } else if (value.equals(INVALIDATE)) {
                    showToast("Số điện thoại đã khôi phục mật khẩu trong vòng 1 ngày. Thử lại lần sau");
                } else if (value.equals(SUCCESS)) {
                    showToast("Thao tác thành công! Hãy kiểm tra tin nhắn để khôi phục mật khẩu!");
                    // Lưu thông tin đã được recover
                    SharedPreferences pre = getSharedPreferences(PVI_AUTOCARE, MODE_PRIVATE);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                    String oldDay = sdf.format(new Date());
                    SharedPreferences.Editor editor = pre.edit();
                    editor.putString(RECOVER_DAY, oldDay);
                    editor.commit();
                }
            } else {
                showToast("Lỗi hệ thống! Vui lòng liên hệ hotline để được hỗ trợ");
            }
        } catch (Exception ex) {
            showToast("Lỗi hệ thống: " + ex.toString());
        }
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

}
