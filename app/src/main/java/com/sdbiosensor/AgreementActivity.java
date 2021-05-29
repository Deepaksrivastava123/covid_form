package com.sdbiosensor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.sdbiosensor.covicatch.R;
import com.sdbiosensor.form.CovidFormActivity;

public class AgreementActivity extends AppCompatActivity {

    CheckBox cbCondition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);

        cbCondition = (CheckBox) findViewById(R.id.cb_Condition);

       cbCondition.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               startActivity(new Intent(AgreementActivity.this,CovidFormActivity.class));
               finish();
           }
       });
    }
}