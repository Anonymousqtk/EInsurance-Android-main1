package com.pvi.activities;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.pvi.adapter.GridImageAdapter;
import com.pvi.helpers.AsyncTaskManager;
import com.pvi.helpers.DatabaseHelper;
import com.pvi.helpers.FileHelper;
import com.pvi.helpers.GetServerTimeHelper;
import com.pvi.helpers.GlobalData;
import com.pvi.helpers.GlobalMethod;
import com.pvi.helpers.OnAsyncTaskCompleteListener;
import com.pvi.helpers.TimeService;
import com.pvi.helpers.TimerCounterHelper;
import com.pvi.helpers.UploadImagesHelper;
import com.pvi.objects.BeforSurveyObject;
import com.pvi.objects.BeforeSurveyImage;
import com.pvi.objects.BeforeSurveyOfflineImage;
import com.pvi.objects.BeforeSurveyOfflineObject;
import com.pvi.objects.PVIShipObject;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
//import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class BeforeSurveyDetailActivity extends AppCompatActivity {

    private int majorType;
    private String objectID;
    private DatabaseHelper writeHelper;
    private AsyncTaskManager taskManager;
    private GridView gridView;
    private ArrayList<BeforeSurveyImage> gridArray;
    private GridImageAdapter gridAdapter;
//    private GPSTracker gps;
    private static final int CAMERA_REQUEST_CODE = 1000;
    private Button captureButton, uploadButton, offlineButton;
    private static final String IMAGE_UPLOADED = "1";
    private static final String IMAGE_NOT_UPLOADED = "0";

    public static final int REQUEST_CODE_INPUT_SEARCH = 1001;
    public static final int RESULT_CODE_SAVE_SEARCH = 1002;
    private int currentUploadIndex = 0;
    ProgressDialog progress;

    private TimerCounterHelper timerCounter = null;
    String[] permissions=
            new String[]{
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.CAMERA};
    //Uri uri_for_camera;
    boolean is_storage_image_permitted = false;
    boolean is_storage_camera_permitted = false;
    private static final String TAG = "PERMISSION_TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        setContentView(R.layout.before_survey_capture_activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions=
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.CAMERA};
        }else{
            permissions=
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA};
        }
        taskManager = new AsyncTaskManager(this);
        writeHelper = DatabaseHelper.getWritableInstance(this);

        captureButton = (Button) findViewById(R.id.btn_chup);
        uploadButton = (Button) findViewById(R.id.btn_upload);
        offlineButton = (Button) findViewById(R.id.btn_anhoffline);

        majorType = GlobalMethod.getMajorType(BeforeSurveyDetailActivity.this, MODE_PRIVATE);
        Intent i = getIntent();
        if (majorType == GlobalMethod.CAR_MAJOR) {
            BeforSurveyObject resume = (BeforSurveyObject) i.getSerializableExtra("BeforSurveyObject");
            objectID = resume.getObjectID();
        } else if (majorType == GlobalMethod.MOTO_MAJOR) {
            BeforSurveyObject resume = (BeforSurveyObject) i.getSerializableExtra("BeforSurveyObject");
            objectID = resume.getObjectID();
        } else if (majorType == GlobalMethod.SHIP_MAJOR) {
            PVIShipObject shipObject = (PVIShipObject) i.getSerializableExtra("BeforSurveyObject");
            objectID = shipObject.getObjectID();
        }
        writeHelper.open();
        gridArray = (ArrayList<BeforeSurveyImage>) writeHelper.getAllPicturesWithObjectID(objectID);
        writeHelper.close();

        gridView = (GridView) findViewById(R.id.gridViewImage);
        gridAdapter = new GridImageAdapter(this, R.layout.image_row_item, gridArray);
        gridView.setAdapter(gridAdapter);
        if(!is_storage_image_permitted){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionStorageImage();
            }
        }
        // GPS Tracker
