package com.pvi.adapter;

import java.util.ArrayList;

import com.pvi.activities.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuAdapter extends ArrayAdapter<MenuItem> {
	private Context mContext;
	private int layoutResourceId;
	private ArrayList<MenuItem> data = new ArrayList<MenuItem>();

	// Keep all Images in array
	public Integer[] mThumbIds = { R.drawable.calculator,
			R.drawable.einsurance, R.drawable.survey, R.drawable.claim,
			R.drawable.tracuu, R.drawable.license_manager };

	// Constructor
	public MenuAdapter(Context context, int layoutResourceId,
			ArrayList<MenuItem> data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.mContext = context;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		RecordHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new RecordHolder();
			holder.txtTitle = (TextView) row.findViewById(R.id.item_text);
			holder.imageItem = (ImageView) row.findViewById(R.id.contentImage);
			row.setTag(holder);
		} else {
			holder = (RecordHolder) row.getTag();
		}

		MenuItem item = data.get(position);
		holder.txtTitle.setText(item.getTitle());
		holder.imageItem.setImageBitmap(item.getImage());
		return row;

	}

	static class RecordHolder {
		TextView txtTitle;
		ImageView imageItem;

	}

}
