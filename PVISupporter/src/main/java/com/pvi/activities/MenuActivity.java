package com.pvi.activities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.pvi.adapter.MenuAdapter;
import com.pvi.adapter.MenuItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MenuActivity extends Activity {
    private static final String PVI_AUTOCARE = "pvi_autocare";
    private static final String OLD_DAY_SAVED = "old_day_saved";
    private static final int SESSION_DAYS = 30; // days: time to re-login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ArrayList<MenuItem> gridArray = new ArrayList<MenuItem>();

        gridArray.add(new MenuItem(BitmapFactory.decodeResource(this.getResources(), R.drawable.license_manager), "Quản lý cấp đơn"));
        gridArray.add(new MenuItem(BitmapFactory.decodeResource(this.getResources(), R.drawable.survey), "Giám định điều kiện"));
        gridArray.add(new MenuItem(BitmapFactory.decodeResource(this.getResources(), R.drawable.einsurance), "Báo phát sinh ấn chỉ"));
        gridArray.add(new MenuItem(BitmapFactory.decodeResource(this.getResources(), R.drawable.restriction_shield), "Tra cứu blacklist xe ôtô"));
        gridArray.add(new MenuItem(BitmapFactory.decodeResource(this.getResources(), R.drawable.tracuu), "Tra cứu lịch sử tổn thất"));
        gridArray.add(new MenuItem(BitmapFactory.decodeResource(this.getResources(), R.drawable.tra_cuu_gtrixe), "Tra cứu giá trị xe"));
//		gridArray.add(new MenuItem(BitmapFactory.decodeResource(
//				this.getResources(), R.drawable.claim), "Giám định trực tuyến"));
//		gridArray.add(new MenuItem(BitmapFactory.decodeResource(
//				this.getResources(), R.drawable.tracuu), "Tra cứu bồi thường"));
//		gridArray.add(new MenuItem(BitmapFactory.decodeResource(
//				this.getResources(), R.drawable.license_manager), "Quản lý ấn chỉ"));

        GridView gridView = (GridView) findViewById(R.id.gridViewMenu);
        MenuAdapter customItem = new MenuAdapter(this, R.layout.menu_row_item, gridArray);

        // Instance of ImageAdapter Class
        gridView.setAdapter(customItem);

        /**
         * On Click event for Single Gridview Item
         * */
        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (position >= 6) {
                    showAlertWithMessage("Chức năng đang được tiếp tục hoàn thiện");
                    return;
                }
                if (position == 0) {
                    Intent i = new Intent(getApplicationContext(), TradeActivity.class);
                    i.putExtra("trade", "http://qlcd.pvi.com.vn");
                    startActivity(i);
                } else if (position == 1) {
                    Intent i = new Intent(getApplicationContext(), BeforeSurveyLoginActivity.class);
                    startActivity(i);
                } else if (position == 2) {
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                } else if (position == 3) {
                    Intent i = new Intent(getApplicationContext(), RestrictionList.class);
                    startActivity(i);
                } else if (position == 4) {
                    Intent i = new Intent(getApplicationContext(), LossHistoryActivity.class);
                    startActivity(i);
                }
                else if (position == 5) {
                    Intent i = new Intent(getApplicationContext(), TraCuuGiaTriXeActivity.class);
                    startActivity(i);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // get old day saved if availables
        SharedPreferences pre = getSharedPreferences(PVI_AUTOCARE, MODE_PRIVATE);
        String oldDay = pre.getString(OLD_DAY_SAVED, null);
        if (oldDay == null || (getDateDiff(oldDay, TimeUnit.DAYS) >= SESSION_DAYS)) {
            // go to login page
//			Intent i = new Intent(getApplicationContext(), LoginActivity.class);
//			startActivity(i);
//			finish();
        }
    }

    // get number of days from old day to current day
    public static long getDateDiff(String oldDate, TimeUnit timeUnit) {
        if (oldDate == null) {
            return SESSION_DAYS;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        Date _oldDate = null;
        try {
            _oldDate = sdf.parse(oldDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        long diffInMillies = (new Date()).getTime() - _oldDate.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    private void showAlertWithMessage(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        TextView title = new TextView(this);
        title.setText("PVI E-Insurance");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.RED);
        title.setTextSize(20);
        // Setting Dialog Title
        alertDialog.setCustomTitle(title);
        // Setting Dialog Message
        alertDialog.setMessage(message);
        // on pressing cancel button
        alertDialog.setNegativeButton("Thử lại", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.setCancelable(false);
        // Showing Alert Message
        alertDialog.show();
    }

}
