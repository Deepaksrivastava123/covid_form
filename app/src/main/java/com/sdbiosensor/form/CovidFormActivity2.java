package com.sdbiosensor.form;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.sdbiosensor.covicatch.R;

public class CovidFormActivity2 extends AppCompatActivity {
    EditText etTime;
    TextView txtOpengallery;
    RadioButton rbPositive,rbNegative,rbInvalid;
    Button btnUpload,btnPreviewform,btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid_form2);

        etTime = (EditText)findViewById(R.id.edit_Time);
        txtOpengallery = (TextView)findViewById(R.id.txt_Opengallery);
        rbPositive = (RadioButton) findViewById(R.id.rb_Positive);
        rbNegative = (RadioButton) findViewById(R.id.rb_Negative);
        rbInvalid = (RadioButton) findViewById(R.id.rb_Invalid);
        btnUpload = (Button)findViewById(R.id.btn_Upload);
        btnPreviewform = (Button)findViewById(R.id.btn_Previewform);
        btnSubmit = (Button)findViewById(R.id.btn_Submit);
    }
}