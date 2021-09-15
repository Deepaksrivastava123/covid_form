package com.dotvik.covify.screens;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.gson.Gson;
import com.dotvik.covify.R;
import com.dotvik.covify.constants.Constants;
import com.dotvik.covify.customcomoponents.BaseActivity;
import com.dotvik.covify.network.models.LocalDataModel;
import com.dotvik.covify.utils.SharedPrefUtils;
import com.dotvik.covify.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Calendar;

public class TimerActivity extends BaseActivity implements View.OnClickListener {
    public static String TAG = "TimerActivity";

    public static final int TIMER_INTERVAL = 1;     //In minutes
    private TextView text_timer;
    private Button button_take_picture;
    private Calendar savedCalendar;
    private Calendar currentCalendar;
    private boolean isTimerUp = false;
    private CountDownTimer mainCountdownTimer;

    public static final int CAMERA_PERMISSIONS_CODE  = 1001;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        initViews();
        handleClicks();
        loadTimer();
    }

    private void initViews() {
        text_timer = findViewById(R.id.text_timer);
        button_take_picture = findViewById(R.id.button_take_picture);

        try {
            String tempString = SharedPrefUtils.getInstance(this).getString(Constants.PREF_LOCAL_MODEL, "");
            LocalDataModel localDataModel = new Gson().fromJson(tempString, LocalDataModel.class);
            ((TextView) findViewById(R.id.text_serial_number)).setText(getString(R.string.serial_number) + " " + localDataModel.getQrCode());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleClicks() {
        button_take_picture.setOnClickListener(this);
    }

    private void loadTimer() {
        currentCalendar = Calendar.getInstance();
        savedCalendar = Calendar.getInstance();

        long savedTime = SharedPrefUtils.getInstance(this).getLong(Constants.PREF_TIMER_ALARM_TIME, -1);
        savedCalendar.setTimeInMillis(savedTime);

        long timePassed = savedCalendar.getTimeInMillis() - currentCalendar.getTimeInMillis();
        long seconds = Utils.secondsBetween(savedCalendar, currentCalendar);
        if (timePassed > 0) {
            isTimerUp = false;
            startTimer(seconds);
        } else {
            isTimerUp = true;
            text_timer.setText("00:00");
        }

        handleViewBasedOnTimer();
    }

    private void startTimer(long seconds) {
        mainCountdownTimer = new CountDownTimer(seconds * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                text_timer.setText(Utils.convertMillisToMS(millisUntilFinished));
            }

            public void onFinish() {
                text_timer.setText("00:00");
                isTimerUp = true;
                handleViewBasedOnTimer();
            }

        }.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mainCountdownTimer != null) {
            mainCountdownTimer.cancel();
            mainCountdownTimer = null;
        }
    }

    private void handleViewBasedOnTimer() {
        if (isTimerUp) {
            button_take_picture.setTextColor(getResources().getColor(R.color.app_blue));
        } else {
            button_take_picture.setTextColor(getResources().getColor(R.color.grey));
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_take_picture) {
            if (isTimerUp) {
                clickPhoto();
            } else {
                showErrorDialog(getString(R.string.wait_for_timer));
            }
        }
    }

    private void clickPhoto() {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                // check again permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                                , Manifest.permission.CAMERA},
                        CAMERA_PERMISSIONS_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE
                                , Manifest.permission.CAMERA},
                        CAMERA_PERMISSIONS_CODE);
                // Grant Permission
            }
        } else {
            ImagePicker.with(this)
                    .compress(1024)
                    .cameraOnly()
                    .crop(1080, 1920)
                    .start();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (resultCode == RESULT_OK) {
            String currentPhotoPath = data.getData().getPath();


            Log.d(TAG, "currentPhotoPath = " + currentPhotoPath);
            // handling of exif information
            try {
                File f = new File(currentPhotoPath);
                ExifInterface exif = new ExifInterface(f.getPath());
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                int angle = 0;

                if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
                {
                    angle = 90;
                }
                else if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
                {
                    angle = 180;
                }
                else if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
                {
                    angle = 270;
                }

                Matrix mat = new Matrix();
                mat.postRotate(angle);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;

                Log.d(TAG, "Angle = " + angle);
                Bitmap inComing = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
                Bitmap converted = Bitmap.createBitmap(inComing, 0, 0, inComing.getWidth(), inComing.getHeight(), mat, true);

                String newFileName = "";
                if(currentPhotoPath.endsWith(".png") || currentPhotoPath.endsWith(".jpg")) {
                    newFileName = currentPhotoPath.substring(0, currentPhotoPath.length() - 4) + "_resize.jpg";
                } else if(currentPhotoPath.endsWith(".jpeg")) {
                    newFileName = currentPhotoPath.substring(0, currentPhotoPath.length() - 5) + "_resize.jpg";
                } else {
                    newFileName = currentPhotoPath;
                }
                OutputStream stream = new FileOutputStream(newFileName);
                Log.d(TAG, "newFileName = " + newFileName);
                converted.compress(Bitmap.CompressFormat.JPEG, 95, stream);
                currentPhotoPath = newFileName;
            } catch(Exception e) {
                e.printStackTrace();
            }




            Intent intent = new Intent(this, PleaseWaitActivity.class);
            intent.putExtra("photo", currentPhotoPath);
            startActivity(intent);
            finish();
        }
    }

}