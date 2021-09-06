package com.sdbiosensor.covicatch.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.sdbiosensor.covicatch.R;
import com.sdbiosensor.covicatch.customcomoponents.BaseActivity;
import com.sdbiosensor.covicatch.utils.Utils;

public class TestZxing extends BaseActivity implements View.OnClickListener {

    Button testButton = null;
    TextView textQR = null;

    // text_qr_code =
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_zxing);

       testButton =  findViewById(R.id.test_button);

       testButton.setOnClickListener(this);

       textQR = findViewById(R.id.text_qr_code);


    }

    @Override
    public void onClick(android.view.View v) {

        if(v.getId() == R.id.test_button) {

            Utils.launchZxingQRScanner(this);

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                showErrorDialog("Cancelled");
            } else {
                String qrCode = result.getContents();

                textQR.setText(qrCode);


            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
