package com.sdbiosensor.covicatch.screens;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;
import com.sdbiosensor.covicatch.R;
import com.sdbiosensor.covicatch.constants.Constants;
import com.sdbiosensor.covicatch.network.models.LocalDataModel;
import com.sdbiosensor.covicatch.utils.SharedPrefUtils;
import com.sdbiosensor.covicatch.utils.Utils;
import com.tejpratapsingh.pdfcreator.activity.PDFCreatorActivity;
import com.tejpratapsingh.pdfcreator.utils.PDFUtil;
import com.tejpratapsingh.pdfcreator.views.PDFBody;
import com.tejpratapsingh.pdfcreator.views.PDFFooterView;
import com.tejpratapsingh.pdfcreator.views.PDFHeaderView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFHorizontalView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFImageView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFLineSeparatorView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFTextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.Locale;

public class PdfCreatorActivity extends PDFCreatorActivity {

    private LocalDataModel localDataModel;
    private String imageToUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        String tempString = SharedPrefUtils.getInstance(this).getString(Constants.PREF_LOCAL_MODEL, "");
        localDataModel = new Gson().fromJson(tempString, LocalDataModel.class);
        imageToUpload = getIntent().getExtras().getString("photo");

        createPDF("Result", new PDFUtil.PDFUtilListener() {
            @Override
            public void pdfGenerationSuccess(File savedPDFFile) {
                File downloadsPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File resultFile = new File(downloadsPath, "COVI-CATCH-" + Utils.getFormattedDateTime(Calendar.getInstance()) + ".pdf");
                try {
                    exportFile(savedPDFFile, resultFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                SharedPrefUtils.getInstance(PdfCreatorActivity.this).resetAllWithoutLogout();
                showDialog("Result PDF saved to Downloads as Result.pdf");
            }

            @Override
            public void pdfGenerationFailure(Exception exception) {
                Toast.makeText(PdfCreatorActivity.this, "PDF NOT Created", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.create().show();
    }

    private void exportFile(File src, File dst) throws IOException {
        FileChannel inChannel = null;
        FileChannel outChannel = null;

        try {
            inChannel = new FileInputStream(src).getChannel();
            outChannel = new FileOutputStream(dst).getChannel();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }

    @Override
    protected PDFHeaderView getHeaderView(int pageIndex) {
        PDFHeaderView headerView = new PDFHeaderView(getApplicationContext());

        PDFTextView kitNumberView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.H2);
        kitNumberView.setText(localDataModel.getQrCode());
        kitNumberView.setLayout(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        kitNumberView.getView().setGravity(Gravity.END);
        headerView.addView(kitNumberView);

        PDFHorizontalView horizontalView = new PDFHorizontalView(getApplicationContext());

        PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.HEADER);
        SpannableString word = new SpannableString("Ultra Covi-Catch Test Report");
        word.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        pdfTextView.setText(word);
        pdfTextView.setLayout(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        pdfTextView.getView().setGravity(Gravity.CENTER_VERTICAL);
        pdfTextView.getView().setTypeface(pdfTextView.getView().getTypeface(), Typeface.BOLD);

        horizontalView.addView(pdfTextView);

        PDFImageView imageView = new PDFImageView(getApplicationContext());
        LinearLayout.LayoutParams imageLayoutParam = new LinearLayout.LayoutParams(
                60,
                60, 0);
        imageView.setImageScale(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setImageResource(R.mipmap.ic_launcher);
        imageLayoutParam.setMargins(0, 0, 10, 0);
        imageView.setLayout(imageLayoutParam);

        horizontalView.addView(imageView);

        headerView.addView(horizontalView);

        PDFLineSeparatorView lineSeparatorView1 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.WHITE);
        headerView.addView(lineSeparatorView1);

        return headerView;
    }

    @Override
    protected PDFBody getBodyViews() {
        PDFBody pdfBody = new PDFBody();

        PDFTextView pdfDateView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.H3);
        pdfDateView.setText("Date : " + Utils.getFormattedDate(Calendar.getInstance()));
        pdfBody.addView(pdfDateView);
        PDFLineSeparatorView lineSeparatorView1 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.WHITE);
        pdfBody.addView(lineSeparatorView1);
        PDFTextView pdfUserDetailsView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
        pdfUserDetailsView.setText("Name: " + localDataModel.getFirstName() + " " + localDataModel.getLastName() + "\n" +
                "Date of birth : " + localDataModel.getDob() + "\n" +
                "Gender : " + localDataModel.getGender() + "\n" +
                "ID Type : " + localDataModel.getId_type() + "\n" +
                "ID Number : " + localDataModel.getId_no() + "\n" +
                "Address : " + localDataModel.getAddress() + "\n" +
                "City : " + localDataModel.getCity() + "\n" +
                "Pincode : " + localDataModel.getPincode() + "\n" +
                "Symptoms : " + Utils.getCsvFromArrayList(localDataModel.getSymptoms()) + "\n" +
                "Medical Condition : " + Utils.getCsvFromArrayList(localDataModel.getConditions()) + "\n");
        pdfBody.addView(pdfUserDetailsView);

        String result = new String[] {"Negative", "Positive", "Inconclusive",
                "Negative", "Positive", "Inconclusive",
                "Negative", "Positive", "Inconclusive"}[(int)(Math.random()*9)];
        PDFTextView pdfResultView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.H3);
        pdfResultView.setText("Result : " + result + "\n\n");
        pdfBody.addView(pdfResultView);

        Bitmap cassetteImage = BitmapFactory.decodeFile(imageToUpload);
        PDFImageView imageView = new PDFImageView(getApplicationContext());
        LinearLayout.LayoutParams imageLayoutParam = new LinearLayout.LayoutParams(
                200,
                200, 0);
        imageView.setImageScale(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setImageBitmap(cassetteImage);
        imageLayoutParam.setMargins(0, 0, 10, 0);
        imageView.setLayout(imageLayoutParam);
        pdfBody.addView(imageView);

        PDFTextView pdfNoteView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
        pdfNoteView.setText("Note:\n" +
                "1. All individuals who test positive by Rapid Antigen Test may be considered as True Positive. All such i\n" +
                "ndividuals are advised to follow home isolation & care as per government guidelines.\n" +
                "2. If any individual with symptoms test negative by Rapid Antigen test, they should immediately get teste\n" +
                "d by RT-PCR. Such individuals should also considered themselves as suspect cases of Covid-19 and follow h\n" +
                "ome isolation protocol while awaiting the RT-PCR results.\n" +
                "3. Rapid Antigen tests are highly specific but their sensitivity depends on the viral load present in different individuals, proper sample collection & extraction. Ultra Covi-Catch has undergone strict quality control procedures to provide high accuracy of the test. However, the product is used outside the control of the manufacturing company & its channel partners. Moreever, the result may be affected due to envirnmental factors or human error, in such case, the company or its channel partners will not be liable for any losses or costs whether direct or indirect arising out of incorrect diagnosis.");
        pdfBody.addView(pdfNoteView);

        return pdfBody;
    }

    @Override
    protected PDFFooterView getFooterView(int pageIndex) {
        PDFFooterView footerView = new PDFFooterView(getApplicationContext());

        PDFTextView pdfTextViewPage = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
        pdfTextViewPage.setText(String.format(Locale.getDefault(), "Page: %d", pageIndex + 1));
        pdfTextViewPage.setLayout(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 0));
        pdfTextViewPage.getView().setGravity(Gravity.CENTER_HORIZONTAL);

        footerView.addView(pdfTextViewPage);

        return footerView;
    }

    @Nullable
    @Override
    protected PDFImageView getWatermarkView(int forPage) {
        PDFImageView pdfImageView = new PDFImageView(getApplicationContext());
        FrameLayout.LayoutParams childLayoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                200, Gravity.CENTER);
        pdfImageView.setLayout(childLayoutParams);

        pdfImageView.setImageResource(R.drawable.img_app_logo);
        pdfImageView.setImageScale(ImageView.ScaleType.FIT_CENTER);
        pdfImageView.getView().setAlpha(0.3F);

        return pdfImageView;
    }

    @Override
    protected void onNextClicked(final File savedPDFFile) {
        finish();
    }
}