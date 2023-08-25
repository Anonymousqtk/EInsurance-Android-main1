package com.pvi.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

public class BeforeSurveySearchActivity extends Activity {
	SearchView search;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.before_survey_search_activity);

		search = (SearchView) findViewById(R.id.searchBeforeSurvey);
		search.setQueryHint("Nhập số ấn chỉ/BKS");

		search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				Toast.makeText(getBaseContext(), String.valueOf(hasFocus),
						Toast.LENGTH_SHORT).show();
			}
		});

		search.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
				Toast.makeText(getBaseContext(), query, Toast.LENGTH_SHORT)
						.show();

				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				Toast.makeText(getBaseContext(), newText, Toast.LENGTH_SHORT)
						.show();
				return false;
			}
		});
	}

}
