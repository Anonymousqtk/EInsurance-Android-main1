package com.pvi.helpers;

import android.util.Log;

import com.google.gson.Gson;
import com.pvi.objects.DongXeObject;
import com.pvi.objects.MucDichSdObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tunb on 05/04/2023.
 */

public class GetMucDichSd extends AbstractProgressableAsyncTask<SoapObject, ArrayList<MucDichSdObject>> {
    private static final String TAG = GetMucDichSd.class.getName();
    private static final String WS_METHOD_NAME = "Dm_mucdichsd_new";

    public GetMucDichSd() {}

    public static SoapObject createRequest() {
        SoapObject request = new SoapObject(WS_NAMESPACE, WS_METHOD_NAME);
        PropertyInfo property = new PropertyInfo();
        property.setNamespace(WS_NAMESPACE); // to ensure that the element-name is prefixed with the namespace
        return request;
    }

    @Override
    protected ArrayList<MucDichSdObject> performTaskInBackground(SoapObject parameter) throws Exception {
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
        ArrayList<MucDichSdObject> result = new ArrayList<>();
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
                    MucDichSdObject resume = gson.fromJson(currentObject.toString(), MucDichSdObject.class);
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
