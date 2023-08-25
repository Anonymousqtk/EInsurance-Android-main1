package com.pvi.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import com.pvi.objects.SMSErrorObject;
import com.pvi.activities.R;

public class ErrorAdapter extends ArrayAdapter<SMSErrorObject> {

	Activity context = null;
	ArrayList<SMSErrorObject> myArray = null;
	int layoutId;

	public ErrorAdapter(Activity context, int layoutId, ArrayList<SMSErrorObject> arr) {
		super(context, layoutId, arr);
		this.context = context;
		this.layoutId = layoutId;
		this.myArray = arr;
	}

	@SuppressLint("ViewHolder")
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = context.getLayoutInflater();
		convertView = inflater.inflate(layoutId, null);

		if (myArray.size() > 0 && position >= 0) {

			final TextView t1 = (TextView) convertView.findViewById(R.id.t1);
			final TextView t2 = (TextView) convertView.findViewById(R.id.t2);
			final TextView t3 = (TextView) convertView.findViewById(R.id.t3);
			final TextView t4 = (TextView) convertView.findViewById(R.id.t4);

			// lấy ra nhân viên thứ position
			final SMSErrorObject seri_bks_ = myArray.get(position);
			// đưa thông tin lên TextView
			t1.setText(seri_bks_.getnoidungsms());
			t2.setText(seri_bks_.getngayps());
			t3.setText(seri_bks_.getphone());
			t4.setText(seri_bks_.getphanhoi());

		}

		return convertView;
	}

}
