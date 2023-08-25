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
import android.widget.TableLayout;
import android.widget.TextView;

import com.pvi.activities.R;
import com.pvi.activities.BeforeSurveyImageOffline;
import com.pvi.helpers.DatabaseHelper;
import com.pvi.helpers.GlobalMethod;
import com.pvi.objects.BeforeSurveyOfflineObject;

public class BeforeSurveyOfflineAdapter extends
        ArrayAdapter<BeforeSurveyOfflineObject> {
    private Activity context;
    private List<BeforeSurveyOfflineObject> dataArray;
    private int layoutId;
    private DatabaseHelper writeHelper;
    public int majorType;

    public BeforeSurveyOfflineAdapter(Activity context, int layoutId,
                                      List<BeforeSurveyOfflineObject> arr) {
        super(context, layoutId, arr);
        this.context = context;
        this.layoutId = layoutId;
        this.dataArray = arr;
        writeHelper = DatabaseHelper.getWritableInstance(context);
    }

    @SuppressLint("ViewHolder")
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        convertView = inflater.inflate(layoutId, null);

        if (dataArray.size() > 0 && position >= 0) {
            final TextView tvc12 = (TextView) convertView.findViewById(R.id.tvbks_list_offline);
            final TextView tvc22 = (TextView) convertView.findViewById(R.id.tvseri_list_offline);
            final Button btnchon = (Button) convertView.findViewById(R.id.btndel_list_offline);
            final BeforeSurveyOfflineObject resume = dataArray.get(position);

            final TableLayout tbChoice = (TableLayout) convertView.findViewById(R.id.tb_list_offline);
            tvc12.setText((majorType == GlobalMethod.CAR_MAJOR) ? "BKS/SK: " + resume.getLicensePlate() : "Số ĐK tàu: " + resume.getLicensePlate());
            tvc22.setText((majorType == GlobalMethod.CAR_MAJOR) ? "Serial: " + resume.getSerialNumber() : "Tên tàu: " + resume.getSerialNumber());
            btnchon.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    showSettingsAlert(resume, position, "Bạn có muốn xóa tất cả thông tin và ảnh của hồ sơ offline này không?");
                }
            });

            tbChoice.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent(context, BeforeSurveyImageOffline.class);
                    intent.putExtra("BeforeSurveyOfflineObject", resume);
                    context.startActivity(intent);
                }
            });
        }
        return convertView;
    }

    public void showSettingsAlert(final BeforeSurveyOfflineObject Resume, final int position, String mAlert) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.context);

        // Setting Dialog Title
        alertDialog.setTitle("PVI E-Insurance");

        // Setting Dialog Message
        alertDialog.setMessage(mAlert);

        // On pressing Settings button
        alertDialog.setPositiveButton("Hủy", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Đồng ý", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                writeHelper.open();
                writeHelper.deleteOfflineResumeWithKey(Resume.getPrimaryKey());
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
