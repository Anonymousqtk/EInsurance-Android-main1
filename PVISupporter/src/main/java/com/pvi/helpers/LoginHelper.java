package com.pvi.helpers;

import java.io.IOException;
import java.net.Proxy;
import java.net.SocketTimeoutException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.util.Log;

public class LoginHelper extends AbstractProgressableAsyncTask<SoapObject, String> {

	private static final String TAG = LoginHelper.class.getName();
	private static final String WS_METHOD_NAME = "loginGDDK";

	public LoginHelper() {}

	public static SoapObject createRequest(String userName, String password, String checkUserType) {
		SoapObject request = new SoapObject(WS_NAMESPACE, WS_METHOD_NAME);

		PropertyInfo property = new PropertyInfo();
		property.setNamespace(WS_NAMESPACE); // to ensure that the element-name is prefixed with the namespace
		request.addProperty("username", userName);
		request.addProperty("password", password);
		request.addProperty("checkTypeUser", checkUserType);

		return request;
	}

	@Override
	protected String performTaskInBackground(SoapObject parameter) throws Exception {
		// 1. Create SOAP Envelope using the request
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.implicitTypes = true;
		envelope.setAddAdornments(false);
		envelope.setOutputSoapObject(parameter);

		// 2. Create a HTTP Transport object to send the web service request
		HttpTransportSE httpTransport = new HttpTransportSE(Proxy.NO_PROXY, WSDL_URL, TIME_OUT);
		httpTransport.debug = this.isAllowedLogcat; // allows capture of raw request/response in Logcat
		httpTransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"utf-8\"?>");

		// 3. Make the web service invocation
		String result = null;
		try {
			httpTransport.call(WS_NAMESPACE + WS_METHOD_NAME, envelope, headerList);
			Log.d(TAG, "HTTP REQUEST:\n" + httpTransport.requestDump);
			Log.d(TAG, "HTTP RESPONSE:\n" + httpTransport.responseDump);

			SoapPrimitive resultsString = (SoapPrimitive) envelope.getResponse();
			result = resultsString.toString();

		} catch (SocketTimeoutException t) {
			t.printStackTrace();
		} catch (IOException i) {
			i.printStackTrace();
		} catch (Exception q) {
			q.printStackTrace();
		}

		return result;
	}

}
