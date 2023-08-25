package com.pvi.activities;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UpdateSoapURLActivity extends Activity {
	private Button btn_url_ok,btn_url_cancel,btn_check_url;
	private EditText edt_input_url;
	String prefname="my_data"; 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.changeurl_soap);
		getFormWidgets();
		addEvents();
		
	}

	
	public void addEvents()
	{
		btn_url_ok.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	
	        	SharedPreferences pre=getSharedPreferences(prefname, MODE_PRIVATE);
	           	SharedPreferences.Editor editor=pre.edit();	         
	            	editor.remove("web_url"); //remove giá trị trước khi cập nhật mới
	           		String url_new=edt_input_url.getText().toString(); 
	           		editor.putString("web_url", url_new);
	           		editor.commit();
	           		//Toast.makeText(getApplicationContext(), "Cập nhật xong",Toast.LENGTH_LONG).show();
	           		finish();
	           		
	           
	        }
	        
		});
		
		btn_url_cancel.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	finish();
	        
	        	
	         }
		});
		btn_check_url.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	Boolean check_url;
        		
	        	if(edt_input_url.getText().toString()!="")
	           	{
	        		String c_url="http://"+edt_input_url.getText().toString()+"/Soap_mobile/Mobile_soap.asmx?WSDL";
	   	        	 try {
	   	        		 
	   	        	        java.net.URL url = new java.net.URL (c_url);
	   	        	        InputStream i = null;
	   	        	        try {
	   							i = url.openStream();
	   						} catch (IOException e) {
	   							check_url=false;
	   						}      	       
	   	        	        	
	   	        	       
	   	        	        if (i != null) {
	   	        	        	check_url=true;
	   	        	        }
	   	        	        else
	   	        	        {
	   	        	        	check_url=false;
	   	        	        }
	   	        	        	
	   	        	        if(check_url)
	   	        	        {
	   	        	        	btn_url_ok.setEnabled(true);
	   	        	        }
	   	        	    } 
	   	        	 catch (MalformedURLException e)
	   	        	    {
	   	        	        e.printStackTrace();
	   	                }
	           	}
	           	else
	           	{
	           		Toast.makeText(getApplicationContext(), "Chưa nhập URL mới",Toast.LENGTH_LONG).show();
	           	}
	        	
	        	
	        	
	         }
		});
	}
	public void getFormWidgets()
	{
		
		btn_url_ok=(Button) findViewById(R.id.btn_url_ok);	
		btn_check_url=(Button) findViewById(R.id.btn_check_url);	
		btn_url_cancel=(Button) findViewById(R.id.btn_url_cancel);		
		edt_input_url=(EditText) findViewById(R.id.edt_input_url);
		btn_url_ok.setEnabled(false);
		
	
	}
}