//        gps = new GPSTracker(BeforeSurveyDetailActivity.this);
        getServerTime();

    }
    public void requestPermissionStorageImage(){
            if(ContextCompat.checkSelfPermission(getApplicationContext(),permissions[0]) == PackageManager.PERMISSION_GRANTED){
                Log.i(TAG,permissions[0] + " Granted");
                is_storage_image_permitted = true;
                requestPermissionCameraAccess();
            }else{
                request_permision_storage_image.launch(permissions[0]);
            }
    }
    private void requestPermissionCameraAccess() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(),permissions[1]) == PackageManager.PERMISSION_GRANTED){
            Log.i(TAG,permissions[1] + " Granted");
            is_storage_image_permitted = true;
        }else{
            request_permision_camera_access.launch(permissions[1]);
        }
    }
    private final ActivityResultLauncher<String> request_permision_storage_image  = registerForActivityResult(new ActivityResultContracts.RequestPermission(), is_Granted ->{
        if(is_Granted){
            Log.i(TAG,permissions[0] + " Granted");
            is_storage_image_permitted = true;
        }else{
            Log.i(TAG,permissions[0] + " not Granted");
            is_storage_image_permitted = false;
        }
        requestPermissionCameraAccess();
    });
    private final ActivityResultLauncher<String> request_permision_camera_access  = registerForActivityResult(new ActivityResultContracts.RequestPermission(), is_Granted ->{
        if(is_Granted){
            Log.i(TAG,permissions[1] + " Granted");
            is_storage_camera_permitted = true;
        }else{
            Log.i(TAG,permissions[1] + " not Granted");
            is_storage_camera_permitted = false;
        }
    });
