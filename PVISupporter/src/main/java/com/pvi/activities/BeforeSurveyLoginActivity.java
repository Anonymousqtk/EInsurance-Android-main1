package com.pvi.activities;

import com.pvi.helpers.AsyncTaskManager;
import com.pvi.helpers.GlobalData;
import com.pvi.helpers.GlobalMethod;
import com.pvi.helpers.LoginHelper;
import com.pvi.helpers.OnAsyncTaskCompleteListener;
import com.pvi.helpers.Reachability;
import com.pvi.helpers.TimeService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class BeforeSurveyLoginActivity extends Activity {

    private static final String GDDK_USER_NAME = "GDDK_USER_NAME";
    private static final String GDDK_PASSWORD = "GDDK_PASSWORD";
    private static final String GDDK_CHECKED = "GDDK_CHECKED";
    private static final String WRONG_PASS = "0";
    private static final String INVALID_USER = "-1";
    private static final String LOGIN_PARAM = "0";
    private CheckBox isSaveButton;
    private AsyncTaskManager taskManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.before_survey_login_activity);
        isSaveButton = (CheckBox) findViewById(R.id.chksave_pass_survey);
        taskManager = new AsyncTaskManager(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        final EditText userName = (EditText) findViewById(R.id.edtma_user_survey);
        final EditText password = (EditText) findViewById(R.id.edtpass_survey);
        Button loginButton = (Button) findViewById(R.id.btn_login_survey);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(userName.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
                getLogin(userName.getText().toString(), password.getText().toString());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        GlobalData.getInstance().setUsername(null);
        // kiểm tra thiết bị có kết nối mạng không
        if (!Reachability.isInternetAvailable(getApplicationContext())) {
            // call activity offline menu;
            GlobalData.getInstance().setUsername(null);
            Intent intent = new Intent(getApplicationContext(), BeforeSurveyOfflineMenu.class);
            startActivity(intent);
            this.finish();
            return;
        }

        if (taskManager == null) {
            taskManager = new AsyncTaskManager(this);
        }

        restoringPreferences();

    }

    private void getLogin(final String userName, String password) {
        LoginHelper task = new LoginHelper();
        taskManager.executeTask(task, LoginHelper.createRequest(userName, password, LOGIN_PARAM), getString(R.string.claim_get_login), new OnAsyncTaskCompleteListener<String>() {
            @Override
            public void onTaskCompleteSuccess(String result) {
                String[] data = result.split("\\|");
                if (data[0].equals(WRONG_PASS)) {
                    showToast("Sai mật khẩu");
                } else if (data[0].equals(INVALID_USER)) {
                    showToast("Không tồn tại user này");
                } else {
                    savingPreferences();
                    GlobalData.getInstance().setUnitCode(data[0]);
                    GlobalData.getInstance().setUsername(userName);
                    Intent intent = new Intent(getApplicationContext(), BeforeSurveyActivity.class);
                    startActivity(intent);
                }

                if ((data.length) > 1) {
                    String typeUser = data[1], version = data[2], url = data[3], time = data[4];
                    GlobalData.getInstance().setTypeUser(typeUser.toLowerCase());
                    GlobalData.getInstance().setVersion(version);
                    GlobalData.getInstance().setUrl(url);
                    startTimeService(time);
                }
            }

            @Override
            public void onTaskFailed(Exception cause) {
                showToast(cause.getMessage() + "Lỗi kết nối!");
            }
        });

    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    private void startTimeService(String initTime) {
        if (!GlobalMethod.isServiceRunning(TimeService.class, this)) {
            Intent serviceIntent = new Intent(getApplicationContext(), TimeService.class);
            serviceIntent.putExtra(TimeService.RECEIVER_FLAG, initTime);
            startService(serviceIntent);
        }
    }

    private void savingPreferences() {
        EditText userName = (EditText) findViewById(R.id.edtma_user_survey);
        EditText password = (EditText) findViewById(R.id.edtpass_survey);
        SharedPreferences pre = getSharedPreferences(GlobalMethod.GDDK_STORE, MODE_PRIVATE);

        SharedPreferences.Editor editor = pre.edit();
        String user = userName.getText().toString();
        String pwd = password.getText().toString();
        boolean savepass = isSaveButton.isChecked();
        if (!savepass) {
            editor.remove(GDDK_USER_NAME);
            editor.remove(GDDK_PASSWORD);
            editor.remove(GDDK_CHECKED);
        } else {
            editor.putString(GDDK_USER_NAME, user);
            editor.putString(GDDK_PASSWORD, pwd);
            editor.putBoolean(GDDK_CHECKED, savepass);
        }

        editor.commit();
    }

    private void restoringPreferences() {
        EditText userName = (EditText) findViewById(R.id.edtma_user_survey);
        EditText password = (EditText) findViewById(R.id.edtpass_survey);
        SharedPreferences pre = getSharedPreferences(GlobalMethod.GDDK_STORE, MODE_PRIVATE);
        boolean isSaved = pre.getBoolean(GDDK_CHECKED, false);
        if (isSaved) {
            String user = pre.getString(GDDK_USER_NAME, "");
            String pwd = pre.getString(GDDK_PASSWORD, "");
            userName.setText(user);
            password.setText(pwd);
            isSaveButton.setChecked(isSaved);
        }
    }
}
