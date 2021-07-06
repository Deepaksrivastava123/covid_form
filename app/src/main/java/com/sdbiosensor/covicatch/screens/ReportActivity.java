package com.sdbiosensor.covicatch.screens;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.sdbiosensor.covicatch.BuildConfig;
import com.sdbiosensor.covicatch.R;
import com.sdbiosensor.covicatch.customcomoponents.BaseActivity;

import java.io.File;

public class ReportActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        ((TextView)findViewById(R.id.text)).setText("File downloaded to Downloads folder as: " + getIntent().getStringExtra("name"));

        findViewById(R.id.button_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File downloadsPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File file = new File(downloadsPath, getIntent().getStringExtra("name"));
                Uri uri;
                if (Build.VERSION.SDK_INT < 24) {
                    uri = Uri.fromFile(file);
                } else {
                    uri = FileProvider.getUriForFile(ReportActivity.this, BuildConfig.APPLICATION_ID + ".provider", file);
                }
                Intent viewFile = new Intent(Intent.ACTION_VIEW);
                viewFile.setDataAndType(uri, "application/pdf");
                viewFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(viewFile);
            }
        });
    }

}