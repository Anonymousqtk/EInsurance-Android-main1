package com.pvi.adapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.pvi.activities.R;
import com.pvi.objects.MucDichSdObject;

import java.util.ArrayList;
public class MucDichSdAdapter extends ArrayAdapter<MucDichSdObject> {

	Activity context = null;
	ArrayList<MucDichSdObject> dataArray = null;
	int layoutId;
	public MucDichSdAdapter(Activity context, int resource, ArrayList<MucDichSdObject> objects) {
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
			final MucDichSdObject mduch_sdung = dataArray.get(position);
			tv_title.setText(mduch_sdung.getTen_nhphi());
		}
		return convertView;
	}
}
