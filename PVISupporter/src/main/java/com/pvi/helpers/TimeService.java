package com.pvi.helpers;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class TimeService extends Service {
    public static final String ACTION_UPDATE_TIME = "update_time";
    public static final String RECEIVER_FLAG = "receiver_flag";

    private static final String INIT_TIME = "initialize";
    private static final String LONG_ALIVE = "long_alive";

    private static final int MSG_UPDATE = 1;
    private static final int TIME_UPDATE = 3000;
    private static final int MAX_TIME_CYCLE = 6000;
    private IncomingMessageHandler mHandler = new IncomingMessageHandler();
    private String currentTime;
    private long currentTimeLong = 0;
    private long deltaTime = 0, previousTime = 0, totalDeltaTime = 0;
    private static final String dateFormat = "dd/MM/yyyy HH:mm:ss";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler.sendEmptyMessageAtTime(MSG_UPDATE, TIME_UPDATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO do something useful
        if (intent != null) {
            String msg = intent.getStringExtra(RECEIVER_FLAG);
            currentTimeLong = getTimeLong(msg);
            if (currentTimeLong != Long.parseLong(getInitTime())) {
                saveInitTime(currentTimeLong + "");
                saveDeltaTime("0");
            } else {
                currentTimeLong = currentTimeLong + Long.parseLong(getDeltaTime());
            }
        } else {
            currentTimeLong = Long.parseLong(getInitTime()) + Long.parseLong(getDeltaTime());
        }

        previousTime = System.currentTimeMillis();
        return START_STICKY; // Run until explicitly stopped

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private long getTimeLong(String _time) {
        try {
            return simpleDateFormat.parse(_time).getTime();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            // stop service if time string invalid
            Toast.makeText(getApplicationContext(), "Định dạng thời gian sai! Tiến trình bị ngắt", Toast.LENGTH_LONG).show();
            stopSelf();
        }
        return 0;
    }

    private String getTimeString(long time) {
        return simpleDateFormat.format(new Date(time));
    }

    private void saveDeltaTime(String deltaTime) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LONG_ALIVE, deltaTime);
        editor.commit();
    }

    private String getDeltaTime() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString(LONG_ALIVE, "0");
    }

    private void saveInitTime(String initTime) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(INIT_TIME, initTime);
        editor.commit();
    }

    private String getInitTime() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString(INIT_TIME, "0");
    }

    @SuppressLint("HandlerLeak")
	private class IncomingMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE:
                    deltaTime = System.currentTimeMillis() - ((previousTime == 0) ? System.currentTimeMillis() : previousTime);
                    /* stop service if delta time is incorrect */
                    if (deltaTime < 0 || deltaTime > MAX_TIME_CYCLE) {
                        stopSelf();
                    }

                    previousTime = System.currentTimeMillis();
                    currentTimeLong = currentTimeLong + deltaTime;
                    currentTime = getTimeString(currentTimeLong);

                    totalDeltaTime = Long.parseLong(getDeltaTime());
                    totalDeltaTime = totalDeltaTime + deltaTime;
                    saveDeltaTime(totalDeltaTime + "");

                    Intent intent = new Intent();
                    intent.setAction(ACTION_UPDATE_TIME);
                    intent.putExtra(RECEIVER_FLAG, currentTime);
                    getApplicationContext().sendBroadcast(intent);
                    sendEmptyMessageDelayed(MSG_UPDATE, TIME_UPDATE);
                    break;
            }
        }
    }

}
