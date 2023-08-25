package com.pvi.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import com.pvi.objects.EinsuranceObject;
import com.pvi.activities.R;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class MyArrayAdapter extends ArrayAdapter<EinsuranceObject> {

	Activity context = null;
	ArrayList<EinsuranceObject> myArray = null;
	int layoutId;
	Button changeFee;
	TextView tv_feeStatus;

	private static String SOAP_ACTION = "http://tempuri.org/update_seri_edit";
	private static String NAMESPACE = "http://tempuri.org/";
	private static String METHOD_NAME = "update_seri_edit";
//	private static String URL = "http://103.26.252.13/soapmobiletest/Mobile_soap.asmx";
	private static String URL = "http://103.26.252.13/Soap_mobile/Mobile_soap.asmx";


	public MyArrayAdapter(Activity context, int layoutId, ArrayList<EinsuranceObject> arr) {
		super(context, layoutId, arr);
		this.context = context;
		this.layoutId = layoutId;
		this.myArray = arr;
	}

	@SuppressLint("ViewHolder")
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = context.getLayoutInflater();
		convertView = inflater.inflate(layoutId, null);

		if (myArray.size() > 0 && position >= 0) {

			TextView tv_seri = (TextView) convertView.findViewById(R.id.tv_seri);
			TextView tv_bks = (TextView) convertView.findViewById(R.id.tv_bks);
			TextView tv_lhbh = (TextView) convertView.findViewById(R.id.tv_lhbh);
			TextView tv_date_cre = (TextView) convertView.findViewById(R.id.tv_date_cre);
			TextView tv_dienthoai = (TextView) convertView.findViewById(R.id.tv_dienthoai);
			tv_feeStatus = (TextView) convertView.findViewById(R.id.tv_feeStatus);
			changeFee = (Button)convertView.findViewById(R.id.btn_getMonney);

			// lấy ra nhân viên thứ position
			final EinsuranceObject seri_bks_ = myArray.get(position);
			// đưa thông tin lên TextView
			tv_seri.setText(seri_bks_.getseri());
			tv_bks.setText(seri_bks_.getbks());
			tv_lhbh.setText(seri_bks_.getlhbh());
			tv_date_cre.setText(seri_bks_.getdate());
			tv_dienthoai.setText(seri_bks_.getphone());

			String feeString = "";
			if (seri_bks_.getFeeStatus() != null) {
				if (seri_bks_.getFeeStatus().equals("C")) {
					feeString = "Đã thu phí";
				} else if (seri_bks_.getFeeStatus().equals("K")) {
					feeString = "Chưa thu phí";
					changeFee.setVisibility(View.VISIBLE);
				} else if (seri_bks_.getFeeStatus().equals("HD")) {
					feeString = "Theo hợp đồng";
					changeFee.setVisibility(View.VISIBLE);
				}
			}
			tv_feeStatus.setText(feeString);
			changeFee.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					save_anchi(seri_bks_);
				}
			});
		}

		return convertView;
	}

	public void save_anchi(EinsuranceObject seri_bks_) {

		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		request.addProperty("seri", seri_bks_.getseri());
		request.addProperty("bks_sk", seri_bks_.getbks());
		request.addProperty("loai_hinhbh", seri_bks_.getlhbh());
		request.addProperty("id_tr", seri_bks_.getid());
		request.addProperty("feeStatus", "C");

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;
		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 1000 * 60 * 2);
			androidHttpTransport.call(SOAP_ACTION, envelope);

			SoapObject result = (SoapObject) envelope.bodyIn;
			if (result.getPropertyCount() > 0) {
				String res = result.getProperty(0).toString();
				if (res.equals("1")) {
					showToast("Đã hết thời gian chỉnh sửa");
				} else if (res.equals("2")) {
					showToast("Sửa phát sinh ấn chỉ không thành công");
				} else if (res.equals("3")) {
					showToast("Chưa nhập chỉnh sửa phát sinh");
				} else {
					showToast("Sửa phát sinh ấn chỉ thành công");
					changeFee.setVisibility(View.GONE);
					tv_feeStatus.setText("Đã thu phí");
					seri_bks_.setFeeStatus("C");
					this.notifyDataSetChanged();

				}
			} else {
				showToast("Có lỗi trong quá trình nhập");
			}

		} catch (Exception ex) {
			showToast("Có lỗi trong quá trình nhập");
		}


	}
	private void showToast(String message) {
		Toast toast = Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();
	}

}
