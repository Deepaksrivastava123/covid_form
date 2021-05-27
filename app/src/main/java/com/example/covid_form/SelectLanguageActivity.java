package com.example.covid_form;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Locale;

public class SelectLanguageActivity extends AppCompatActivity {
    Button cbEnglish,cbHindi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);

        cbEnglish = (Button) findViewById(R.id.cb_English);
        cbHindi = (Button) findViewById(R.id.cb_Hindi);

        cbEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLanguage(SelectLanguageActivity.this,"en");
                startActivity(new Intent(SelectLanguageActivity.this,CovidFormActivity.class));
            }
        });

        cbHindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLanguage(SelectLanguageActivity.this,"hi");
                startActivity(new Intent(SelectLanguageActivity.this,CovidFormActivity.class));
            }
        });



    }



    public void setLanguage(Activity activity,String language){
        Locale locale = new Locale(language);
        Resources resources = activity.getResources();
        Configuration configuration=resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration,resources.getDisplayMetrics());
    }



}