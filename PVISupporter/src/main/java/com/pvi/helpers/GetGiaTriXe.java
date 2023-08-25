package com.pvi.helpers;

import android.util.Log;

import com.google.gson.Gson;
import com.pvi.objects.LoaiXeObject;

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

public class GetGiaTriXe extends AbstractProgressableAsyncTask<SoapObject, String> {
    private static final String TAG = GetGiaTriXe.class.getName();
    private static final String WS_METHOD_NAME = "Get_GiaTriXe_New1";

    public GetGiaTriXe() {}

    public static SoapObject createRequest(String nhan_hieu, String ma_dongxe, String nam_sx, String ma_mdichsd, String ten_loaixe, boolean xemoi, String trong_tai, String so_cho ) {
        SoapObject request = new SoapObject(WS_NAMESPACE, WS_METHOD_NAME);
        PropertyInfo property = new PropertyInfo();
        property.setNamespace(WS_NAMESPACE); // to ensure that the element-name is prefixed with the namespace
        request.addProperty("nhan_hieu", nhan_hieu);
        request.addProperty("ma_dongxe", ma_dongxe);
        request.addProperty("nam_sx", nam_sx);
        request.addProperty("ma_mdichsd", ma_mdichsd);
        request.addProperty("ten_loaixe", ten_loaixe);
        request.addProperty("xemoi", xemoi);
        request.addProperty("trong_tai", trong_tai.equals("") ? "0":trong_tai);
        request.addProperty("so_cho", so_cho.equals("") ? "0":so_cho);
        return request;
    }
//    public static SoapObject createRequest(String nhan_hieu, String ma_dongxe, String nam_sx, String ma_loaixe, boolean xemoi ) {
//        SoapObject request = new SoapObject(WS_NAMESPACE, WS_METHOD_NAME);
//        PropertyInfo property = new PropertyInfo();
//        property.setNamespace(WS_NAMESPACE); // to ensure that the element-name is prefixed with the namespace
//        request.addProperty("nhan_hieu", nhan_hieu);
//        request.addProperty("ma_dongxe", ma_dongxe);
//        request.addProperty("nam_sx", nam_sx);
//        request.addProperty("ma_loaixe", ma_loaixe);
//        request.addProperty("xemoi", xemoi);
//        return request;
//    }


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
        String result = null;
        try {
            httpTransport.call(WS_NAMESPACE + WS_METHOD_NAME, envelope, headerList);
            Log.d(TAG, "HTTP REQUEST:\n" + httpTransport.requestDump);
            Log.d(TAG, "HTTP RESPONSE:\n" + httpTransport.responseDump);
            //SoapObject resultsString= (SoapObject) envelope.getResponse();
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
