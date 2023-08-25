package com.pvi.adapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.pvi.activities.R;
import com.pvi.objects.LoaiXeObject;

import java.util.ArrayList;
public class LoaiXeAdapter extends ArrayAdapter<LoaiXeObject> {

	Activity context = null;
	ArrayList<LoaiXeObject> dataArray = null;
	int layoutId;
	public LoaiXeAdapter(Activity context, int resource, ArrayList<LoaiXeObject> objects) {
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
			final LoaiXeObject loai_xe = dataArray.get(position);
			tv_title.setText(loai_xe.getTen_loaixe());
		}
		return convertView;
	}
}
