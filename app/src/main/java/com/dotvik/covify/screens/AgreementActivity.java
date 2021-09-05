package com.dotvik.covify.screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.dotvik.covify.R;
import com.dotvik.covify.customcomoponents.BaseActivity;

public class AgreementActivity extends BaseActivity {

    private boolean hasBeenChecked = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);

        handleCheckBox();
    }

    private void handleCheckBox() {
        ((CheckBox) findViewById(R.id.check_tnc)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked && !hasBeenChecked) {
                    hasBeenChecked = true;
                    buttonView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent;
                            if (getIntent().getSerializableExtra("user") == null) {
                                intent = new Intent(AgreementActivity.this, FormActivity.class);
                            } else {
                                intent = new Intent(AgreementActivity.this, FormProfileActivity.class);
                                intent.putExtra("user", getIntent().getSerializableExtra("user"));
                            }
                            startActivity(intent);
                            finish();
                        }
                    }, 500);
                }
            }
        });
    }
}