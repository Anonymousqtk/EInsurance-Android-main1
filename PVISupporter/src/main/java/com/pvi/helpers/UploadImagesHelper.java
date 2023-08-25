package com.pvi.helpers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Proxy;
import java.net.SocketTimeoutException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;

import com.pvi.objects.BeforeSurveyImage;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

public class UploadImagesHelper extends AbstractProgressableAsyncTask<SoapObject, String> {

	private static final String TAG = UploadImagesHelper.class.getName();
	private static final String WS_METHOD_NAME = "uploadFileGDDK";

	/**
	 * if result = 200, image uploaded successfully for other value, image
	 * uploaded failure
	 */
	public UploadImagesHelper() {}

	public static SoapObject createRequest(BeforeSurveyImage image) {
		SoapObject request = new SoapObject(WS_NAMESPACE, WS_METHOD_NAME);

		String base64 = convertToBase64(FileHelper.loadImageFromPath(image.getFileName()));
		String fileName = FileHelper.getFileName(image.getFileName());

		PropertyInfo property = new PropertyInfo();
		property.setNamespace(WS_NAMESPACE); // to ensure that the element-name is prefixed with the namespace
		request.addProperty("prKey", image.getObjectID());
		request.addProperty("filebinary", base64);
		request.addProperty("filename", fileName);
		request.addProperty("ngay_chup", image.getTakenDay());
		request.addProperty("kinh_do", image.getLongitude());
		request.addProperty("vi_do", image.getLatitude());

		return request;
	}

	private static String convertToBase64(Bitmap bitmap) {
		Bitmap image = bitmap;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
		byte[] byteArray = byteArrayOutputStream.toByteArray();
		
		// add more
		image.recycle();
		image = null;
		
		return Base64.encodeToString(byteArray, Base64.DEFAULT);
	}

	// private static Bitmap decodeBase64(String input) {
	// byte[] decodedByte = Base64.decode(input, 0);
	// return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
	// }

	@Override
	protected String performTaskInBackground(SoapObject parameter) throws Exception {
		// TODO Auto-generated method stub
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
			
		} catch (HttpResponseException c) {
			c.printStackTrace();
			result = Integer.toString(c.getStatusCode());
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
