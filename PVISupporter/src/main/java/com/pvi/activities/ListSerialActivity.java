package com.pvi.activities;

import java.util.ArrayList;

import com.pvi.adapter.ErrorAdapter;
import com.pvi.adapter.MyArrayAdapter;
import com.pvi.objects.EinsuranceObject;
import com.pvi.objects.SMSErrorObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ListSerialActivity extends Activity {
    Button btn_back_loopup;
    ListView listview_loopup = null;
    TextView tv_lbl;
    ArrayList<EinsuranceObject> arrseri_bks_date = new ArrayList<EinsuranceObject>();
    MyArrayAdapter adapter = null;

    ArrayList<SMSErrorObject> arrclass_sms_err = null;//new ArrayList<class_sms_err>();
    ErrorAdapter adapter_err = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listiview_loopupseri);

        getFormWidgets();
        addEvents();

        Bundle receive_susss = this.getIntent().getExtras();
        final String[] suss = receive_susss.getStringArray("suss");
        String filter = receive_susss.getString("filter_");


        if (filter.equals("_suss") || filter.equals("_input")) {
            arrseri_bks_date = new ArrayList<EinsuranceObject>();
            for (int i = 0; i < suss.length; i++) {
                EinsuranceObject seri_bks_ = new EinsuranceObject();
                String[] re = suss[i].split(";");
                if (re[0].toString().equals("suss") && filter.equals("_suss")) {
                    seri_bks_.setid(re[1].toString());
                    seri_bks_.setseri(re[2].toString());
                    seri_bks_.setbks(re[3].toString());
                    seri_bks_.setlhbh(re[4].toString());
                    seri_bks_.setdate(re[5].toString());
                    seri_bks_.setphone(re[6].toString());
                    seri_bks_.setFeeStatus(re[7].toString());
                    arrseri_bks_date.add(seri_bks_);
                }

                if (re[0].toString().equals("input") && filter.equals("_input")) {
                    seri_bks_.setid(re[1].toString());
                    seri_bks_.setseri(re[2].toString());
                    seri_bks_.setbks(re[3].toString());
                    seri_bks_.setlhbh(re[4].toString());
                    seri_bks_.setdate(re[5].toString());
                    seri_bks_.setphone(re[6].toString());
                    seri_bks_.setFeeStatus(re[7].toString());
                    arrseri_bks_date.add(seri_bks_);
                }

            }

            adapter = new MyArrayAdapter(this, R.layout.seri_list, arrseri_bks_date);
            adapter.notifyDataSetChanged();
            listview_loopup.setAdapter(adapter);
        }
        if (filter.equals("_notsuss")) {
            arrclass_sms_err = new ArrayList<SMSErrorObject>();
            for (int i = 0; i < suss.length; i++) {
                SMSErrorObject seri_bks_ = new SMSErrorObject();
                String[] re = suss[i].split(";");

                if (re[0].toString().equals("notsuss") && filter.equals("_notsuss")) {
                    seri_bks_.setid(re[1].toString());
                    seri_bks_.setnoidungsms(re[2].toString());
                    seri_bks_.setngayps(re[3].toString());
                    seri_bks_.setphone(re[4].toString());
                    seri_bks_.setphanhoi(re[5].toString());
                    arrclass_sms_err.add(seri_bks_);
                }

            }
            try {
                adapter_err = new ErrorAdapter(this, R.layout.seri_sms_error, arrclass_sms_err);
                adapter_err.notifyDataSetChanged();
                listview_loopup.setAdapter(adapter_err);

            } catch (Exception ex) {

                ex.getMessage();
            }

        }


    }

    //	 @Override
//	    public void onBackPressed() {
////	     khi người dùng bấm back button thì không làm gì cả,
////	     phải quay lại bằng nút 'quay lại' trên popup để kích hoạt
////	     startActivityForResult vì không sử dụng đc intent này trong onBackPressed
//
//	    }
    public void addEvents() {
        btn_back_loopup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();

            }

        });
    }

    public void getFormWidgets() {
        btn_back_loopup = (Button) findViewById(R.id.btn_back_loopup);
        tv_lbl = (TextView) findViewById(R.id.tv_lbl);
        listview_loopup = (ListView) findViewById(R.id.listview_loopup);

    }
}
