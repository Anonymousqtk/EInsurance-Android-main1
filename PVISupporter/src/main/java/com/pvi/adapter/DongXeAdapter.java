package com.pvi.adapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.pvi.activities.R;
import com.pvi.objects.DongXeObject;

import java.util.ArrayList;
public class DongXeAdapter extends ArrayAdapter<DongXeObject> {

	Activity context = null;
	ArrayList<DongXeObject> dataArray = null;
	int layoutId;
	public DongXeAdapter(Activity context, int resource, ArrayList<DongXeObject> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.layoutId = resource;
		this.dataArray = objects;
	}
	@SuppressLint("ViewHolder")
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		convertView = inflater.inflate(layoutId, null);
		if (dataArray.size() > 0 && position >= 0) {
			final TextView tv_title = (TextView) convertView
					.findViewById(R.id.item_spinner);
			final DongXeObject dongxe = dataArray.get(position);
			tv_title.setText(dongxe.getTen_dongxe());
		}
		return convertView;
	}
}
