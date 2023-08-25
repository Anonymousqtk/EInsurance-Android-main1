package com.pvi.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.pvi.adapter.ShipAdapter;
import com.pvi.helpers.AsyncTaskManager;
import com.pvi.helpers.GetShipInfoHelper;
import com.pvi.helpers.GlobalData;
import com.pvi.helpers.OnAsyncTaskCompleteListener;
import com.pvi.objects.PVIShipObject;

import java.util.List;

/**
 * Created by tuyenpt on 13/04/2016.
 */
public class BeforeShipSurveyActivity extends Activity implements SearchView.OnQueryTextListener {
    private SearchView searchView;
    private ListView listView;
    private AsyncTaskManager taskManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ship_create);
        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(true);
        listView = (ListView) findViewById(R.id.listView);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (GlobalData.getInstance().getUsername() == null) {
            this.finish();
        }
        if (taskManager == null) {
            taskManager = new AsyncTaskManager(this);
        }
    }

    /**
     * Called when the user submits the query. This could be due to a key press on the
     * keyboard or due to pressing a submit button.
     * The listener can override the standard behavior by returning true
     * to indicate that it has handled the submit request. Otherwise return false to
     * let the SearchView handle the submission by launching any associated intent.
     *
     * @param query the query text that is to be submitted
     * @return true if the query has been handled by the listener, false to let the
     * SearchView perform the default action.
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        searchView.clearFocus();
        if (query == null) {
            return false;
        }
        GetShipInfoHelper task = new GetShipInfoHelper();
        taskManager.executeTask(task, GetShipInfoHelper.createRequest(query), getString(R.string.claim_get_ship), new OnAsyncTaskCompleteListener<List<PVIShipObject>>() {
            @Override
            public void onTaskCompleteSuccess(List<PVIShipObject> result) {
                if (result.size() <= 0) {
                    showToast("Không tìm thấy tàu có thông tin như trên!");
                    return;
                }
                ShipAdapter adapter =  new ShipAdapter(BeforeShipSurveyActivity.this, R.layout.ship_row, result);
                adapter.taskManager = taskManager;
                adapter.isFromListview = false;
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);
            }

            @Override
            public void onTaskFailed(Exception cause) {
                showToast(cause.getMessage() + "Tìm tàu thất bại. Hãy thử lại!");
            }
        });
        return true;
    }

    /**
     * Called when the query text is changed by the user.
     *
     * @param newText the new content of the query text field.
     * @return false if the SearchView should perform the default action of showing any
     * suggestions if available, true if the action was handled by the listener.
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }
}
