package com.pvi.activities;

import java.util.List;

import com.pvi.adapter.SearchAdapter;
import com.pvi.helpers.DatabaseHelper;
import com.pvi.objects.BeforeSurveyOfflineObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;

public class BeforeSurveySelectOfflineActivity extends Activity implements OnClickListener, OnEditorActionListener, OnItemClickListener {
    Button btnSearch, btnLeft;
    EditText edSearch;
    ListView mListView;
    SearchAdapter adapter;
    private DatabaseHelper readHelper;
    List<BeforeSurveyOfflineObject> offlineList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.before_survey_select_offline_activity);
        // disable screen timeout while app is running
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        readHelper = DatabaseHelper.getReadableInstance(this);
        this.getFormWidgets();
        this.addEvents();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    private void getFormWidgets() {
        mListView = (ListView) findViewById(R.id.mListView_search);
        btnSearch = (Button) findViewById(R.id.btnSearch_dm);
        btnLeft = (Button) findViewById(R.id.btnLeft_search);
        edSearch = (EditText) findViewById(R.id.edSearch_dm);
        btnLeft.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        setData();
    }

    public void setData() {
        readHelper.open();
        offlineList = readHelper.getAllOfflineResumes(BeforeSurveySelectOfflineActivity.this, MODE_PRIVATE);
        readHelper.close();
        adapter = new SearchAdapter(this, R.layout.element_search_list, offlineList);
        adapter.notifyDataSetChanged();
        mListView.setOnItemClickListener(this);
        mListView.setAdapter(adapter);
    }

    @SuppressLint("DefaultLocale")
    public void setSearchResult(String id) {
        adapter = new SearchAdapter(this, R.layout.element_search_list);
        for (BeforeSurveyOfflineObject temp : offlineList) {
            if (temp.getLicensePlate().toLowerCase().contains(id.toLowerCase())) {
                adapter.addItem(temp);
            }
        }
        mListView.setAdapter(adapter);
    }

    private void addEvents() {
        edSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (0 != edSearch.getText().length()) {
                    String spnId = edSearch.getText().toString();
                    setSearchResult(spnId);
                } else {
                    setData();
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = getIntent();
        intent.putExtra("id_offline", adapter.getItem(position));
        setResult(BeforeSurveyDetailActivity.RESULT_CODE_SAVE_SEARCH, intent);
        finish();

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSearch:
                edSearch.setText("");
                setData();
                break;
            case R.id.btnLeft:
                break;
        }
    }
}
