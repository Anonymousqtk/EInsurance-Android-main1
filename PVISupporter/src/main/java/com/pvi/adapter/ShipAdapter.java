package com.pvi.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pvi.activities.BeforeSurveyDetailActivity;
import com.pvi.activities.R;
import com.pvi.helpers.AsyncTaskManager;
import com.pvi.helpers.DatabaseHelper;
import com.pvi.helpers.GlobalData;
import com.pvi.helpers.GlobalMethod;
import com.pvi.helpers.OnAsyncTaskCompleteListener;
import com.pvi.helpers.SaveShipHelper;
import com.pvi.objects.PVIShipObject;

import java.util.List;

/**
 * Created by tuyenpt on 14/04/2016.
 */
public class ShipAdapter extends ArrayAdapter<PVIShipObject> {
    private Activity context;
    private List<PVIShipObject> dataSource;
    private int resourceID;
    private static final int FIRST_REQUEST = 0;
    public AsyncTaskManager taskManager;
    public boolean isFromListview;
    private DatabaseHelper writeHelper;

    /**
     * Constructor
     *
     * @param context            The current context.
     * @param textViewResourceId The resource ID for a layout file containing a TextView to use when
     *                           instantiating views.
     * @param objects            The objects to represent in the ListView.
     */
    public ShipAdapter(Activity context, int textViewResourceId, List<PVIShipObject> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.dataSource = objects;
        this.resourceID = textViewResourceId;
        writeHelper = DatabaseHelper.getWritableInstance(context);

    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        convertView = inflater.inflate(resourceID, null);

        if(this.dataSource.size() > 0 && position >= 0) {
            TextView shipName = (TextView) convertView.findViewById(R.id.shipName);
            TextView shipCode = (TextView) convertView.findViewById(R.id.shipCode);
            TextView firstDayRegistration = (TextView) convertView.findViewById(R.id.firstDayRegistration);
            TextView lastDayRegistration = (TextView) convertView.findViewById(R.id.lastDayRegistration);
            TextView registrationInfo = (TextView) convertView.findViewById(R.id.registrationInfo);
            LinearLayout cell = (LinearLayout)convertView.findViewById(R.id.shipCreateCell);

            final PVIShipObject object = this.dataSource.get(position);
            shipName.setText(object.getShipName());
            shipCode.setText(object.getShipCode());
            if (!isFromListview) {
                firstDayRegistration.setText(object.getStartDay());
                lastDayRegistration.setText(object.getEndDay());
            } else {
                TextView firstDayRegistrationText = (TextView) convertView.findViewById(R.id.firstDayRegistrationText);
                TextView lastDayRegistrationText = (TextView) convertView.findViewById(R.id.lastDayRegistrationText);
                firstDayRegistration.setVisibility(View.GONE);
                lastDayRegistration.setVisibility(View.GONE);
                firstDayRegistrationText.setVisibility(View.GONE);
                lastDayRegistrationText.setVisibility(View.GONE);
            }

            registrationInfo.setText("Đăng kiểm: " + object.getRegistration());

            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isFromListview) {
                        saveResume(object, FIRST_REQUEST, -1);
                    } else {
                        Intent intent = new Intent(context, BeforeSurveyDetailActivity.class);
                        intent.putExtra("BeforSurveyObject", object);
                        context.startActivity(intent);
                    }

                }
            });

            cell.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (isFromListview) {
                        showSettingsAlert("Xóa hồ sơ giám định tàu\nToàn bộ thông tin và ảnh của hồ sơ sẽ bị xóa trên thiết bị này. Bạn thật sự muốn xóa?", object, position);
                    }
                    return false;
                }
            });
        }
        return convertView;
    }

    private void saveResume(final PVIShipObject object, int isFirstRequest, final int position) {
        SaveShipHelper task = new SaveShipHelper();
        taskManager.executeTask(task, SaveShipHelper.createRequest(object.getShipCode(), isFirstRequest), context.getString(R.string.claim_save_resume),
                new OnAsyncTaskCompleteListener<String>() {
                    @Override
                    public void onTaskCompleteSuccess(String result) {
                        int value = Integer.parseInt(result);
                        if (value == -1) {
                            showToast("Lưu hồ sơ thất bại");
                        } else if (value == -2) {
                            showSettingsAlert("Đã tồn tại hồ sơ giám định cho tàu có số đăng ký " + object.getShipCode() + "\nTiếp tục lưu hồ sơ giám định cho tàu này?", object, position);
                        } else {
                            object.setObjectID(value + "");
                            object.setUserName(GlobalData.getInstance().getUsername());
                            writeHelper.open();
                            writeHelper.insertShipResume(object);
                            writeHelper.close();
                            showToast("Lưu hồ sơ giám định thành công!");
                            // goto Capture
                            Intent intent = new Intent(context, BeforeSurveyDetailActivity.class);
                            intent.putExtra("BeforSurveyObject", object);
                            context.startActivity(intent);
                        }
                    }

                    @Override
                    public void onTaskFailed(Exception cause) {
                        showToast("Lưu hồ sơ lỗi. Hãy thử lại!");
                    }
                });
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }
    private void showSettingsAlert(String mAlert, final PVIShipObject object, final int position) {
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
                        if (position == -1) {
                            saveResume(object, FIRST_REQUEST + 1, position);
                        } else {
                            writeHelper.open();
                            writeHelper.deleteShipResume(object);
                            writeHelper.close();
                            dataSource.remove(position);
                            notifyDataSetChanged();
                        }

                    }
                });
        alertDialog.setCancelable(false);
        // Showing Alert Message
        alertDialog.show();
    }

}
