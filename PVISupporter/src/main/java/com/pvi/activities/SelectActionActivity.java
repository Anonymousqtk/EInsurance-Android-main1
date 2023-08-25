package com.pvi.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SelectActionActivity extends Activity {
    Bundle send_agent_phone = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_action);
        // lấy thông tin agent_phone đưa vao bundle
        Bundle receive_bundle = this.getIntent().getExtras();
        final String receive_value = receive_bundle.getString("agent_phone");
        send_agent_phone.putString("agent_phone", receive_value);

    }

    public void call_detaiview_input(View v) {
        Intent intent = new Intent(getApplicationContext(), DetailInputActivity.class);
        intent.putExtras(send_agent_phone);
        startActivity(intent);
    }

    public void call_seach_seri(View v) {

        Intent intent = new Intent(getApplicationContext(), SerialFindingActivity.class);
        intent.putExtras(send_agent_phone);
        startActivity(intent);
    }

    public void call_loopup_seri(View v) {
        Intent intent = new Intent(getApplicationContext(), SerialSeachingActivity.class);
        intent.putExtras(send_agent_phone);
        startActivity(intent);
    }

    public void thoat(View v) {
        finish();
        System.exit(0);
    }
}
