package com.sdbiosensor.covicatch.screens;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.ViewGroup;
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
                showDialog("Result PDF saved to Downloads");
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

        PDFImageView imageView = new PDFImageView(getApplicationContext());
        LinearLayout.LayoutParams imageLayoutParam = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                50, 0);
        imageLayoutParam.gravity = Gravity.CENTER;
        imageView.setImageScale(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setImageResource(R.drawable.img_pdf_logo);
        imageLayoutParam.setMargins(0, 0, 10, 0);
        imageView.setLayout(imageLayoutParam);

        headerView.addView(imageView);

        return headerView;
    }

    @Override
    protected PDFBody getBodyViews() {
        PDFBody pdfBody = new PDFBody();

        PDFLineSeparatorView lineSeparator = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.BLACK);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1, 0);
        lp.setMargins(60, 10, 60, 0);
        lineSeparator.setLayout(lp);

        pdfBody.addView(lineSeparator);

        addRow("Test Report Number", localDataModel.getQrCode(), pdfBody);
        addRow("Name", localDataModel.getFirstName() + " " + localDataModel.getLastName(), pdfBody);
        addRow("Address", localDataModel.getAddress(), pdfBody);
        addRow("Gender", localDataModel.getGender(), pdfBody);
        addRow("Mobile Number", localDataModel.getMobile(), pdfBody);
        addRow("ID Type", localDataModel.getId_type(), pdfBody);
        addRow("ID Number", localDataModel.getId_no(), pdfBody);
        addBlueSeparator(pdfBody);
        addRow("Test Name", "Ultra Covi-Catch (Home Test for Covid-19 Ag)", pdfBody);
        addBlueSeparator(pdfBody);
        addRow("Symptoms", Utils.getCsvFromArrayList(localDataModel.getSymptoms()), pdfBody);
        addRow("Medical Condition", Utils.getCsvFromArrayList(localDataModel.getConditions()), pdfBody);
        addRow("Test Date", Utils.getFormattedDate(Calendar.getInstance()), pdfBody);
        addRow("Test Method", "Rapid Antigen Test", pdfBody);
        //        String result = new String[] {"Negative", "Positive", "Inconclusive",
        //              "Negative", "Positive", "Inconclusive",
        //              "Negative", "Positive", "Inconclusive"}[(int)(Math.random()*9)];
        addRow("Test Result", getIntent().getExtras().getString("result"), pdfBody);

        Bitmap cassetteImage = BitmapFactory.decodeFile(imageToUpload);
        PDFImageView imageView = new PDFImageView(getApplicationContext());
        LinearLayout.LayoutParams imageLayoutParam = new LinearLayout.LayoutParams(
                100,
                100, 0);
        imageLayoutParam.setMargins(0, 10, 0, 10);
        imageView.setImageScale(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setImageBitmap(cassetteImage);
        imageLayoutParam.setMargins(0, 10, 0, 10);
        imageView.setLayout(imageLayoutParam);
        pdfBody.addView(imageView);

        PDFTextView pdfNoteView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
        pdfNoteView.setText("Remarks:\n" +
                "1. All individuals who test positive by Rapid Antigen Test may be considered as True Positive. All such individuals are advised to follow home isolation & care as per government guidelines.\n\n" +
                "2. If any individual with symptoms test negative by Rapid Antigen test, they should immediately get tested by RT-PCR. Such individuals should also considered themselves as suspect cases of Covid-19 and follow home isolation protocol while awaiting the RT-PCR results.\n\n" +
                "3. Rapid Antigen tests are highly specific but their sensitivity depends on the viral load present in different individuals, proper sample collection & extraction. Ultra Covi-Catch has undergone strict quality control procedures to provide high accuracy of the test. However, the product is used outside the control of the manufacturing company & its channel partners. Moreever, the result may be affected due to environmental factors or human error, in such case, the company or its channel partners will not be liable for any losses or costs whether direct or indirect arising out of incorrect diagnosis.");
        pdfBody.addView(pdfNoteView);

        return pdfBody;
    }

    private void addBlueSeparator(PDFBody pdfBody) {
        PDFLineSeparatorView lineSeparator = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(getResources().getColor(R.color.app_blue));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                20, 0);
        lp.setMargins(60, 0, 60, 0);
        lineSeparator.setLayout(lp);

        pdfBody.addView(lineSeparator);
    }

    private void addRow(String key, String value, PDFBody pdfBody) {
        PDFHorizontalView horizontalView = new PDFHorizontalView(getApplicationContext());

        PDFLineSeparatorView lineSeparator = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.BLACK);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                1,
                20, 0);
        lp.setMargins(60, 0, 0, 0);
        lineSeparator.setLayout(lp);
        horizontalView.addView(lineSeparator);

        PDFTextView keyTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
        keyTextView.setText("" + key);
        LinearLayout.LayoutParams lpKey= new LinearLayout.LayoutParams(
                100,
                20, 0);
        lpKey.setMargins(10, 0, 0, 0);
        keyTextView.setLayout(lpKey);
        horizontalView.addView(keyTextView);

        PDFLineSeparatorView lineSeparator2 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.BLACK);
        LinearLayout.LayoutParams lp2= new LinearLayout.LayoutParams(
                1,
                20, 0);
        lp2.setMargins(0, 0, 0, 0);
        lineSeparator2.setLayout(lp2);
        horizontalView.addView(lineSeparator2);

        PDFTextView valueTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
        valueTextView.setText("" + value);
        LinearLayout.LayoutParams lpValue= new LinearLayout.LayoutParams(
                0,
                20, 3);
        lpValue.setMargins(10, 0, 0, 0);
        valueTextView.setLayout(lpValue);
        horizontalView.addView(valueTextView);

        PDFLineSeparatorView lineSeparator3 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.BLACK);
        LinearLayout.LayoutParams lp3= new LinearLayout.LayoutParams(
                1,
                20, 0);
        lp3.setMargins(0, 0, 60, 0);
        lineSeparator3.setLayout(lp3);
        horizontalView.addView(lineSeparator3);

        pdfBody.addView(horizontalView);

        PDFLineSeparatorView lineSeparator4 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.BLACK);
        LinearLayout.LayoutParams lp4 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1, 0);
        lp4.setMargins(60, 0, 60, 0);
        lineSeparator4.setLayout(lp4);

        pdfBody.addView(lineSeparator4);

    }

    @Override
    protected PDFFooterView getFooterView(int pageIndex) {
        PDFFooterView footerView = new PDFFooterView(getApplicationContext());
        PDFImageView imageView = new PDFImageView(getApplicationContext());
        LinearLayout.LayoutParams imageLayoutParam = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                40, 0);
        imageLayoutParam.gravity = Gravity.CENTER;
        imageView.setImageScale(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setImageResource(R.drawable.img_pdf_footer);
        imageLayoutParam.setMargins(0, 0, 10, 0);
        imageView.setLayout(imageLayoutParam);
        footerView.addView(imageView);
        return footerView;
    }

    @Nullable
    @Override
    protected PDFImageView getWatermarkView(int forPage) {
//        PDFImageView pdfImageView = new PDFImageView(getApplicationContext());
//        FrameLayout.LayoutParams childLayoutParams = new FrameLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                200, Gravity.CENTER);
//        pdfImageView.setLayout(childLayoutParams);
//
//        pdfImageView.setImageResource(R.drawable.img_app_logo);
//        pdfImageView.setImageScale(ImageView.ScaleType.FIT_CENTER);
//        pdfImageView.getView().setAlpha(0.3F);
//
//        return pdfImageView;
        return null;
    }

    @Override
    protected void onNextClicked(final File savedPDFFile) {
        finish();
    }
}