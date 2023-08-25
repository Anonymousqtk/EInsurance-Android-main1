package com.pvi.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.net.ParseException;

public class TimerCounterHelper {
	private static final int timeDelay = 1000;
	private static final String dateFormat = "dd/MM/yyyy HH:mm:ss";
	private SimpleDateFormat simpleDateFormat = null;
	private CountingTask task = new CountingTask();
	private boolean isContinued = true;

	@SuppressLint("SimpleDateFormat")
	public TimerCounterHelper() {
		simpleDateFormat = new SimpleDateFormat(dateFormat);
		Thread thread = new Thread(task);
		thread.start();
	}

	public void timeIncrement(String s) throws java.text.ParseException {
		try {
			long input = simpleDateFormat.parse(s).getTime();
			task.increaseTime(input);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public String getCurrentTime() {
		return simpleDateFormat.format(new Date(task.getTime()));
	}
	
	public void interrupTime() {
		isContinued = false;
	}

	private class CountingTask implements Runnable {
		volatile long timeLong = 0L;

		@Override
		public void run() {
			while (isContinued) {

				if (timeLong != 0) {
					timeLong = timeLong + timeDelay;
				}
				try {
					Thread.sleep(timeDelay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		private long getTime() {
			return timeLong;
		}

		private void increaseTime(long timeLong) {
			this.timeLong = timeLong;
		}

	}
}
