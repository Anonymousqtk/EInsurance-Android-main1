package com.pvi.activities;

import java.math.BigDecimal;
import java.math.RoundingMode;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

public class CarCalculationActivity extends Activity {

    private EditText numberOfSeat, yearOfUsed, money, moneyPerson;
    private TextWatcher textWatcher, textWatcherP;
//    private float ratioGeneral = 0.0f;
//    private float carFee = 0.0f, personalFee = 0.0f, humanInsuranceFee = 0.0f;
    private BigDecimal ratioGeneral = BigDecimal.ZERO;
    private BigDecimal carFee = BigDecimal.ZERO, personalFee = BigDecimal.ZERO, humanInsuranceFee = BigDecimal.ZERO;

    private String carData, liabilityData;
    public static final int CAR_CODE = 10000;
    public static final int LIABILITY_CODE = 10001;
    public static final int REQUEST_CODE_INPUT = 99999;
    private static final int ADDITION_2 = 2;
    private static final int ADDITION_3 = 3;
    private static final int ADDITION_6 = 6;
    private static final int ADDITION_7 = 7;
    private static final int ADDITION_8 = 8;
    private static final int ADDITION_11 = 11;

    private boolean uberSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_calculation);

        initCheckbox();
        initCommon();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (data != null) {
                if (requestCode == REQUEST_CODE_INPUT) {
                    if (resultCode == CAR_CODE) {
                        carData = (String) data.getSerializableExtra("data");
                        String str[] = carData.split("_");
                        TextView txt = (TextView) findViewById(R.id.itemSelected);
                        int _start = 0;
                        if (str[3].length() > 2) {
                            _start = 2;
                        }
                        txt.setText("Xe BH VCX: " + str[1] + "\nĐKBS: " + str[3].substring(_start, str[3].length() - 1));
                        if (str[4].equals("1")) {
                            uberSelected = true;
                        }
                    } else if (resultCode == LIABILITY_CODE) {
                        liabilityData = (String) data.getSerializableExtra("data");
                        String str[] = liabilityData.split("_");
                        TextView txt = (TextView) findViewById(R.id.itemSelectedPersonal);
                        txt.setText("Xe BH TNDS: " + str[0]);
                    }
                }
            }
        } catch (Exception ex) {

        }
    }

    private void initCheckbox() {
        CheckBox checbox2 = (CheckBox) findViewById(R.id.checkBox2);
        checbox2.setOnCheckedChangeListener(new checkboxChangeClicked());
    }

    class checkboxChangeClicked implements CheckBox.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // TODO Auto-generated method stub
            if (buttonView == (CheckBox) findViewById(R.id.checkBox2)) {
                TextView txt = (TextView) findViewById(R.id.moneyLabel);
                TextView txtSeat = (TextView) findViewById(R.id.txtNumberOfSeat);
                if (isChecked) {
                    moneyPerson.setVisibility(View.VISIBLE);
                    txt.setVisibility(View.VISIBLE);
                    txtSeat.setVisibility(View.VISIBLE);
                    numberOfSeat.setVisibility(View.VISIBLE);
                } else {
                    moneyPerson.setVisibility(View.GONE);
                    txt.setVisibility(View.GONE);
                    txtSeat.setVisibility(View.GONE);
                    numberOfSeat.setVisibility(View.GONE);
                }
            }
        }
    }
    private void initCommon() {
        yearOfUsed = (EditText) findViewById(R.id.yearOfUsed);
        numberOfSeat = (EditText) findViewById(R.id.numberOfSeat);
        numberOfSeat.setVisibility(View.GONE);

        textWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                money.removeTextChangedListener(textWatcher);// after this line you do
                // the editing code
                money.setText(formatCurrency(s.toString().replace(".", "")));
                money.addTextChangedListener(textWatcher); // you register again for listener callbacks
                money.setSelection(money.getText().length()); // move cursor to the end of text
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };

        textWatcherP = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                moneyPerson.removeTextChangedListener(textWatcherP);// after this line you do the editing code
                moneyPerson.setText(formatCurrency(s.toString().replace(".", "")));
                moneyPerson.addTextChangedListener(textWatcherP); // you register again for listener callbacks
                moneyPerson.setSelection(moneyPerson.getText().length()); // move cursor to the end of text
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };

        money = (EditText) findViewById(R.id.money);
        money.addTextChangedListener(textWatcher);

        moneyPerson = (EditText) findViewById(R.id.moneyPerson);
        moneyPerson.addTextChangedListener(textWatcherP);
        moneyPerson.setVisibility(View.GONE);

        TextView txt = (TextView) findViewById(R.id.moneyLabel);
        txt.setVisibility(View.GONE);
        TextView txtSeat = (TextView) findViewById(R.id.txtNumberOfSeat);
        txtSeat.setVisibility(View.GONE);

        Button calBtn = (Button) findViewById(R.id.calculateButton);
        calBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                CheckBox box1 = (CheckBox) findViewById(R.id.checkBox1);
                CheckBox box2 = (CheckBox) findViewById(R.id.checkBox2);
                CheckBox box3 = (CheckBox) findViewById(R.id.checkBox3);
                if (!box1.isChecked() && !box2.isChecked() && !box3.isChecked()) {
                    showAlertWithMessage("Chưa chọn loại hình bảo hiểm\nLựa chọn loại hình bảo hiểm!");
                    return;
                }

                if (box3.isChecked() && carData == null) {
                    showAlertWithMessage("Chưa chọn loại xe tham gia bảo hiểm VCX\nHãy lựa chọn!");
                    return;
                }

                if (box1.isChecked() && liabilityData == null) {
                    showAlertWithMessage("Chưa chọn loại xe tham gia bảo hiểm TNDS bắt buộc\nHãy lựa chọn!");
                    return;
                }

                if ((yearOfUsed.getText().toString().length() == 0 || money.getText().toString().length() == 0) && box3.isChecked()) {
                    showAlertWithMessage("Vui lòng nhập số tiền bảo hiểm VCX và năm sử dụng");
                    return;
                }

                if (numberOfSeat.getText().toString().length() == 0 && box2.isChecked()) {
                    String estring = "Nhập vào số người tham gia\nTrong mọi trường hợp, tổng MTN/xe không quá 8 tỷ đồng\nVới xe KDVT chỉ cấp BH cho lái phụ xe";
                    ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.RED);
                    SpannableStringBuilder ssbuilder = new SpannableStringBuilder(estring);
                    ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
                    numberOfSeat.setError(ssbuilder);
                    return;
                }

                if (moneyPerson.getText().toString().length() == 0 && box2.isChecked()) {
                    String estring = "Nhập vào số tiền bảo hiểm/người/vụ";
                    ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.RED);
                    SpannableStringBuilder ssbuilder = new SpannableStringBuilder(estring);
                    ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
                    moneyPerson.setError(ssbuilder);
                    return;
                }

                calculateValues();
            }
        });

        Button input = (Button) findViewById(R.id.settingsButton);
        input.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(), CarSelectionActivity.class);
                startActivityForResult(i, REQUEST_CODE_INPUT);

            }
        });

        Button liability = (Button) findViewById(R.id.liabilityButton);
        liability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(), PublicLiabilityActivity.class);
                startActivityForResult(i, REQUEST_CODE_INPUT);
            }
        });

    }

    private void showAlertWithMessage(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        TextView title = new TextView(this);
        title.setText("PVI eInsurance");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.RED);
        title.setTextSize(20);
        // Setting Dialog Title
        alertDialog.setCustomTitle(title);
        // Setting Dialog Message
        alertDialog.setMessage(message);
        // on pressing cancel button
        alertDialog.setNegativeButton("Thử lại", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.setCancelable(false);
        // Showing Alert Message
        alertDialog.show();
    }

    @SuppressLint("DefaultLocale")
    private String formatCurrency(String string) {
        if (string.length() == 0) {
            return null;
        }
        return String.format("%,.0f", Double.parseDouble(string)).replace(",", ".");
    }

    private void calculateValues() {

        BigDecimal carValue = BigDecimal.ZERO;

        ratioGeneral = BigDecimal.ZERO;
        carFee = BigDecimal.ZERO;
        personalFee = BigDecimal.ZERO;
        humanInsuranceFee = BigDecimal.ZERO;

        int yearOfUsed = 0;
        CheckBox box1 = (CheckBox) findViewById(R.id.checkBox1);
        CheckBox box2 = (CheckBox) findViewById(R.id.checkBox2);
        CheckBox box3 = (CheckBox) findViewById(R.id.checkBox3);

        if (box3.isChecked()) {
            carValue = new BigDecimal(Float.parseFloat(this.money.getText().toString().replace(".", "")));

            if (carValue.floatValue() < 100000000.0f) {
                this.showAlertWithMessage("Giá trị bảo hiểm không ít hơn 100 triệu đồng!");
                return;
            }

            yearOfUsed = Integer.parseInt(this.yearOfUsed.getText().toString());

            if (uberSelected) {
                if (carValue.floatValue() < 400000000.0f || yearOfUsed > 3) {
                    showAlertWithMessage("Xe tham gia Uber/Grab phải có số năm sử dụng <= 3 và giá trị xe >= 400 triệu!\nKhông thể khai thác loại xe này!");
                    return;
                }
            }

            String _values = carData.split("_")[2];
            int _groupIndex = Integer.parseInt(carData.split("_")[0].toString());
            ratioGeneral = new BigDecimal(Float.parseFloat(_values));
            // for year of used
            if (yearOfUsed <= 6) {
                ratioGeneral = ratioGeneral.add(BigDecimal.ZERO);
            } else if (yearOfUsed > 6 && yearOfUsed <= 10) {
                ratioGeneral = ratioGeneral.add(new BigDecimal(0.1));
            } else if (yearOfUsed > 10 && yearOfUsed <= 20 && _groupIndex == 0) {
                ratioGeneral = ratioGeneral.add(new BigDecimal(0.2));
            } else if (yearOfUsed > 10 && yearOfUsed <= 15 && (_groupIndex == 1 || _groupIndex == 2)) {
                ratioGeneral = ratioGeneral.add(new BigDecimal(0.2));
            } else {
                this.showAlertWithMessage("Số năm sử dụng không hợp lệ đối với loại xe đã chọn!");
                return;
            }

            // for additional insured rider
            String _str = carData.split("_")[3];
            String _additions = _str.substring(0, _str.length() - 1);
            String[] _additionArray = _additions.split(",");
            if (_additionArray.length > 1) {
                for (int i = 1; i < _additionArray.length; i++) {
                    String s = _additionArray[i];
                    int additionValue = Integer.parseInt(s);
                    switch (additionValue) {
                        case ADDITION_2:
                            ratioGeneral = ratioGeneral.add(new BigDecimal(0.1));
                            break;
                        case ADDITION_3:
                            ratioGeneral = ratioGeneral.add(new BigDecimal(0.2));
                            break;
                        case ADDITION_6:
                            if (yearOfUsed > 3 && yearOfUsed <= 6) {
                                if (_groupIndex == 0) {
                                    ratioGeneral = ratioGeneral.add(new BigDecimal(0.1));
                                } else if (_groupIndex == 1) {
                                    ratioGeneral = ratioGeneral.add(new BigDecimal(0.15));
                                } else if (_groupIndex == 2) {
                                    ratioGeneral = ratioGeneral.add(new BigDecimal(0.2));
                                }
                            } else if (yearOfUsed > 6 && yearOfUsed <= 10) {
                                if (_groupIndex == 0) {
                                    ratioGeneral = ratioGeneral.add(new BigDecimal(0.15));
                                } else if (_groupIndex == 1) {
                                    ratioGeneral = ratioGeneral.add(new BigDecimal(0.2));
                                } else if (_groupIndex == 2) {
                                    ratioGeneral = ratioGeneral.add(new BigDecimal(0.3));
                                }
                            }
                            // update from 12/04/2017
                            else if (yearOfUsed > 10 && (yearOfUsed <= 15)) {
                                if (_groupIndex == 0) {
                                    ratioGeneral = ratioGeneral.add(new BigDecimal(0.2));
                                } else if (_groupIndex == 1) {
                                    ratioGeneral = ratioGeneral.add(new BigDecimal(0.4));
                                } else if (_groupIndex == 2) {
                                    ratioGeneral = ratioGeneral.add(new BigDecimal(0.4));
                                }
                            } else if (yearOfUsed > 15 && yearOfUsed <= 20) {
                                if (_groupIndex == 0) {
                                    ratioGeneral = ratioGeneral.add(new BigDecimal(0.5));
                                } else if (_groupIndex == 1) {
                                    ratioGeneral = ratioGeneral.add(new BigDecimal(0.4));
                                } else if (_groupIndex == 2) {
                                    ratioGeneral = ratioGeneral.add(new BigDecimal(0.4));
                                }
                            } else if (yearOfUsed > 20) {
                                if (_groupIndex == 0) {
                                    ratioGeneral = ratioGeneral.add(new BigDecimal(0.5));
                                } else if (_groupIndex == 1) {
                                    ratioGeneral = ratioGeneral.add(new BigDecimal(0.4));
                                } else if (_groupIndex == 2) {
                                    ratioGeneral = ratioGeneral.add(new BigDecimal(0.45));
                                }
                            }
//                            else if (yearOfUsed > 10 && (yearOfUsed <= 15)) {
//                                if (_groupIndex == 0) {
//                                    ratioGeneral = ratioGeneral.add(new BigDecimal(0.2));
//                                } else if (_groupIndex == 1) {
//                                    ratioGeneral = ratioGeneral.add(new BigDecimal(0.25));
//                                } else if (_groupIndex == 2) {
//                                    ratioGeneral = ratioGeneral.add(new BigDecimal(0.35));
//                                }
//                            } else if (yearOfUsed > 15 && yearOfUsed <= 20) {
//                                if (_groupIndex == 0) {
//                                    ratioGeneral = ratioGeneral.add(new BigDecimal(0.25));
//                                } else if (_groupIndex == 1) {
//                                    ratioGeneral = ratioGeneral.add(new BigDecimal(0.3));
//                                } else if (_groupIndex == 2) {
//                                    ratioGeneral = ratioGeneral.add(new BigDecimal(0.4));
//                                }
//                            } else if (yearOfUsed > 20) {
//                                if (_groupIndex == 0) {
//                                    ratioGeneral = ratioGeneral.add(new BigDecimal(0.3));
//                                } else if (_groupIndex == 1) {
//                                    ratioGeneral = ratioGeneral.add(new BigDecimal(0.35));
//                                } else if (_groupIndex == 2) {
//                                    ratioGeneral = ratioGeneral.add(new BigDecimal(0.45));
//                                }
//                            }
                            break;
                        case ADDITION_7:
                            if (yearOfUsed <= 3) {
                                ratioGeneral = ratioGeneral.add(new BigDecimal(0.1));
                            } else if (yearOfUsed > 3 && yearOfUsed <= 6) {
                                ratioGeneral = ratioGeneral.add(new BigDecimal(0.2));
                            } else if (yearOfUsed > 6 && yearOfUsed <= 10) {
                                ratioGeneral = ratioGeneral.add(new BigDecimal(0.3));
                            } else if (yearOfUsed > 10) {// update from 12/04/2017
                                ratioGeneral = ratioGeneral.add(new BigDecimal(0.5));
                            }
//                            else if (yearOfUsed > 10 && yearOfUsed <= 15) {
//                                ratioGeneral = ratioGeneral.add(new BigDecimal(0.35));
//                            } else if (yearOfUsed > 15 && yearOfUsed <= 20) {
//                                ratioGeneral = ratioGeneral.add(new BigDecimal(0.4));
//                            } else if (yearOfUsed > 20) {
//                                ratioGeneral = ratioGeneral.add(new BigDecimal(0.45));
//                            }
                            break;
                        case ADDITION_8:
                            ratioGeneral = ratioGeneral.add(new BigDecimal(0.1));
                            break;
                        case ADDITION_11:
                            ratioGeneral = ratioGeneral.add(new BigDecimal(0.1));
                            break;

                        default:
                            break;
                    }
                }
            }
        }

        if (uberSelected) {
            ratioGeneral = ratioGeneral.multiply(new BigDecimal(1.3));
        }
        carFee = carValue.multiply(ratioGeneral).divide(new BigDecimal(100));

        if (box2.isChecked()) {
            float personValue = Float.parseFloat(this.moneyPerson.getText().toString().replace(".", ""));
            int numberOfPerson = (Integer.parseInt(this.numberOfSeat.getText().toString()));
            if (personValue < 5000000.0f) {
                this.showAlertWithMessage("Số tiền bảo hiểm tai nạn người trên xe không nhỏ hơn 5 triệu đồng!");
                return;
            } else if (personValue >= 5000000.0f && personValue <= 500000000.0f) {
                humanInsuranceFee = new BigDecimal(personValue).multiply(new BigDecimal(0.001f)).multiply(new BigDecimal(numberOfPerson));
            } else if (personValue >= 500000000.0f && personValue <= 1000000000.0f) {
                humanInsuranceFee = new BigDecimal(personValue).multiply(new BigDecimal(0.002f)).multiply(new BigDecimal(numberOfPerson));
            } else {
                humanInsuranceFee = new BigDecimal(personValue).multiply(new BigDecimal(0.003f)).multiply(new BigDecimal(numberOfPerson));
            }
        }

        if (box1.isChecked()) {
            String _valuePersonal = liabilityData.split("_")[1];
            personalFee = new BigDecimal(Float.parseFloat(_valuePersonal));
        }

        Intent i = new Intent(getApplicationContext(), CarValuesActivity.class);
        i.putExtra("PERSONAL", personalFee.floatValue());
        i.putExtra("HUMAN", humanInsuranceFee.floatValue());
        i.putExtra("CAR", carFee.floatValue());
        i.putExtra("PERCENT_CAR", round(ratioGeneral.floatValue(), 3));
        startActivity(i);

    }

    private float round(float value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

}
