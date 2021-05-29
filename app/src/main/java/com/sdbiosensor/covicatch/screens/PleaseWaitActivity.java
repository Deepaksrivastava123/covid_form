package com.sdbiosensor.covicatch.screens;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.sdbiosensor.covicatch.R;
import com.sdbiosensor.covicatch.customcomoponents.BaseActivity;

public class PleaseWaitActivity extends BaseActivity {

    ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_please_wait);

        initViews();
        sendData();
    }

    private void initViews() {
        imageView = findViewById(R.id.image);
        Bitmap imageToUpload = (Bitmap) getIntent().getExtras().get("photo");
        imageView.setImageBitmap(imageToUpload);
    }

    private void sendData() {
        //TODO api call
        //TODO reset shared prefs
    }

    @Override
    public void onBackPressed() {
        showErrorDialog(getString(R.string.cannot_go_back));
    }
}