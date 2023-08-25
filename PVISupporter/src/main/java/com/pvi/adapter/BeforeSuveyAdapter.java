package com.pvi.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pvi.activities.BeforeSurveyCreateActivity;
import com.pvi.activities.BeforeSurveyDetailActivity;
import com.pvi.activities.BeforeSurveyListActivity;
import com.pvi.activities.R;
import com.pvi.helpers.DatabaseHelper;
import com.pvi.helpers.GlobalMethod;
import com.pvi.objects.BeforSurveyObject;

import static android.content.Context.MODE_PRIVATE;

public class BeforeSuveyAdapter extends ArrayAdapter<BeforSurveyObject> {

	Activity context = null;
	List<BeforSurveyObject> dataArray = null;
	int layoutId;
	DatabaseHelper writeHelper;
	private int majorType;

	public BeforeSuveyAdapter(Activity context, int resource, List<BeforSurveyObject> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.layoutId = resource;
		this.dataArray = objects;
		writeHelper = DatabaseHelper.getWritableInstance(context);
		majorType = GlobalMethod.getMajorType(context, MODE_PRIVATE);
	}

	@SuppressLint("ViewHolder")
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		convertView = inflater.inflate(layoutId, null);

		if (dataArray.size() > 0 && position >= 0) {
			final TextView tvLicensePlate = (TextView) convertView
					.findViewById(R.id.tvLicensePlate);
			final TextView tvSerialNumber = (TextView) convertView
					.findViewById(R.id.tvSerialNumber);
			final TextView tvCustomerName = (TextView) convertView
					.findViewById(R.id.tvCustomerName);
			final TextView tvVehicleNumber = (TextView) convertView
					.findViewById(R.id.tvVehicleNumber);
			final Button deleteButton = (Button) convertView
					.findViewById(R.id.deleteResumeButton);
			final Button editButton = (Button) convertView
					.findViewById(R.id.editResumeButton);
			final RelativeLayout before_survey_cell = (RelativeLayout) convertView
					.findViewById(R.id.before_survey_cell);

			final TextView tvPolicyInsurance = (TextView) convertView
					.findViewById(R.id.tvPolicyInsurance);

			final BeforSurveyObject resume = dataArray.get(position);

			tvLicensePlate.setText(resume.getLicensePlate());
			tvSerialNumber.setText(resume.getSerialNumber());
			tvCustomerName.setText(resume.getCustomerName());
			tvVehicleNumber.setText(resume.getVehicleNumber());
			tvPolicyInsurance.setText(resume.getPolicyInsurance());

			deleteButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					showSettingsAlert(resume, position,
							"Bạn muốn xóa hồ sơ này?\nToàn bộ ảnh và thông tin về hồ sơ này sẽ bị xóa!");

				}
			});
			
			editButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// call BeforeSurveyCreateActivity
					Intent intent = new Intent(context,
							BeforeSurveyCreateActivity.class);
					intent.putExtra("BeforSurveyObject", resume);
					intent.putExtra("NEW_RESUME", false);
					context.startActivity(intent);
				}
			});
			
			before_survey_cell.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// call BeforeSurveyDetailActivity
					Intent intent = new Intent(context,
							BeforeSurveyDetailActivity.class);
					intent.putExtra("BeforSurveyObject", resume);
					context.startActivity(intent);

				}
			});
		}
		return convertView;
	}
	
	public void showSettingsAlert(final BeforSurveyObject resume,
			final int position, String mAlert) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.context);

		// Setting Dialog Title
		alertDialog.setTitle("PVI E-Insurance");

		// Setting Dialog Message
		alertDialog.setMessage(mAlert);

		// On pressing Settings button
		alertDialog.setPositiveButton("Hủy",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		// on pressing cancel button
		alertDialog.setNegativeButton("Đồng ý",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						writeHelper.open();
						writeHelper.deleteResume(resume, majorType);
						writeHelper.close();
						dataArray.remove(position);
						notifyDataSetChanged();

					}
				});

		alertDialog.setCancelable(false);
		// Showing Alert Message
		alertDialog.show();
	}

}
