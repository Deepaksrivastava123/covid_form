package com.sdbiosensor.form;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sdbiosensor.covicatch.R;

public class InstructionActivity extends AppCompatActivity {

    Button btnstarttimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);

        btnstarttimer = (Button) findViewById(R.id.btn_Starttimer);

        btnstarttimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InstructionActivity.this,TimerActivity.class));
                finish();
            }
        });
    }
}