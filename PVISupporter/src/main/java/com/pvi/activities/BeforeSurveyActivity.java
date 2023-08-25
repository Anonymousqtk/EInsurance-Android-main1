package com.pvi.activities;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import com.pvi.helpers.FileHelper;
import com.pvi.helpers.GlobalData;
import com.pvi.helpers.GlobalMethod;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeforeSurveyActivity extends Activity implements OnItemSelectedListener {

    private String[] majorData = {"GIÁM ĐỊNH ĐIỀU KIỆN XE ÔTÔ", "GIÁM ĐỊNH ĐIỀU KIỆN TÀU", "GIÁM ĐỊNH ĐIỀU KIỆN XE MÁY"};
    private Spinner majorSpiner;
    private int majorIndex;
    private static final String STAFF = "cb";
    private boolean isStaff = false;
    //public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
//    String[] permissions= new String[]{
//            WRITE_EXTERNAL_STORAGE,
//            READ_MEDIA_IMAGES,
//            READ_EXTERNAL_STORAGE,
//            CAMERA,
//            Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.ACCESS_FINE_LOCATION};

    // Progress Dialog
    private ProgressDialog pDialog;
    // Progress dialog type (0 - for Horizontal progress bar)
    public static final int progress_bar_type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.before_survey_activity);

        // spinner
        majorSpiner = (Spinner) findViewById(R.id.majorSpinner);
        majorSpiner.setAdapter(new CarAdapter(this, R.layout.spinner_row, majorData));
        majorSpiner.setOnItemSelectedListener(this);
        if (GlobalData.getInstance().getTypeUser().equals(STAFF)) {
            majorSpiner.setSelection(GlobalMethod.getMajorType(BeforeSurveyActivity.this, MODE_PRIVATE));
            isStaff = true;
        } else {
            majorSpiner.setSelection(0);
            GlobalMethod.saveMajorType(GlobalMethod.CAR_MAJOR, MODE_PRIVATE, BeforeSurveyActivity.this);
            isStaff = false;
        }
        // version label
        TextView version = (TextView) findViewById(R.id.tv_version);
        try {
            String currentVersion = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
            version.setText("E-Insurance version " + currentVersion + " - Copyright © 2016 by PVI");
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            version.setText("E-Insurance version 0.0 - Copyright © 2016 by PVI");
        }
        // major index
        majorIndex = GlobalMethod.getMajorType(BeforeSurveyActivity.this, MODE_PRIVATE);
    }
    @Override
    protected void onStart() {
        super.onStart();
        Button createButton = (Button) findViewById(R.id.btn_create_survey);
        createButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (majorIndex == GlobalMethod.CAR_MAJOR) {
                    Intent intent = new Intent(getApplicationContext(), BeforeSurveyCreateActivity.class);
                    startActivity(intent);
                } else if (majorIndex == GlobalMethod.MOTO_MAJOR) {
                    Intent intent = new Intent(getApplicationContext(), BeforeSurveyCreateActivity.class);
                    startActivity(intent);
                } else if (majorIndex == GlobalMethod.SHIP_MAJOR) {
                    showSelectAlert();
                }

            }
        });

        Button listButton = (Button) findViewById(R.id.btn_list_survey);
        listButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), BeforeSurveyListActivity.class);
                    startActivity(intent);
            }
        });

        Button searchButton = (Button) findViewById(R.id.btn_search_survey);
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), BeforeSurveySearchActivity.class);
                    startActivity(intent);
            }
        });

        Button logout = (Button) findViewById(R.id.btn_logout_survey);
        logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();

        if (GlobalData.getInstance().getUsername() == null) {
            this.finish();
        }

		/* check should update new version */
        if (GlobalMethod.needUpdateNewVersion(getApplicationContext(), GlobalData.getInstance().getVersion())) {
            this.showUpdateAlert(GlobalData.getInstance().getUrl());
            return;
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        // TODO Auto-generated method stub
        GlobalMethod.saveMajorType(position, MODE_PRIVATE, BeforeSurveyActivity.this);
        majorIndex = position;

        if (!isStaff && position == GlobalMethod.SHIP_MAJOR) {
            GlobalMethod.saveMajorType(GlobalMethod.CAR_MAJOR, MODE_PRIVATE, BeforeSurveyActivity.this);
            majorIndex = 0;
            majorSpiner.setSelection(0);
            showToast("Chỉ cán bộ của PVI mới được tạo hồ sơ giám định tàu!");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    public void showSelectAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BeforeSurveyActivity.this);
        alertDialog.setTitle("PVI E-Insurance");
        alertDialog.setMessage("Bạn muốn tạo hồ sơ giám định cho tàu đã có trong danh mục tàu?");
        // On pressing Settings button
        alertDialog.setPositiveButton("Đã có", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), BeforeShipSurveyActivity.class);
                startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Chưa có", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), BeforeSurveyOfflineList.class);
                startActivity(intent);
            }
        });

        alertDialog.setCancelable(false);
        // Showing Alert Message
        alertDialog.show();
    }

    private void showUpdateAlert(final String fileURL) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BeforeSurveyActivity.this);
        TextView title = new TextView(getApplicationContext());
        title.setText("PVI E-Insurance");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.RED);
        title.setTextSize(20);

        // Setting Dialog Title
        alertDialog.setCustomTitle(title);
        // Setting Dialog Message
        alertDialog.setMessage("Đã có phiên bản mới \nVui lòng cập nhật phiên bản mới để tiếp tục sử dụng ứng dụng");

        // on pressing update button
        alertDialog.setNegativeButton("Cập nhật", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                new DownloadFileFromURL().execute(fileURL);
            }
        });

        alertDialog.setCancelable(false);
        // Showing Alert Message
        alertDialog.show();
    }

    /**
     * Showing Dialog
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type:
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Đang tải ứng dụng. Vui lòng chờ...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(false);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    /**
     * Background Async Task to download file
     **/
    private class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         **/
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         **/
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);// 1024*8 = 8KB

                // Output stream to write file
                OutputStream output = new FileOutputStream(FileHelper.getDownloadPath());

                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            return null;
        }

        /**
         * Updating progress bar
         **/
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task Dismiss the progress dialog
         ***/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            dismissDialog(progress_bar_type);
            GlobalMethod.installNewUpdate(BeforeSurveyActivity.this, GlobalData.getInstance().getCurrentDownloadPath());

        }

    }

    private class CarAdapter extends ArrayAdapter<String> {
        private String[] data;

        public CarAdapter(Context context, int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);
            data = objects;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_row, parent, false);
            TextView label = (TextView) row.findViewById(R.id.itemType);
            label.setGravity(Gravity.CENTER);
            label.setText(data[position]);
            return row;
        }
    }
}