//    public void openCamera(){
//        ContentValues values = new ContentValues();
//        values.put(MediaStore.Images.Media.TITLE,"Camera");
//        values.put(MediaStore.Images.Media.DESCRIPTION,"Capture image");
//        uri_for_camera = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//        Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        cameraintent.putExtra(MediaStore.EXTRA_OUTPUT,uri_for_camera );
//        launcher_for_camera.launch(cameraintent);
//    }
//    private ActivityResultLauncher<Intent> launcher_for_camera  = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//            @Override
//            public void onActivityResult(ActivityResult o) {
//                if(o.getResultCode() == RESULT_OK){
//                    String uri_ = uri_for_camera.toString();
//                    Log.i(TAG," Uri capture" + uri_);
//                }
//            }
//    });
//    private void sendToSettingDialog() {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BeforeSurveyDetailActivity.this);
//        // Setting Dialog Title
//        alertDialog.setTitle("Thông báo quyền truy cập");
//        // Setting Dialog Message
//        alertDialog.setMessage("Cài đặt quyền truy cập");
//        // On pressing Settings button
//        alertDialog.setPositiveButton("Cài đặt", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent();
//                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                Uri uri = Uri.fromParts("packages", getPackageName(), null);
//                intent.setData(uri);
//                startActivity(intent);
//                dialog.dismiss();
//            }
//        }).setNegativeButton("Thoát", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                    finish();
//            }
//        });
//        alertDialog.setCancelable(false);
//        // Showing Alert Message
//        alertDialog.show();
//    }
    @Override
    protected void onStart() {
        super.onStart();
        captureButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(is_storage_camera_permitted){
                    capture();
                }else{
                    requestPermissionCameraAccess();
                }

            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (GlobalData.getInstance().uploadIndexes.size() == 0) {
                    showToast("Vui lòng chọn ảnh gửi đi!");
                    return;
                }

                uploadImageEvent(currentUploadIndex);
            }
        });

        gridView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                gotoFullView(position);
                // TODO Auto-generated method stub
                return false;
            }
        });

        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                BeforeSurveyImage image = gridArray.get(position);
                if (image.getUploadState().equals(IMAGE_UPLOADED)) {
                    return;
                }

                ImageView imgCheck = (ImageView) view.findViewById(R.id.stateImage);
                if (imgCheck.getVisibility() == ImageView.GONE) {
                    imgCheck.setVisibility(ImageView.VISIBLE);
                } else {
                    imgCheck.setVisibility(ImageView.GONE);
                }

                boolean isAvailable = false;
                for (int i = 0; i < GlobalData.getInstance().uploadIndexes.size(); i++) {
                    Integer value = GlobalData.getInstance().uploadIndexes.get(i);
                    if (value == position) {
                        GlobalData.getInstance().uploadIndexes.remove(i);
                        isAvailable = true;
                        break;
                    }
                }

                if (!isAvailable) {
                    GlobalData.getInstance().uploadIndexes.add(position);
                }

            }
        });

        offlineButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BeforeSurveySelectOfflineActivity.class);
                startActivityForResult(intent, REQUEST_CODE_INPUT_SEARCH);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (GlobalData.getInstance().getUsername() == null) {
            this.finish();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progress != null)
            progress.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timerCounter != null) {
            timerCounter.interrupTime();
            timerCounter = null;
        }
    }

    private void reloadGridview() {
        gridArray.clear();
        writeHelper.open();
        gridArray = (ArrayList<BeforeSurveyImage>) writeHelper.getAllPicturesWithObjectID(objectID);
        writeHelper.close();

        try {
            gridAdapter = new GridImageAdapter(this, R.layout.image_row_item, gridArray);
            gridView.setAdapter(gridAdapter);

        } catch (NullPointerException e) {
            // TODO: handle exception
            gridAdapter = new GridImageAdapter(this, R.layout.image_row_item, gridArray);
        }
    }

    private void capture() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = FileHelper.createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("Can't create file", ex.getLocalizedMessage());
            }

            Uri photoUri = Uri.fromFile(photoFile);

            // Continue only if the File was successfully created
            if (photoFile != null) {
                // currentFilePath = photoFile.getAbsolutePath();
                GlobalData.getInstance().setCurrentPhotoPath(photoFile.getAbsolutePath());

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
        /**
        if (gps.canGetLocation()) {

            // can not get current location - go back
            if (gps.getLocation() == null) {
                this.showAlertWithMessage("Không lấy được vị trí!", true);
                return;
            }

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = FileHelper.createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Log.e("Can't create file", ex.getLocalizedMessage());
                }

                Uri photoUri = Uri.fromFile(photoFile);

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);

                    // currentFilePath = photoFile.getAbsolutePath();
                    GlobalData.getInstance().setCurrentPhotoPath(photoFile.getAbsolutePath());
                }
            }

        } else {
            gps.showSettingsAlert();
        }
         */

    }

    private void gotoFullView(int index) {
        BeforeSurveyImage image = gridArray.get(index);
        Intent i = new Intent(this, BeforeSurveyImageViewerActivity.class);
        i.putExtra("PATH", image.getFileName());
        startActivity(i);
    }

    private void uploadImageEvent(Integer index) {

        final int value = GlobalData.getInstance().uploadIndexes.get(index);
        BeforeSurveyImage image = gridArray.get(value);

        UploadImagesHelper task = new UploadImagesHelper();
        taskManager.executeTask(task, UploadImagesHelper.createRequest(image), getString(R.string.image_uploading), new OnAsyncTaskCompleteListener<String>() {

            @Override
            public void onTaskCompleteSuccess(String result) {

                if (result.equals(IMAGE_UPLOADED)) {
                    BeforeSurveyImage image = gridArray.get(value);
                    image.setUploadState(IMAGE_UPLOADED);
                    writeHelper.open();
                    writeHelper.updatePicture(image);
                    writeHelper.close();

                    showToast("Tải thành công ảnh " + (currentUploadIndex + 1));
                    currentUploadIndex++;

                    if (currentUploadIndex >= GlobalData.getInstance().uploadIndexes.size()) {
                        showToast("Tải hết ảnh thành công!");

                        // ** also need remove uploadIndexes
                        GlobalData.getInstance().uploadIndexes.clear();
                        currentUploadIndex = 0;
                        reloadGridview();

                        return;
                    }

                    uploadImageEvent(currentUploadIndex);

                } else if (result.equals("-3")) {
                    showToast("Hồ sơ này đã quá hạn. Không thể thực hiện thao tác này!");
                    GlobalData.getInstance().uploadIndexes.clear();
                    currentUploadIndex = 0;
                    reloadGridview();
                    return;
                } else if (result.equals("-4")) {
                    showToast("Đơn cho tàu này đã được cấp trên PIAS. Không thể thực hiện thao tác này!");
                    GlobalData.getInstance().uploadIndexes.clear();
                    currentUploadIndex = 0;
                    reloadGridview();
                    return;
                } else {
                    showToast("Có lỗi trong quá trình tải ảnh lên!");

                    // ** also need remove uploadIndexes
                    GlobalData.getInstance().uploadIndexes.clear();
                    currentUploadIndex = 0;
                    reloadGridview();
                    return;
                }

            }

            @Override
            public void onTaskFailed(Exception cause) {
                showToast("Có lỗi trong quá trình tải ảnh lên!");

                // ** also need remove uploadIndexes
                GlobalData.getInstance().uploadIndexes.clear();
                currentUploadIndex = 0;
                reloadGridview();
                return;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                progress = ProgressDialog.show(this, "Đang xử lý ảnh", "Hãy chờ...", true);

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        // add new with server time
                        // replace gps.getTime();
                        String timeValue = timerCounter.getCurrentTime();
                        // end server time

                        // do the thing that takes a long time
                        String filePath = FileHelper.compressImage(GlobalData.getInstance().getCurrentPhotoPath(), getApplicationContext(), true, timeValue.replaceAll("/", "").replaceAll(":", ""), majorType);
                        FileHelper.addWatermark(filePath, "User: " + GlobalData.getInstance().getUsername(), "Thời gian: " + timeValue, "");

                        // create BeforeSurveyImage
                        // write to local database
                        BeforeSurveyImage image = new BeforeSurveyImage(objectID, GlobalData.getInstance().getUsername(), filePath, timeValue, "0", "0", IMAGE_NOT_UPLOADED);
                        writeHelper.open();
                        writeHelper.insertPicture(image);
                        writeHelper.close();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (progress != null)
                                    progress.dismiss();
                                // update view
                                reloadGridview();

                                // delete external file
                                FileHelper.deleteTempContent();

                                // re-call camera
//                                gps.getLocation();
                                capture();

                            }
                        });
                    }
                }).start();

            } else {
                super.onActivityResult(requestCode, resultCode, data);
                FileHelper.deleteTempContent();
                reloadGridview();
                // stop using GPS tracker
//                gps.stopUsingGPS();
                return;
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
            try {
                if (data != null) {
                    if (requestCode == REQUEST_CODE_INPUT_SEARCH) {
                        // image offline
                        if (resultCode == RESULT_CODE_SAVE_SEARCH) {
                            BeforeSurveyOfflineObject gr = (BeforeSurveyOfflineObject) data.getSerializableExtra("id_offline");

                            writeHelper.open();
                            List<BeforeSurveyOfflineImage> listImages = writeHelper.getOfflineImagesWithFrkey(gr.getPrimaryKey());
                            writeHelper.close();
                            if (listImages.size() > 0) {
                                for (int i = 0; i < listImages.size(); i++) {
                                    BeforeSurveyImage image = new BeforeSurveyImage(objectID, GlobalData.getInstance().getUsername(), listImages.get(i).getFileName(), listImages.get(i).getTakenDay(), listImages.get(i).getLongitude(), listImages.get(i).getLatitude(), IMAGE_NOT_UPLOADED);
                                    writeHelper.open();
                                    writeHelper.insertPicture(image);
                                    writeHelper.close();
                                }
                                // update view
                                reloadGridview();
                            }

                        }
                    }
                }
            } catch (Exception ex) {

            }
        }

    }

    private void getServerTime() {
        GetServerTimeHelper task = new GetServerTimeHelper();
        taskManager.executeTask(task, GetServerTimeHelper.createRequest(), getString(R.string.texGetTime), new OnAsyncTaskCompleteListener<String>() {
            @Override
            public void onTaskCompleteSuccess(String result) {
                // TODO Auto-generated method stub
                if (result == null) {
                    showAlertWithMessage("Không đồng bộ được giờ server!", true);
                    return;
                }
                startTimeService(result);
                timerCounter = new TimerCounterHelper();
                try {
                    timerCounter.timeIncrement(result);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void onTaskFailed(Exception cause) {
                // TODO Auto-generated method stub
                showAlertWithMessage("Không đồng bộ được giờ server!", true);
            }
        });
    }

    private void showAlertWithMessage(String message, final boolean goBack) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BeforeSurveyDetailActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle("PVI eInsurance");
        // Setting Dialog Message
        alertDialog.setMessage(message);
        // On pressing Settings button
        alertDialog.setPositiveButton("Thử lại", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                // go back
                if (goBack) {
                    finish();
                }

            }
        });
        alertDialog.setCancelable(false);
        // Showing Alert Message
        alertDialog.show();
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }


    private void startTimeService(String initTime) {
        if (!GlobalMethod.isServiceRunning(TimeService.class, this)) {
            Intent serviceIntent = new Intent(getApplicationContext(), TimeService.class);
            serviceIntent.putExtra(TimeService.RECEIVER_FLAG, initTime);
            startService(serviceIntent);
        }
    }
}
