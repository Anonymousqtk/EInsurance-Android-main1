package com.pvi.helpers;

import java.io.IOException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.util.Log;

import com.google.gson.Gson;
import com.pvi.objects.PVIShipObject;

public class GetShipInfoHelper extends AbstractProgressableAsyncTask<SoapObject, List<PVIShipObject>> {
	private static final String TAG = GetShipInfoHelper.class.getName();
	private static final String WS_METHOD_NAME = "searchShipInfo";

	public GetShipInfoHelper() {}

	public static SoapObject createRequest(String input) {
		SoapObject request = new SoapObject(WS_NAMESPACE, WS_METHOD_NAME);

		PropertyInfo property = new PropertyInfo();
		property.setNamespace(WS_NAMESPACE); // to ensure that the element-name is prefixed with the namespace
		request.addProperty("inputValue", input);
		return request;
	}

	@Override
	protected List<PVIShipObject> performTaskInBackground(SoapObject parameter) throws Exception {
		// TODO Auto-generated method stub
		// 1. Create SOAP Envelope using the request
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.implicitTypes = true;
		envelope.setAddAdornments(false);
		envelope.setOutputSoapObject(parameter);

		// 2. Create a HTTP Transport object to send the web service request
		HttpTransportSE httpTransport = new HttpTransportSE(Proxy.NO_PROXY, WSDL_URL, TIME_OUT);
		httpTransport.debug = this.isAllowedLogcat; // allows capture of raw request/response in Logcat
		httpTransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"utf-8\"?>");

		// 3. Make the web service invocation
		List<PVIShipObject> result = new ArrayList<PVIShipObject>();
		try {
			httpTransport.call(WS_NAMESPACE + WS_METHOD_NAME, envelope, headerList);
			Log.d(TAG, "HTTP REQUEST:\n" + httpTransport.requestDump);
			Log.d(TAG, "HTTP RESPONSE:\n" + httpTransport.responseDump);
			SoapPrimitive resultsString = (SoapPrimitive) envelope.getResponse();
			String jsonString = resultsString.toString();
			JSONObject jsonValue = new JSONObject(jsonString);

			if (!jsonValue.isNull("Consumers")) {
				// Getting "Consumers" element from JSON file
				JSONArray array = jsonValue.getJSONArray("Consumers");
				// Creating empty json object for Consumers loop
				JSONObject currentObject;
				// looping all the resumes and adding them parsed one by one to the list
				for (int i = 0; i < array.length(); i++) {
					currentObject = array.getJSONObject(i);
					// Method to parse resume and add them to the list
					Gson gson = new Gson();
					PVIShipObject resume = gson.fromJson(currentObject.toString(), PVIShipObject.class);
					result.add(resume);
				}
			}
		} catch (SocketTimeoutException t) {
			t.printStackTrace();
		} catch (IOException i) {
			i.printStackTrace();
		} catch (JSONException i) {
			i.printStackTrace();
		} catch (Exception q) {
			q.printStackTrace();
		}

		return result;
	}

}
