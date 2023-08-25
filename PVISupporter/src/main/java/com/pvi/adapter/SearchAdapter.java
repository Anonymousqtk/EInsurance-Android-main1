package com.pvi.adapter;

import java.util.ArrayList;
import java.util.List;

import com.pvi.activities.R;
import com.pvi.objects.BeforeSurveyOfflineObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SearchAdapter extends BaseAdapter {
	Activity context=null;
	List<BeforeSurveyOfflineObject> dataArray;
	int layoutId;
	public SearchAdapter (Activity context,int layoutId,List<BeforeSurveyOfflineObject>arr) 
	{
		super();
		this.context=context;
		this.layoutId=layoutId;
		this.dataArray=arr;	
	}
	public SearchAdapter (Activity context,int layoutId)
	{
		super();
		this.context=context;
		this.layoutId=layoutId;
		dataArray=new ArrayList<BeforeSurveyOfflineObject>();
				
	}
	
	public void addItem(BeforeSurveyOfflineObject item) {
		dataArray.add(item);
        notifyDataSetChanged();

    }

    public int getCount() {
        return dataArray.size();
    }

    public BeforeSurveyOfflineObject getItem(int position) {
        return dataArray.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    @SuppressLint("ViewHolder")
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater=context.getLayoutInflater();
			convertView=inflater.inflate(layoutId, null);	                     
            holder.textView =(TextView)convertView.findViewById(R.id.edt_searchname);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String str =dataArray.get(position).getLicensePlate();
        holder.textView.setText(str);
        return convertView;
    }

    public class ViewHolder {
        public TextView textView;
    }

}
