package com.pvi.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.pvi.adapter.DongXeAdapter;
import com.pvi.adapter.LoaiXeAdapter;
import com.pvi.adapter.MucDichSdAdapter;
import com.pvi.adapter.NhanHieuXeAdapter;
import com.pvi.helpers.AsyncTaskManager;
import com.pvi.helpers.FileHelper;
import com.pvi.helpers.GetDongXe;
import com.pvi.helpers.GetGiaTriXe;
import com.pvi.helpers.GetLoaiXe;
import com.pvi.helpers.GetMucDichSd;
import com.pvi.helpers.GetNhanHieuXe;
import com.pvi.helpers.OnAsyncTaskCompleteListener;
import com.pvi.objects.DongXeObject;
import com.pvi.objects.LoaiXeObject;
import com.pvi.objects.MucDichSdObject;
import com.pvi.objects.NhanHieuXeObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TraCuuGiaTriXeActivity extends Activity {
    private AsyncTaskManager taskManager;
    LinearLayout ln_kq;
    TextView txt_kq;
    TextView txt_val_nhxe, txt_dongxe, txt_val_mdsd, txt_val_loaixe, txt_nam_sx;
    EditText edt_trongtai, edt_so_cho;
    Switch mSwitch;
    ArrayList<NhanHieuXeObject> arrayNhanHieuXe= new ArrayList<>();
    NhanHieuXeObject nhan_hieu_sel = null;
    ArrayList<MucDichSdObject> arrayMdichSd= new ArrayList<>();
    MucDichSdObject mucdichsd_sel = null;
    ArrayList<DongXeObject> arrayDongXe= new ArrayList<>();
    DongXeObject dongxe_sel = null;
    ArrayList<LoaiXeObject> arrayLoaiXe = new ArrayList<>();
    LoaiXeObject loai_xe_sel = null;
    ArrayList<String> arr_nam_sx= new ArrayList<>();
    String nam_sx_sel = "";
    boolean xemoi = true;
    NhanHieuXeAdapter nhan_hieu_adapter;
    MucDichSdAdapter mdichsdung_adapter;
    LoaiXeAdapter loaixe_adapter;
    DongXeAdapter dongxe_adapter;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tra_cuu_giatri_xe);
        taskManager = new AsyncTaskManager(this);
        txt_val_nhxe=findViewById(R.id.txt_val_nhxe);
        txt_dongxe = findViewById(R.id.txt_val_dongxe);
        txt_val_mdsd =  findViewById(R.id.txt_val_mdsd);
        txt_val_loaixe =  findViewById(R.id.txt_val_loaixe);
        txt_nam_sx =  findViewById(R.id.txt_nam_sx);
        mSwitch =  findViewById(R.id.switch_1);
        edt_trongtai = findViewById(R.id.edt_trongtai);
        edt_so_cho = findViewById(R.id.edt_so_cho);
        ln_kq = findViewById(R.id.ln_kq);
        txt_kq = findViewById(R.id.txt_kq);
        initNhanHieuXe();
        initMucDichSd();
        initNamSX();
        initSwitch();
        Button search = (Button) findViewById(R.id.goSearchAction);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateSearch();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (taskManager == null) {
            taskManager = new AsyncTaskManager(this);
        }
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }
    private void showToastLong(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }
    public void initSwitch(){
        mSwitch.setChecked(true);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    xemoi = true;
                    //Toast.makeText(TraCuuGiaTriXeActivity.this, "Xe mới", Toast.LENGTH_SHORT).show();
                }
                else {
                    xemoi = false;
                    //Toast.makeText(TraCuuGiaTriXeActivity.this, "Xe cũ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void validateSearch() {
        // hide all keyboards
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        if(nhan_hieu_sel == null){
            showToast("Quý khách vui lòng chọn nhãn hiệu xe");
            return;
        }
        if(dongxe_sel == null){
            showToast("Quý khách vui lòng chọn dòng xe");
            return;
        }
        if(mucdichsd_sel == null){
            showToast("Quý khách vui lòng chọn mục đích sử dụng");
            return;
        }
        if(loai_xe_sel == null){
            showToast("Quý khách vui lòng chọn loại xe");
            return;
        }
        String sSo_Cho = edt_so_cho.getText().toString();
        if (sSo_Cho.matches("")) {
            showToast("Quý khách vui lòng nhập số chỗ");
            return;
        }
        if(nam_sx_sel.equals("")){
            showToast("Quý khách vui lòng chọn năm sản xuất");
            return;
        }
        GetGiaTriXe();
    }
    public void initNhanHieuXe(){
        GetNhanHieuXe task = new GetNhanHieuXe();
        taskManager.executeTask(task, GetNhanHieuXe.createRequest(), getString(R.string.title_activity_value_car), new OnAsyncTaskCompleteListener<ArrayList<NhanHieuXeObject>>() {
            @Override
            public void onTaskCompleteSuccess(ArrayList<NhanHieuXeObject> result) {
                if (result.size() <= 0) {
                    showToast("Không có dữ liệu nhãn hiệu xe!");
                    return;
                }
                arrayNhanHieuXe = result;
                txt_val_nhxe.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onClick(View v) {
                        // Initialize dialog
                        dialog=new Dialog(TraCuuGiaTriXeActivity.this);
                        // set custom dialog
                        dialog.setContentView(R.drawable.dialog_searchable_spinner);
                        // set custom height and width
                        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.95);
                        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.80);
                        dialog.getWindow().setLayout(width,height);
                        int divierId = dialog.getContext().getResources()
                                .getIdentifier("android:id/titleDivider", null, null);
                        View divider = dialog.findViewById(divierId);
                        divider.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        // set transparent background
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        // show dialog
                        dialog.show();
                        // Initialize and assign variable
                        EditText editText=dialog.findViewById(R.id.edit_text);
                        ListView listView=dialog.findViewById(R.id.list_view);
                        TextView txtHolder = dialog.findViewById(R.id.txt_title_holder);
                        txtHolder.setText("Chọn nhãn hiệu xe");
                        ImageView btn_close = dialog.findViewById(R.id.btn_close);
                        btn_close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        // Initialize array adapter
                        nhan_hieu_adapter= new NhanHieuXeAdapter(TraCuuGiaTriXeActivity.this, R.layout.spinner_row_item, arrayNhanHieuXe);
                        nhan_hieu_adapter.notifyDataSetChanged();
                        // set adapter
                        listView.setAdapter(nhan_hieu_adapter);
                        editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }
                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                ArrayList<NhanHieuXeObject> arrtL_temp_one = new ArrayList<>();
                                //adapter.getFilter().filter(s.toString());
                                if (arrtL_temp_one.size() > 0)
                                    arrtL_temp_one.clear();
                                if (s.length() == 0) {
                                    arrtL_temp_one.addAll(arrayNhanHieuXe);
                                } else {
                                    for (int listNo = 0; listNo < arrayNhanHieuXe.size(); listNo++) {
                                        final String value = arrayNhanHieuXe.get(listNo).getTen_nhomxe().toString().toLowerCase();
						                final String str = FileHelper.ConvertString(value); // đưa chuỗi về không dấu
						                final String strPrefix = FileHelper.ConvertString(s.toString().toLowerCase()); // đưa chuỗi về không dấu
                                        if (str.contains(strPrefix)) {
                                            NhanHieuXeObject contact = new NhanHieuXeObject(arrayNhanHieuXe.get(listNo).getMa_nhomxe(),arrayNhanHieuXe.get(listNo).getTen_nhomxe());
                                            arrtL_temp_one.add(contact);
                                        }
                                    }
                                }
                                nhan_hieu_adapter= new NhanHieuXeAdapter(TraCuuGiaTriXeActivity.this, R.layout.spinner_row_item, arrtL_temp_one);
                                listView.setAdapter(nhan_hieu_adapter);
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                txt_val_nhxe.setText(nhan_hieu_adapter.getItem(position).getTen_nhomxe());
                                nhan_hieu_sel = nhan_hieu_adapter.getItem(position);
                                initDongXe(nhan_hieu_adapter.getItem(position).getMa_nhomxe());
                                // Dismiss dialog
                                nhan_hieu_adapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });
                    }
                });
            }
            @Override
            public void onTaskFailed(Exception cause) {
                showToast(cause.getMessage() + " Tìm xe thất bại. Hãy thử lại!");
            }
        });
    }
    public void initDongXe(String parent_val){
        GetDongXe task = new GetDongXe();
        taskManager.executeTask(task, GetDongXe.createRequest(parent_val), getString(R.string.title_activity_value_car), new OnAsyncTaskCompleteListener<ArrayList<DongXeObject>>() {
            @Override
            public void onTaskCompleteSuccess(ArrayList<DongXeObject> result) {
                if (result.size() <= 0) {
                    showToast("Không có dữ liệu dòng xe!");
                    return;
                }
                if(arrayDongXe.size() > 0 && arrayDongXe != null){
                    dongxe_sel = null;
                    txt_dongxe.setText("");
                    arrayDongXe.clear();
                }
                arrayDongXe = result;
                txt_dongxe.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onClick(View v) {
                        // Initialize dialog
                        dialog=new Dialog(TraCuuGiaTriXeActivity.this);
                        // set custom dialog
                        dialog.setContentView(R.drawable.dialog_searchable_spinner);
                        // set custom height and width
                        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.95);
                        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.80);
                        dialog.getWindow().setLayout(width,height);
                        int divierId = dialog.getContext().getResources()
                                .getIdentifier("android:id/titleDivider", null, null);
                        View divider = dialog.findViewById(divierId);
                        divider.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        // set transparent background
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        // show dialog
                        dialog.show();
                        // Initialize and assign variable
                        EditText editText=dialog.findViewById(R.id.edit_text);
                        ListView listView=dialog.findViewById(R.id.list_view);
                        TextView txtHolder = dialog.findViewById(R.id.txt_title_holder);
                        txtHolder.setText("Chọn dòng xe");
                        ImageView btn_close = dialog.findViewById(R.id.btn_close);
                        btn_close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        // Initialize array adapter
                        dongxe_adapter= new DongXeAdapter(TraCuuGiaTriXeActivity.this, R.layout.spinner_row_item, arrayDongXe);
                        dongxe_adapter.notifyDataSetChanged();
                        // set adapter
                        listView.setAdapter(dongxe_adapter);
                        editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }
                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                ArrayList<DongXeObject> arrtL_temp_one = new ArrayList<>();
                                //adapter.getFilter().filter(s.toString());
                                if (arrtL_temp_one.size() > 0)
                                    arrtL_temp_one.clear();
                                if (s.length() == 0) {
                                    arrtL_temp_one.addAll(arrayDongXe);
                                } else {
                                    for (int listNo = 0; listNo < arrayDongXe.size(); listNo++) {
                                        final String value = arrayDongXe.get(listNo).getTen_dongxe().toString().toLowerCase();
                                        final String str = FileHelper.ConvertString(value); // đưa chuỗi về không dấu
                                        final String strPrefix = FileHelper.ConvertString(s.toString().toLowerCase()); // đưa chuỗi về không dấu
                                        if (str.contains(strPrefix)) {
                                            DongXeObject contact = new DongXeObject(arrayDongXe.get(listNo).getMa_dongxe(),arrayDongXe.get(listNo).getTen_dongxe());
                                            arrtL_temp_one.add(contact);
                                        }
                                    }
                                }
                                dongxe_adapter= new DongXeAdapter(TraCuuGiaTriXeActivity.this, R.layout.spinner_row_item, arrtL_temp_one);
                                listView.setAdapter(dongxe_adapter);
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                txt_dongxe.setText(dongxe_adapter.getItem(position).getTen_dongxe());
                                dongxe_sel = dongxe_adapter.getItem(position);
                                // Dismiss dialog
                                dongxe_adapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });
                    }
                });
            }
            @Override
            public void onTaskFailed(Exception cause) {
                showToast(cause.getMessage() + " Lấy dữ liệu dòng xe thất bại. Hãy thử lại!");
            }
        });
    }
    public void initMucDichSd(){
        GetMucDichSd task = new GetMucDichSd();
        taskManager.executeTask(task, GetMucDichSd.createRequest(), getString(R.string.title_activity_value_car), new OnAsyncTaskCompleteListener<ArrayList<MucDichSdObject>>() {
            @Override
            public void onTaskCompleteSuccess(ArrayList<MucDichSdObject> result) {
                if (result.size() <= 0) {
                    showToast("Không có dữ liệu mục đích sử dụng!");
                    return;
                }
                arrayMdichSd = result;
                txt_val_mdsd.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onClick(View v) {
                        // Initialize dialog
                        dialog=new Dialog(TraCuuGiaTriXeActivity.this);
                        // set custom dialog
                        dialog.setContentView(R.drawable.dialog_searchable_spinner);
                        // set custom height and width
                        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.95);
                        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.80);
                        dialog.getWindow().setLayout(width,height);
                        int divierId = dialog.getContext().getResources()
                                .getIdentifier("android:id/titleDivider", null, null);
                        View divider = dialog.findViewById(divierId);
                        divider.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        // set transparent background
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        // show dialog
                        dialog.show();
                        // Initialize and assign variable
                        EditText editText=dialog.findViewById(R.id.edit_text);
                        ListView listView=dialog.findViewById(R.id.list_view);
                        TextView txtHolder = dialog.findViewById(R.id.txt_title_holder);
                        txtHolder.setText("Chọn mục đích sử dụng");
                        ImageView btn_close = dialog.findViewById(R.id.btn_close);
                        btn_close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        // Initialize array adapter
                        mdichsdung_adapter= new MucDichSdAdapter(TraCuuGiaTriXeActivity.this, R.layout.spinner_row_item, arrayMdichSd);
                        mdichsdung_adapter.notifyDataSetChanged();
                        // set adapter
                        listView.setAdapter(mdichsdung_adapter);
                        editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }
                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                ArrayList<MucDichSdObject> arrtL_temp_one = new ArrayList<>();
                                //adapter.getFilter().filter(s.toString());
                                if (arrtL_temp_one.size() > 0)
                                    arrtL_temp_one.clear();
                                if (s.length() == 0) {
                                    arrtL_temp_one.addAll(arrayMdichSd);
                                } else {
                                    for (int listNo = 0; listNo < arrayMdichSd.size(); listNo++) {
                                        final String value = arrayMdichSd.get(listNo).getTen_nhphi().toString().toLowerCase();
                                        final String str = FileHelper.ConvertString(value); // đưa chuỗi về không dấu
                                        final String strPrefix = FileHelper.ConvertString(s.toString().toLowerCase()); // đưa chuỗi về không dấu
                                        if (str.contains(strPrefix)) {
                                            MucDichSdObject contact = new MucDichSdObject(arrayMdichSd.get(listNo).getMa_nhphi(),arrayMdichSd.get(listNo).getTen_nhphi());
                                            arrtL_temp_one.add(contact);
                                        }
                                    }
                                }
                                mdichsdung_adapter= new MucDichSdAdapter(TraCuuGiaTriXeActivity.this, R.layout.spinner_row_item, arrtL_temp_one);
                                listView.setAdapter(mdichsdung_adapter);
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                txt_val_mdsd.setText(mdichsdung_adapter.getItem(position).getTen_nhphi());
                                mucdichsd_sel = mdichsdung_adapter.getItem(position);
                                // Dismiss dialog
                                initLoaiXe(mdichsdung_adapter.getItem(position).getMa_nhphi());
                                mdichsdung_adapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });
                    }
                });
            }
            @Override
            public void onTaskFailed(Exception cause) {
                showToast(cause.getMessage() + " Lấy dữ liệu mục đích sử dụng thất bại. Hãy thử lại!");
            }
        });
    }
    public void initLoaiXe(String parent_val){
        GetLoaiXe task = new GetLoaiXe();
        taskManager.executeTask(task, GetLoaiXe.createRequest(parent_val), getString(R.string.title_activity_value_car), new OnAsyncTaskCompleteListener<ArrayList<LoaiXeObject>>() {
            @Override
            public void onTaskCompleteSuccess(ArrayList<LoaiXeObject> result) {
                if (result.size() <= 0) {
                    showToast("Không có dữ liệu loại xe!");
                    return;
                }
                if(arrayLoaiXe.size() > 0 && arrayLoaiXe != null){
                    loai_xe_sel = null;
                    txt_val_loaixe.setText("");
                    arrayLoaiXe.clear();
                }
                arrayLoaiXe = result;
                txt_val_loaixe.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onClick(View v) {
                        // Initialize dialog
                        dialog=new Dialog(TraCuuGiaTriXeActivity.this);
                        // set custom dialog
                        dialog.setContentView(R.drawable.dialog_searchable_spinner);
                        // set custom height and width
                        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.95);
                        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.80);
                        dialog.getWindow().setLayout(width,height);
                        int divierId = dialog.getContext().getResources()
                                .getIdentifier("android:id/titleDivider", null, null);
                        View divider = dialog.findViewById(divierId);
                        divider.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        // set transparent background
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        // show dialog
                        dialog.show();
                        // Initialize and assign variable
                        EditText editText=dialog.findViewById(R.id.edit_text);
                        ListView listView=dialog.findViewById(R.id.list_view);
                        TextView txtHolder = dialog.findViewById(R.id.txt_title_holder);
                        txtHolder.setText("Chọn loại xe");
                        ImageView btn_close = dialog.findViewById(R.id.btn_close);
                        btn_close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        // Initialize array adapter
                        loaixe_adapter= new LoaiXeAdapter(TraCuuGiaTriXeActivity.this, R.layout.spinner_row_item, arrayLoaiXe);
                        loaixe_adapter.notifyDataSetChanged();
                        // set adapter
                        listView.setAdapter(loaixe_adapter);
                        editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }
                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                ArrayList<LoaiXeObject> arrtL_temp_one = new ArrayList<>();
                                //adapter.getFilter().filter(s.toString());
                                if (arrtL_temp_one.size() > 0)
                                    arrtL_temp_one.clear();
                                if (s.length() == 0) {
                                    arrtL_temp_one.addAll(arrayLoaiXe);
                                } else {
                                    for (int listNo = 0; listNo < arrayLoaiXe.size(); listNo++) {
                                        final String value = arrayLoaiXe.get(listNo).getTen_loaixe().toString().toLowerCase();
                                        final String str = FileHelper.ConvertString(value); // đưa chuỗi về không dấu
                                        final String strPrefix = FileHelper.ConvertString(s.toString().toLowerCase()); // đưa chuỗi về không dấu
                                        if (str.contains(strPrefix)) {
                                            LoaiXeObject contact = new LoaiXeObject(arrayLoaiXe.get(listNo).getMa_phi(),arrayLoaiXe.get(listNo).getTen_loaixe());
                                            arrtL_temp_one.add(contact);
                                        }
                                    }
                                }
                                loaixe_adapter= new LoaiXeAdapter(TraCuuGiaTriXeActivity.this, R.layout.spinner_row_item, arrtL_temp_one);
                                listView.setAdapter(loaixe_adapter);
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                txt_val_loaixe.setText(loaixe_adapter.getItem(position).getTen_loaixe());
                                loai_xe_sel = loaixe_adapter.getItem(position);
                                // Dismiss dialog
                                loaixe_adapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });
                    }
                });
            }
            @Override
            public void onTaskFailed(Exception cause) {
                showToast(cause.getMessage() + " Lấy dữ liệu loại xe thất bại. Hãy thử lại!");
            }
        });
    }
    private String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + "><b>" + text + "</b></font>";
        return input;
    }
    public void GetGiaTriXe(){
        GetGiaTriXe task = new GetGiaTriXe();
        taskManager.executeTask(task, GetGiaTriXe.createRequest(
                        nhan_hieu_sel.getMa_nhomxe(),dongxe_sel.getMa_dongxe(), nam_sx_sel, mucdichsd_sel.getMa_nhphi(),loai_xe_sel.getTen_loaixe(), xemoi,
                        edt_trongtai.getText().toString(),edt_so_cho.getText().toString() )
                , getString(R.string.title_activity_value_car), new OnAsyncTaskCompleteListener<String>() {
//        taskManager.executeTask(task, GetGiaTriXe.createRequest(
//                        "027","0271205", "2022", "NHPHI01","1.Xe chở người không kinh doanh", xemoi,
//                        "0","6" )
//                , getString(R.string.title_activity_value_car), new OnAsyncTaskCompleteListener<String>() {
//        taskManager.executeTask(task, GetGiaTriXe.createRequest(
//                        "027","0271205","2022", "3002", xemoi)
//                , getString(R.string.title_activity_value_car), new OnAsyncTaskCompleteListener<String>() {
            @Override
            public void onTaskCompleteSuccess(String result) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    JSONArray consumers = jsonObject.getJSONArray("Consumers");
                    ln_kq.setVisibility(View.VISIBLE);
                    if (consumers.length() == 0) {
                        txt_kq.setText("Không tìm thấy kết quả. Hãy thử lại!");
                        return;
                    }else{
                        JSONObject obj_data = consumers.getJSONObject(0);
                        String trongkhoang = obj_data.getString("gia_tri_xe_min");
                        String denkhoang = obj_data.getString("gia_tri_xe_max");
                        NumberFormat formatter = new DecimalFormat("#,###");
                        String text = "Giá trị thực tế xe nằm trong khoảng "+getColoredSpanned(formatter.format(Double.parseDouble(trongkhoang)),"#800000")+" VNĐ đến "+getColoredSpanned(formatter.format(Double.parseDouble(denkhoang)),"#800000")+" VNĐ";
                        txt_kq.setText(Html.fromHtml(text));
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            @Override
            public void onTaskFailed(Exception cause) {
                showToast((cause != null ? cause.getMessage():"Không tìm thấy kết quả ") + ". Hãy thử lại!");
            }
        });
    }
    public void initNamSX(){
        // initialize array list
        arr_nam_sx=new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        for (int x = year; x > year-30; x--) {
            arr_nam_sx.add(x + "");
        }
        txt_nam_sx.setText(year+"");
        nam_sx_sel = year+"";
        txt_nam_sx.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                // Initialize dialog
                dialog=new Dialog(TraCuuGiaTriXeActivity.this);

                // set custom dialog
                dialog.setContentView(R.drawable.dialog_searchable_spinner);
                // set custom height and width
                int width = (int)(getResources().getDisplayMetrics().widthPixels*0.95);
                int height = (int)(getResources().getDisplayMetrics().heightPixels*0.80);
                dialog.getWindow().setLayout(width,height);
                int divierId = dialog.getContext().getResources()
                        .getIdentifier("android:id/titleDivider", null, null);
                View divider = dialog.findViewById(divierId);
                divider.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                // set transparent background
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                // show dialog
                dialog.show();
                // Initialize and assign variable
                EditText editText=dialog.findViewById(R.id.edit_text);
                ListView listView=dialog.findViewById(R.id.list_view);
                TextView txtHolder = dialog.findViewById(R.id.txt_title_holder);
                txtHolder.setText("Chọn năm sản xuất");
                ImageView btn_close = dialog.findViewById(R.id.btn_close);
                btn_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                // Initialize array adapter
                ArrayAdapter<String> adapter=new ArrayAdapter<>(TraCuuGiaTriXeActivity.this, android.R.layout.simple_list_item_1,arr_nam_sx);
                // set adapter
                listView.setAdapter(adapter);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }
                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        txt_nam_sx.setText(adapter.getItem(position));
                        nam_sx_sel = adapter.getItem(position).toString();
                        dialog.dismiss();
                    }
                });
            }
        });



    }
}
