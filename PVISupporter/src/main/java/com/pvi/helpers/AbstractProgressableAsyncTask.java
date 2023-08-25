package com.pvi.helpers;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.kobjects.base64.Base64;
import org.ksoap2.HeaderProperty;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractProgressableAsyncTask<P, R> extends AsyncTask<P, Void, R> {

	private final String TAG = getClass().getName();

	private OnAsyncTaskCompleteListener<R> taskCompletionListener;
	private IProgressTracker progressTracker;
	// Most recent exception (used to diagnose failures)
	private Exception mostRecentException;

	protected boolean isAllowedLogcat = true;
	// for test
//	protected static final String WSDL_URL = "http://10.2.4.86:81/Service.asmx";
	// primary - will be changed when deploy
	protected static final String WSDL_URL = "http://apps.pvi24.vn/Gdtt2021/Service.asmx";
	protected static final String WS_NAMESPACE = "http://localhost/gdtt/Service.asmx/";
	protected static final int TIME_OUT = 200000; // 200 seconds

	// SOAP authentication
	protected List<HeaderProperty> headerList = new ArrayList<HeaderProperty>();

	public AbstractProgressableAsyncTask() {
		headerList.add(new HeaderProperty("Authorization", "Basic " + Base64.encode("pvi247:s@1236541".getBytes())));
	}

	public final void setOnTaskCompletionListener(OnAsyncTaskCompleteListener<R> taskCompletionListener) {
		this.taskCompletionListener = taskCompletionListener;
	}

	public final void setProgressTracker(IProgressTracker progressTracker) {
		if (progressTracker != null) {
			this.progressTracker = progressTracker;
		}
	}

	@Override
	protected final void onPreExecute() {
		if (progressTracker != null) {
			this.progressTracker.onStartProgress();
		}
	}

	/**
	 * Invoke the web service request
	 */
	@Override
	protected final R doInBackground(P... parameters) {
		mostRecentException = null;
		R result = null;

		try {
			result = performTaskInBackground(parameters[0]);
		} catch (Exception e) {
			Log.e(TAG, "Failed to invoke the web service: ", e);
			mostRecentException = e;
		}

		return result;
	}

	protected abstract R performTaskInBackground(P parameter) throws Exception;

	/**
	 * @param result
	 *            to be sent back to the observer (typically an {@link Activity}
	 *            running on the UI Thread). This can be <code>null</code> if an
	 *            error occurs while attempting to invoke the web service (e.g.
	 *            web service was unreachable, or network I/O issue etc.)
	 */
	@Override
	protected final void onPostExecute(R result) {
		if (progressTracker != null) {
			progressTracker.onStopProgress();
		}
		if (taskCompletionListener != null) {
			if (result == null || mostRecentException != null) {
				taskCompletionListener.onTaskFailed(mostRecentException);

			} else {
				taskCompletionListener.onTaskCompleteSuccess(result);
			}
		}

		// clean up listeners since we are done with this task
		progressTracker = null;
		taskCompletionListener = null;
	}
}
