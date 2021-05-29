package com.sdbiosensor.form;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.sdbiosensor.covicatch.R;

import java.util.ArrayList;

public class CovidFormActivity extends AppCompatActivity {
    EditText etFirstname,etLastname,etMobilenumber,etAddress,etPincode;
    Spinner spGender;
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
        btnNext = (Button)findViewById(R.id.btn_Next);
        spGender = (Spinner)findViewById(R.id.sp_Gender);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validation()){
                    nextStep();
                }
            }
        });

        loadGender();
    }

    private void loadGender() {

        ArrayList<String> gender = new ArrayList<String>();
        gender.add("Select gender");
        gender.add("Male");
        gender.add("Female");
        gender.add("Others");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,gender);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGender.setAdapter(arrayAdapter);

        spGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    //do nothing
                }
                else {
                    spGender.setSelection(position);
                    String selGender = spGender.getItemAtPosition(position).toString();


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void nextStep() {
       startActivity(new Intent(CovidFormActivity.this,CovidFormActivity1.class));
       finish();
    }

    private boolean validation() {



        if (etFirstname.getText().toString().isEmpty()){
            etFirstname.setError("Enter first name");
            return false;
        }

        if (etLastname.getText().toString().isEmpty()){
            etFirstname.setError("Enter last name");
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