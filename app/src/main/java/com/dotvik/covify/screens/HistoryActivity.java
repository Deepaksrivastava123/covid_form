package com.dotvik.covify.screens;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.dotvik.covify.R;
import com.dotvik.covify.adapters.HistoryRecyclerAdapter;
import com.dotvik.covify.constants.Constants;
import com.dotvik.covify.customcomoponents.BaseActivity;
import com.dotvik.covify.network.ApiClient;
import com.dotvik.covify.network.models.GetHistoryResponseModel;
import com.dotvik.covify.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dotvik.covify.screens.FormActivity.CAMERA_PERMISSIONS_CODE;

public class HistoryActivity extends BaseActivity{

    private RecyclerView recyclerView;
    private View progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initView();
        fetchHistory();
    }

    private void initView() {
        recyclerView = findViewById(R.id.recyclerView);
        progress = findViewById(R.id.progress);
    }

    private void fetchHistory() {
        progress.setVisibility(View.VISIBLE);
        if (ApiClient.getBaseInstance(this) != null) {
            ApiClient.getBaseInstance(this).getPatientHistory(new JsonObject()).enqueue(new Callback<GetHistoryResponseModel>() {
                @Override
                public void onResponse(Call<GetHistoryResponseModel> call, Response<GetHistoryResponseModel> response) {
                    progress.setVisibility(View.GONE);
                    if (response.errorBody() == null) {
                        handleHistoryResponse(response);
                    } else {
                        //Do nothing
                    }
                }

                @Override
                public void onFailure(Call<GetHistoryResponseModel> call, Throwable t) {
                    progress.setVisibility(View.GONE);
                    //Do nothing
                }
            });
        }
    }

    private void handleHistoryResponse(Response<GetHistoryResponseModel> response) {
        if (response.body() != null && response.body().getStatus().equalsIgnoreCase("SUCCESS")) {

            if (response.body().getData() != null) {
                ArrayList<GetHistoryResponseModel.DataModel> list = response.body().getData();
                if (!list.isEmpty()) {
                    handleHistory(list);
                }
            }
        }
    }

    private void handleHistory(ArrayList<GetHistoryResponseModel.DataModel> list) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new HistoryRecyclerAdapter(this, list, new HistoryRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(GetHistoryResponseModel.DataModel item, int positon) {
                checkPermissions(item.getId());
            }
        }));
    }

    private void checkPermissions(String id) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // check again permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        CAMERA_PERMISSIONS_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        CAMERA_PERMISSIONS_CODE);
                // Grant Permission
            }
        } else {
            openDownloadUrl(id);
        }
    }

    private void openDownloadUrl(String uniqueId) {
        progress.setVisibility(View.VISIBLE);
        if (ApiClient.getBaseInstance(this) != null) {
            ApiClient.getBaseInstance(this).downloadFile(Constants.CATEGORIES.TESTING_RESULT_PDF.name(), uniqueId).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.errorBody() == null) {
                        handleDownloadResponse(response);
                    } else {
                        progress.setVisibility(View.GONE);
                        showDialog(getString(R.string.error_server_error));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progress.setVisibility(View.GONE);
                    showDialog(getString(R.string.error_server_error));
                }
            });
        }

//        String url = Constants.BASE_URL + "/api/files/download?category=TESTING_RESULT_PDF&uniqueId=" + uniqueId;
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//        Bundle bundle = new Bundle();
//        bundle.putString("Authorization", "Bearer " + SharedPrefUtils.getInstance(this).getString(Constants.PREF_LOGGED_IN_TOKEN, ""));
//        intent.putExtra(Browser.EXTRA_HEADERS, bundle);
//        startActivity(intent);
    }

    private void handleDownloadResponse(Response<ResponseBody> response) {
        ResponseBody body = response.body();
        File downloadsPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadsPath, "COVI-CATCH-" + Utils.getFormattedDateTime(Calendar.getInstance()) + ".pdf");

        InputStream in = null;
        FileOutputStream out = null;
        try {
            try {
                in = body.byteStream();
                out = new FileOutputStream(file);
                int c;
                while ((c = in.read()) != -1) {
                    out.write(c);
                }
                showDialog("Result PDF saved to Downloads");
            }
            catch (IOException e) {
                e.printStackTrace();
                showDialog(getString(R.string.error_server_error));
            }
            finally {
                if (in != null) {
                    in.close();

                }
                if (out != null) {
                    out.flush();
                    out.getFD().sync();
                    out.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showDialog(getString(R.string.error_server_error));
        }
        progress.setVisibility(View.GONE);
    }

}