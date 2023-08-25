package com.pvi.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pvi.adapter.RestrictionAdapter;
import com.pvi.helpers.AsyncTaskManager;
import com.pvi.helpers.GetLossHistory;
import com.pvi.helpers.OnAsyncTaskCompleteListener;
import com.pvi.objects.LossHistoryObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LossHistoryActivity extends Activity {
    private EditText inputSearch,inputSearchSerial,inputSearchPolicy;
    private ListView listView;
    private AsyncTaskManager taskManager;
    private Button btnNgaycuoi;
    private CheckBox checkBoxDonbh;
    public static final int requestCode_fromdate_pd = 55555;
    public static final int resultCode_fromdate_pd = 66666;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loss_history);

        inputSearch = (EditText) findViewById(R.id.inputSearch);
        inputSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });
        listView = (ListView) findViewById(R.id.restrictionList);
        Button search = (Button) findViewById(R.id.goSearchAction);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });

        inputSearchSerial = (EditText) findViewById(R.id.inputSearchSerial);
        inputSearchPolicy = (EditText) findViewById(R.id.inputSearchPolicy);

        btnNgaycuoi = (Button) findViewById(R.id.btnNgaycuoi);
        btnNgaycuoi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String check_date_pd = "get_fromdate_pd";
                Intent intent = new Intent(getApplicationContext(), DatePickerActivity.class);
                intent.putExtra("check_date_pd", check_date_pd);
                startActivityForResult(intent, requestCode_fromdate_pd);
            }

        });

        checkBoxDonbh = (CheckBox)findViewById(R.id.checkBox_donbh);

//        Date current_date = new Date();
//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//        String curDate = sdf.format(current_date);
        btnNgaycuoi.setText("");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (taskManager == null) {
            taskManager = new AsyncTaskManager(this);
        }
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    private void performSearch() {
        // hide all keyboards
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        GetLossHistory task = new GetLossHistory();
        taskManager.executeTask(task, GetLossHistory.createRequest(inputSearch.getText().toString(),btnNgaycuoi.getText().toString(),inputSearchSerial.getText().toString(),inputSearchPolicy.getText().toString(), checkBoxDonbh.isChecked() ? "1" : "0"), getString(R.string.title_activity_loss_history), new OnAsyncTaskCompleteListener<List<LossHistoryObject>>() {
            @Override
            public void onTaskCompleteSuccess(List<LossHistoryObject> result) {
                if (result.size() <= 0) {
                    showToast("Không tìm thấy xe có thông tin như trên!");
                    return;
                }
                RestrictionAdapter adapter = new RestrictionAdapter(LossHistoryActivity.this, R.layout.car_row, result, RestrictionAdapter.LOSS_HISTORY);
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);
            }

            @Override
            public void onTaskFailed(Exception cause) {
                showToast(cause.getMessage() + " Tìm xe thất bại. Hãy thử lại!");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String get_date = data.getStringExtra("data");

            if (requestCode == requestCode_fromdate_pd) {
                if (resultCode == resultCode_fromdate_pd) {
                    btnNgaycuoi.setText(get_date);
                }
            }


        }
    }
}
