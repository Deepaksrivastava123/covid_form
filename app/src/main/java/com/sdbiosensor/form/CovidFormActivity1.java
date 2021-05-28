package com.sdbiosensor.form;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.sdbiosensor.covicatch.R;

import java.util.ArrayList;

public class CovidFormActivity1 extends AppCompatActivity {
    Spinner spState,spCity,spSymptomstatus,spSample;
    EditText etAdharnumber;
    Button btnNext;
    TextView txtSymptom,txtCondition;
    boolean[] selectedSymp;
    ArrayList<Integer> sympList = new ArrayList<>();
    String[] sympArray = {"Fever","Cough","Cold","Diarrhea","Stomach pain","Sore throat",
            "Vomiting","Chest pain","Nasal discharge","Body pain",
            "Breathlessness/Difficulty in breathing","Others"};

    boolean[] selectedCond;
    ArrayList<Integer> condList = new ArrayList<>();
    String[] condArray = {"Lung disease","Heart disease","HIV","Diabetes","Cancer","Dialysis",
              "Hyper tension","Others"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid_form1);

        spState = (Spinner)findViewById(R.id.sp_State);
        spCity = (Spinner)findViewById(R.id.sp_City);
        spSymptomstatus = (Spinner)findViewById(R.id.sp_Symptomstatus);
        txtSymptom = (TextView) findViewById(R.id.txt_Symptom);
        txtCondition = (TextView) findViewById(R.id.txt_Condition);
        spSample = (Spinner)findViewById(R.id.sp_Sample);
        etAdharnumber = (EditText) findViewById(R.id.edit_Adharnumber);
        btnNext = (Button) findViewById(R.id.btn_Next);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CovidFormActivity1.this,CovidFormActivity2.class));
                finish();
            }
        });

        txtSymptom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSymptomDialog();
            }
        });

        txtCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openConditionDialog();
            }
        });
    }

    private void openConditionDialog() {
        selectedCond = new boolean[condArray.length];

        AlertDialog.Builder builder = new AlertDialog.Builder(
                CovidFormActivity1.this);
        builder.setTitle("Select Your Medical COndition");
        builder.setCancelable(false);

        builder.setMultiChoiceItems(condArray, selectedCond, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if(isChecked){
                    condList.add(which);
                }else {
                    condList.remove(which);
                }
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int j=0; j<condList.size(); j++){
                    stringBuilder.append(condArray[condList.get(j)]);
                    if (j!=condList.size()-1){
                        stringBuilder.append(" , ");
                    }
                }
                txtCondition.setText(stringBuilder.toString());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int j=0; j<selectedCond.length; j++){
                    selectedSymp[j] = false;
                    condList.clear();
                    txtCondition.setText("");
                }
            }
        });

        builder.show();

    }

    private void openSymptomDialog() {

        selectedSymp = new boolean[sympArray.length];

        AlertDialog.Builder builder = new AlertDialog.Builder(
                CovidFormActivity1.this);
        builder.setTitle("Select Your Symptoms");
        builder.setCancelable(false);

        builder.setMultiChoiceItems(sympArray, selectedSymp, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if(isChecked){
                    sympList.add(which);
                }else {
                    sympList.remove(which);
                }
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int j=0; j<sympList.size(); j++){
                    stringBuilder.append(sympArray[sympList.get(j)]);
                    if (j!=sympList.size()-1){
                        stringBuilder.append(" , ");
                    }
                }
                txtSymptom.setText(stringBuilder.toString());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int j=0; j<selectedSymp.length; j++){
                    selectedSymp[j] = false;
                    sympList.clear();
                    txtSymptom.setText("");
                }
            }
        });

        builder.show();
    }




}