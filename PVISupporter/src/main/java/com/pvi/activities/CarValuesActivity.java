package com.pvi.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CarValuesActivity extends Activity {

    private EditText percentOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_values);

        Intent i = getIntent();
        final float carFee, percentCarFee;
        final float personalFee, humanInsuranceFee;
        carFee = i.getFloatExtra("CAR", 0.0f);
        percentCarFee = i.getFloatExtra("PERCENT_CAR", 0.0f);
        personalFee = i.getFloatExtra("PERSONAL", 0.0f);
        humanInsuranceFee = i.getFloatExtra("HUMAN", 0.0f);

        TextView carFeeLabel = (TextView) findViewById(R.id.carFeeLBL);
        carFeeLabel.setText(String.format("%,.0f VNĐ", carFee).replace(",", " "));

        TextView percentCarFeeLBL = (TextView) findViewById(R.id.percentCarFee);
        percentCarFeeLBL.setText(String.format("%,.3f", percentCarFee) + "%");

        TextView humanLBL = (TextView) findViewById(R.id.humanFeeLBL);
        humanLBL.setText(String.format("%,.0f VNĐ", humanInsuranceFee).replace(",", " "));

        TextView personal = (TextView) findViewById(R.id.personalFeeLBL);
        personal.setText(String.format("%,.0f VNĐ", personalFee).replace(",", " "));

        TextView personalVAT = (TextView) findViewById(R.id.personalFeeVATLBL);
        personalVAT.setText(String.format("%,.0f VNĐ", personalFee * 0.1f).replace(",", " "));

        TextView total = (TextView) findViewById(R.id.totalFeeLBL);
        total.setText(String.format("%,.0f VNĐ", (carFee + humanInsuranceFee + personalFee + personalFee * 0.1f)).replace(",", " "));

        TextView totalInSale = (TextView) findViewById(R.id.carFeeInsale);
        totalInSale.setText(String.format("%,.0f VNĐ", (carFee + humanInsuranceFee)).replace(",", " "));

        percentOff = (EditText) findViewById(R.id.percentOff);
        final TextView totalOff = (TextView) findViewById(R.id.feeAfterOff);
        totalOff.setText(String.format("%,.0f VNĐ", (carFee + humanInsuranceFee + personalFee + personalFee * 0.1f)).replace(",", " "));

        final Button saleOff = (Button) findViewById(R.id.saleOffButton);
        saleOff.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // hide keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(percentOff.getWindowToken(), 0);

                // calculate
                // total Fee
                float _value = carFee + humanInsuranceFee + personalFee + personalFee * 0.1f;
                // Fee in off
                float _valueInSale = carFee + humanInsuranceFee;
                if (percentOff.getText().toString().matches("")) {
                    totalOff.setText(String.format("%,.0f VNĐ", (_value)).replace(",", " "));
                    return;
                }

                float _saleOff = (_valueInSale) * Float.parseFloat(percentOff.getText().toString()) / 100;
                totalOff.setText(String.format("%,.0f VNĐ", (_value - _saleOff)).replace(",", " "));
            }
        });
    }
}
