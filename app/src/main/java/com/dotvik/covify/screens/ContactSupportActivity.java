package com.dotvik.covify.screens;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.dotvik.covify.R;
import com.dotvik.covify.constants.Constants;
import com.dotvik.covify.customcomoponents.BaseActivity;

public class ContactSupportActivity extends BaseActivity implements View.OnClickListener {

    private boolean hasBeenChecked = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        handleClicks();
    }

    private void handleClicks() {
        findViewById(R.id.text_call).setOnClickListener(this);
        findViewById(R.id.text_mail).setOnClickListener(this);
        findViewById(R.id.text_sms).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.text_call) {
            openCallIntent();
        } else  if (view.getId() == R.id.text_mail) {
            openMailIntent();
        } else  if (view.getId() == R.id.text_sms) {
            openSmsIntent();
        }
    }

    private void openSmsIntent() {
        Intent intentDial = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + "18001237599"));
        startActivity(intentDial);
    }

    private void openMailIntent() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{Constants.CONTACT_SUPPORT_EMAIL});
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        intent.setData(Uri.parse("mailto:"));
        startActivity(Intent.createChooser(intent, "Choose an Email client :"));
    }

    private void openCallIntent() {
        Intent intentDial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "18001237599"));
        startActivity(intentDial);
    }
}