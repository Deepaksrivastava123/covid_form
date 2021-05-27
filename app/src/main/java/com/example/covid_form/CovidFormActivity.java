package com.example.covid_form;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class CovidFormActivity extends AppCompatActivity {
    EditText etFirstname,etLastname,etMobilenumber,etAddress,etPincode;
    RadioGroup radioGroup;
    Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid_form);

        etFirstname = (EditText)findViewById(R.id.edit_Firstname);
        etLastname  = (EditText)findViewById(R.id.edit_Lastname);
        etMobilenumber = (EditText)findViewById(R.id.edit_MobileNumber);
        etAddress = (EditText)findViewById(R.id.edit_Address);
        etPincode = (EditText)findViewById(R.id.edit_Pincode);
        radioGroup = (RadioGroup)findViewById(R.id.radio_group);
        btnNext = (Button)findViewById(R.id.btn_Next);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validation()){
                    nextStep();
                }
            }
        });
    }

    private void nextStep() {
       startActivity(new Intent(CovidFormActivity.this,CovidFormActivity1.class));
       finish();
    }

    private boolean validation() {

        int isSelected = radioGroup.getCheckedRadioButtonId();

        if (etFirstname.getText().toString().isEmpty()){
            etFirstname.setError("Enter first name");
            return false;
        }

        if (etLastname.getText().toString().isEmpty()){
            etFirstname.setError("Enter last name");
            return false;
        }

        if(isSelected == -1){
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etAddress.getText().toString().isEmpty()){
            etFirstname.setError("Enter Address");
            return false;
        }

        if (etMobilenumber.getText().toString().isEmpty() ){
            etFirstname.setError("Enter valid mobile number");
            return false;
        }

        if (etPincode.getText().toString().isEmpty()){
            etFirstname.setError("Enter pincode");
            return false;
        }

        return true;
    }
}