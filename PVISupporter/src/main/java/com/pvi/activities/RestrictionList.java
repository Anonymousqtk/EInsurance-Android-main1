package com.pvi.activities;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pvi.adapter.RestrictionAdapter;
import com.pvi.helpers.AsyncTaskManager;
import com.pvi.helpers.GetBlacklistCar;
import com.pvi.helpers.OnAsyncTaskCompleteListener;
import com.pvi.objects.PVICarObject;

import java.util.List;

public class RestrictionList extends Activity {

    private EditText inputSearch;
    private ListView listView;
    private AsyncTaskManager taskManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restriction_list);
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

        GetBlacklistCar task = new GetBlacklistCar();
        taskManager.executeTask(task, GetBlacklistCar.createRequest(inputSearch.getText().toString()), getString(R.string.title_activity_restriction_list), new OnAsyncTaskCompleteListener<List<PVICarObject>>() {
            @Override
            public void onTaskCompleteSuccess(List<PVICarObject> result) {
                if (result.size() <= 0) {
                    showToast("Không tìm thấy xe có thông tin như trên!");
                    return;
                }
                RestrictionAdapter adapter = new RestrictionAdapter(RestrictionList.this, R.layout.car_row, result);
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);
            }

            @Override
            public void onTaskFailed(Exception cause) {
                showToast(cause.getMessage() + " Tìm xe thất bại. Hãy thử lại!");
            }
        });
    }

}
