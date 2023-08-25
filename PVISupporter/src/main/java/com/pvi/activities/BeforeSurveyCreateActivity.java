package com.pvi.activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.zxing.Result;
import com.pvi.helpers.AsyncTaskManager;
import com.pvi.helpers.DatabaseHelper;
import com.pvi.helpers.GlobalData;
import com.pvi.helpers.GlobalMethod;
import com.pvi.helpers.OnAsyncTaskCompleteListener;
import com.pvi.helpers.SaveResumeHelper;
import com.pvi.objects.BeforSurveyObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class BeforeSurveyCreateActivity extends Activity implements ZXingScannerView.ResultHandler {

	private final int IS_ADDNEW = 1;
	private final int UPDATE	= 0;
	private DatabaseHelper writeHelper;
	private AsyncTaskManager taskManager;
	private EditText licensePlate, vehicleNumber, serialNumber, customerName, policyInsurance;
	private boolean isAddnew;
	private BeforSurveyObject currentResume;

    private ZXingScannerView scannerView;
    private boolean isScanning = false;
	private ProgressDialog pDialog;
	private int majorIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.before_survey_create_activity);
		majorIndex = GlobalMethod.getMajorType(BeforeSurveyCreateActivity.this, MODE_PRIVATE);
		this.getFormWidgets();

	}

	@Override
	protected void onStart() {
		super.onStart();

		this.addEvents();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if (GlobalData.getInstance().getUsername() == null) {
			this.finish();
		}
		
		writeHelper = DatabaseHelper.getWritableInstance(this);
		if (taskManager == null) {
			taskManager = new AsyncTaskManager(this);
		}

	}
    @Override
    public void handleResult(Result result) {
        final String stringResult = result.getText();
        ViewGroup vg = (ViewGroup)(scannerView.getParent());
        vg.removeView(scannerView);
        isScanning = false;
        setContentView(R.layout.before_survey_create_activity);
        this.getFormWidgets();
        this.addEvents();
        String[] contents = stringResult.split("\\r?\\n");
        if (contents.length < 14) {
			String urlString = stringResult.replace("\\r?\\n","").trim();
			Uri uri = Uri.parse(urlString);
			String key = uri.getQueryParameter("key");
			if (uri.getQueryParameter("b") != null) {
				key = key + "_" + uri.getQueryParameter("b");
			}
			String md5 = getMD5Hash("1afcb207d3298c07d37cdd59f689a3b3" + key);
			new JsonTask().execute("https://apiqr.pvi.com.vn/ManagerPVI/GetInformationQR/?code=" + key + "&sign=" + md5 + "&cpid=3");
		} else {
            String[] so_donbh = contents[2].toString().split(":");
            String[] bien_ksoat = contents[6].toString().split(":");
            String[] so_khung = contents[13].toString().split(":");
            if (so_donbh.length < 2) {
                showToastMessage("Không đúng định dạng đơn bảo hiểm. Hãy thử lại.");
            } else {
                serialNumber.setText("0");
                policyInsurance.setText(so_donbh[1].toString().trim());
                if (bien_ksoat.length >= 2) {
					licensePlate.setText(bien_ksoat[1].toString().trim());
				}
				if (so_khung.length >= 2) {
					vehicleNumber.setText(so_khung[1].toString().trim());
				}
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isScanning) {
            ViewGroup vg = (ViewGroup)(scannerView.getParent());
            vg.removeView(scannerView);
            isScanning = false;
            setContentView(R.layout.before_survey_create_activity);
            this.getFormWidgets();
            this.addEvents();
        } else {
            super.onBackPressed();
        }
    }
    private void showToastMessage(String _toastMsg) {
        Toast toast = Toast.makeText(getApplicationContext(), _toastMsg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    private void getFormWidgets() {
        licensePlate = (EditText) findViewById(R.id.licensePlate);
        vehicleNumber = (EditText) findViewById(R.id.vehicleNumber);
        serialNumber = (EditText) findViewById(R.id.serialNumber);
        customerName = (EditText) findViewById(R.id.customerName);
        policyInsurance = (EditText) findViewById(R.id.policyInsurance);

        customerName.setVisibility(View.INVISIBLE);

        Intent i = getIntent();
        isAddnew = i.getBooleanExtra("NEW_RESUME", true);
        if (!isAddnew) {
            currentResume = (BeforSurveyObject) i.getSerializableExtra("BeforSurveyObject");
            licensePlate.setText(currentResume.getLicensePlate());
            vehicleNumber.setText(currentResume.getVehicleNumber());
            serialNumber.setText(currentResume.getSerialNumber());
            policyInsurance.setText(currentResume.getPolicyInsurance());
        }
    }
    private void  addEvents() {
        Button saveButton = (Button) findViewById(R.id.btn_save_survey);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if ((licensePlate.getText().toString().length() == 0
                        && vehicleNumber.getText().toString().length() == 0)
                        || (serialNumber.getText().toString().length() == 0 && policyInsurance.getText().toString().length() == 0)) {
                    showToast("Nhập số ấn chỉ/số đơn bảo hiểm và biển kiểm soát hoặc số khung!");
                    return;
                }

                if (serialNumber.getText().toString().length() == 0 && policyInsurance.getText().toString().length() > 0) serialNumber.setText("0");

                if (isAddnew) {
                    BeforSurveyObject object = new BeforSurveyObject(licensePlate.getText().toString(), vehicleNumber.getText().toString(), serialNumber.getText().toString(), customerName.getText().toString(), "", GlobalData.getInstance().getUsername(), policyInsurance.getText().toString());
                    object.setObjectID("0");

                    if (shouldSaveResume(object)) {
                        saveResume(object);
                    } else {
                        showAlertWithMessage("Đã tồn tại hồ sơ có biển kiểm soát và ấn chỉ giống với hồ sơ này\nTiếp tục lưu hồ sơ này?", object);
                    }
                } else {
                    currentResume.setLicensePlate(licensePlate.getText().toString());
                    currentResume.setSerialNumber(serialNumber.getText().toString());
                    currentResume.setVehicleNumber(vehicleNumber.getText().toString());
                    currentResume.setPolicyInsurance(policyInsurance.getText().toString());

                    saveResume(currentResume);
                }
            }
        });

        Button scanQR = (Button) findViewById(R.id.btn_scan_qr);
        scanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scannerView = new ZXingScannerView(BeforeSurveyCreateActivity.this);
                scannerView.setResultHandler(BeforeSurveyCreateActivity.this);
                scannerView.startCamera();
                setContentView(scannerView);
                isScanning = true;
            }
        });
    }

	private void saveResume(final BeforSurveyObject resume) {
		SaveResumeHelper task = new SaveResumeHelper();
		taskManager.executeTask(task, SaveResumeHelper.createRequest(resume, isAddnew ? IS_ADDNEW : UPDATE, majorIndex), getString(R.string.claim_save_resume), new OnAsyncTaskCompleteListener<String>() {
					@Override
					public void onTaskCompleteSuccess(String result) {
						if (result.equals("-1")) {
							showToast("Lưu hồ sơ lỗi");
							return;
						} else if (result.equals("-2")) {
							showToast("Hồ sơ với thông tin ấn chỉ và xe này đã có trên hệ thống. Bạn không thể lưu hồ sơ này!");
							return;
						} else if (result.equals("-3")) {
							showToast("Hồ sơ này đã quá hạn 5 ngày. Không thể thực hiện thao tác này!");
							return;
						} else if (result.equals("-4")) {
							showToast("Xe này có trong danh sách hạn chế khai thác. Không thể thực hiện thao tác này!");
							return;
						}
						try {
							updateLocalDatabase(result, resume);
						} catch (Exception e) {
							showToast("Có lỗi trong quá trình cập nhật dữ liệu!");
						}
					}

					@Override
					public void onTaskFailed(Exception cause) {
						showToast("Lưu hồ sơ lỗi. Hãy thử lại!");
					}
				});

	}

	private void updateLocalDatabase(String oid, BeforSurveyObject resume) {

		if (isAddnew) {
			resume.setObjectID(oid);

			writeHelper.open();
			writeHelper.insertResume(resume, majorIndex);
			writeHelper.close();
			
			showToast("Lưu thành công");
			// call BeforeSurveyDetailActivity
			Activity context = BeforeSurveyCreateActivity.this;
			Intent intent = new Intent(context, BeforeSurveyDetailActivity.class);
			intent.putExtra("BeforSurveyObject", resume);
			context.startActivity(intent);
		} else {
			
			writeHelper.open();
			writeHelper.updateResume(resume);
			writeHelper.close();
			
			showToast("Lưu thành công");
			this.finish();
			return;
		}
	}

	private void showToast(String message) {
		Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();
	}

	private boolean shouldSaveResume(BeforSurveyObject resume) {
		writeHelper.open();
		List<BeforSurveyObject> currentResumes = writeHelper.getAllResumeWithUser(GlobalData.getInstance().getUsername(), majorIndex);
		writeHelper.close();
		boolean isAvailable = false;
		for (BeforSurveyObject beforSurveyObject : currentResumes) {
			if (beforSurveyObject.getSerialNumber().equals(resume.getSerialNumber()) && beforSurveyObject.getLicensePlate().equals(resume.getLicensePlate())) {
				isAvailable = true;
				break;
			}
		}

		return !isAvailable;
	}

	private void showAlertWithMessage(String message,
			final BeforSurveyObject resume) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(BeforeSurveyCreateActivity.this);

		// Setting Dialog Title
		alertDialog.setTitle("PVI eInsurance");

		// Setting Dialog Message
		alertDialog.setMessage(message);

		// On pressing Settings button
		alertDialog.setPositiveButton("Hủy",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		// on pressing cancel button
		alertDialog.setNegativeButton("Đồng ý tạo mới",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						saveResume(resume);
					}
				});
		alertDialog.setCancelable(false);
		// Showing Alert Message
		alertDialog.show();
	}

	private String getMD5Hash(String input) {
		final String MD5 = "MD5";
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
			digest.update(input.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuilder hexString = new StringBuilder();
			for (byte aMessageDigest : messageDigest) {
				String h = Integer.toHexString(0xFF & aMessageDigest);
				while (h.length() < 2)
					h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	private class JsonTask extends AsyncTask<String, String, String> {
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(BeforeSurveyCreateActivity.this);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		protected String doInBackground(String... params) {
			HttpURLConnection connection = null;
			BufferedReader reader = null;
			try {
				URL url = new URL(params[0]);
				connection = (HttpURLConnection) url.openConnection();
				connection.connect();
				InputStream stream = connection.getInputStream();
				reader = new BufferedReader(new InputStreamReader(stream));
				StringBuffer buffer = new StringBuffer();
				String line;
				while ((line = reader.readLine()) != null) {
					buffer.append(line+"\n");
				}
				return buffer.toString();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
				try {
					if (reader != null) {
						reader.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (pDialog.isShowing()){
				pDialog.dismiss();
			}
			JsonObject jObject = new Gson().fromJson(result, JsonObject.class);
			String code = jObject.get("errorCode").getAsString();
			if (code.equals("00")) {
				JsonObject innerData = jObject.getAsJsonObject("Data");
				serialNumber.setText(innerData.get("so_serial").getAsString().trim());
				policyInsurance.setText(innerData.get("sodon_baohiem").getAsString().trim());
				licensePlate.setText(innerData.get("bien_kiemsoat").getAsString().trim());
				vehicleNumber.setText(innerData.get("so_khung").getAsString().trim());
			}
		}
	}
}