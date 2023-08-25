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

import com.pvi.objects.BeforSurveyObject;

import android.util.Log;

public class SaveResumeHelper extends AbstractProgressableAsyncTask<SoapObject, String> {

	private static final String TAG = SaveResumeHelper.class.getName();
	private static final String WS_METHOD_NAME = "saveHSGD_dk";
	
	public SaveResumeHelper() {}

	public static SoapObject createRequest(BeforSurveyObject resume, int isNew, int majorType) {

		String typeVerhice = "AUTO";
		if (majorType == GlobalMethod.MOTO_MAJOR){
			typeVerhice = "MOTO";
		}
		SoapObject request = new SoapObject(WS_NAMESPACE, WS_METHOD_NAME);

		PropertyInfo property = new PropertyInfo();
		property.setNamespace(WS_NAMESPACE); // to ensure that the element-name is prefixed with the namespace
		request.addProperty("isAddNew", isNew + "");
		request.addProperty("prKeyAvailable", resume.getObjectID());
		request.addProperty("ma_donvi", GlobalData.getInstance().getUnitCode());
		request.addProperty("so_seri", resume.getSerialNumber());
		request.addProperty("bien_ksoat", resume.getLicensePlate());
		request.addProperty("so_khung", resume.getVehicleNumber());
		request.addProperty("ma_user", GlobalData.getInstance().getUsername());
		request.addProperty("so_donbh", resume.getPolicyInsurance());
		request.addProperty("ma_ctu", typeVerhice);

		return request;
	}

	@Override
	protected String performTaskInBackground(SoapObject parameter) throws Exception {
		String values = null;
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
		try {
			httpTransport.call(WS_NAMESPACE + WS_METHOD_NAME, envelope, headerList);

			Log.d(TAG, "HTTP REQUEST:\n" + httpTransport.requestDump);
			Log.d(TAG, "HTTP RESPONSE:\n" + httpTransport.responseDump);
			SoapPrimitive resultsString = (SoapPrimitive) envelope.getResponse();
			values = resultsString.toString();

		} catch (SocketTimeoutException t) {
			t.printStackTrace();
		} catch (IOException i) {
			i.printStackTrace();
		} catch (Exception q) {
			q.printStackTrace();
		}

		return values;
	}

}
