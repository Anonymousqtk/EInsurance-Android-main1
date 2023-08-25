package com.pvi.activities;

import com.pvi.helpers.TimeService;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by tuyenpham on 5/9/16.
 */
public class BaseActivity extends Activity {
    protected String currentTime;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action != null && action.equals(TimeService.ACTION_UPDATE_TIME)) {
                currentTime = intent.getStringExtra(TimeService.RECEIVER_FLAG);
            }
        }
    };

    private IntentFilter mFilter = null;

    @Override
    protected void onResume() {
        super.onResume();
        if(mFilter == null) {
            mFilter = new IntentFilter();
            mFilter.addAction(TimeService.ACTION_UPDATE_TIME);
        }
        registerReceiver(mReceiver, mFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(mReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Toast.makeText(getApplicationContext(), "un-registered", Toast.LENGTH_LONG).show();
    }
}
