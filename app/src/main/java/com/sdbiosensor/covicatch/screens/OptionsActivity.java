package com.sdbiosensor.covicatch.screens;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sdbiosensor.covicatch.R;
import com.sdbiosensor.covicatch.adapters.ExistingUsersDialogAdapter;
import com.sdbiosensor.covicatch.constants.Constants;
import com.sdbiosensor.covicatch.customcomoponents.BaseActivity;
import com.sdbiosensor.covicatch.events.CloseAllScreens;
import com.sdbiosensor.covicatch.network.ApiClient;
import com.sdbiosensor.covicatch.network.models.CreatePatientRequestModel;
import com.sdbiosensor.covicatch.network.models.GetProfileResponseModel;
import com.sdbiosensor.covicatch.utils.SharedPrefUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OptionsActivity extends BaseActivity implements View.OnClickListener{

    private int USER_THRESHOLD = 5;        //Default
    private View layout_existing_users;
    private TextView edit_existing_users;
    private View progress;
    private ArrayList<CreatePatientRequestModel> existingUsersList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        initView();
        handleClicks();
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchUserProfiles();
    }

    private void initView() {
        layout_existing_users = findViewById(R.id.layout_existing_users);
        edit_existing_users = findViewById(R.id.edit_existing_users);
        progress = findViewById(R.id.progress);

        try {
            USER_THRESHOLD = Integer.parseInt(SharedPrefUtils.getInstance(this).getString(Constants.PREF_PROFILE_THRESHOLD, "" + USER_THRESHOLD));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleClicks() {
        findViewById(R.id.button_form).setOnClickListener(this);
        findViewById(R.id.button_history).setOnClickListener(this);
        findViewById(R.id.button_logout).setOnClickListener(this);
    }

    private void fetchUserProfiles() {
        progress.setVisibility(View.VISIBLE);
        if (ApiClient.getBaseInstance(this) != null) {
            ApiClient.getBaseInstance(this).getProfiles().enqueue(new Callback<GetProfileResponseModel>() {
                @Override
                public void onResponse(Call<GetProfileResponseModel> call, Response<GetProfileResponseModel> response) {
                    progress.setVisibility(View.GONE);
                    if (response.errorBody() == null) {
                        handleProfileResponse(response);
                    } else {
                        //Do nothing
                        Toast.makeText(OptionsActivity.this, "Unkown error, logout and relogin again.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<GetProfileResponseModel> call, Throwable t) {
                    progress.setVisibility(View.GONE);
                    //Do nothing
                }
            });
        }
    }

    private void handleProfileResponse(Response<GetProfileResponseModel> response) {
        if (response.body() != null && response.body().getStatus().equalsIgnoreCase("SUCCESS")) {
            if (response.body().getData() != null) {
                ArrayList<CreatePatientRequestModel> list = response.body().getData();
                handleExistingUsers(list);
            } else {
                layout_existing_users.setVisibility(View.GONE);
            }
        } else {
            layout_existing_users.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_form) {
            openFormActivity();
        } else if (view.getId() == R.id.button_history) {
            openHistoryActivity();
        } else if (view.getId() == R.id.button_logout) {
            confirmLogout();
        } else if (view.getId() == R.id.edit_existing_users) {
            openExistingUsersDialog();
        }
    }

    private void openExistingUsersDialog() {
        if (existingUsersList == null || existingUsersList.isEmpty()) {
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialogView = inflater.inflate(R.layout.dialog_string_selection, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setView(dialogView);

        RecyclerView dialogRecyclerView = dialogView.findViewById(R.id.recyclerView);
        dialogRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dialogRecyclerView.setAdapter(new ExistingUsersDialogAdapter(this, existingUsersList, new ExistingUsersDialogAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CreatePatientRequestModel item, int position) {
                alertDialog.dismiss();
                openFormProfileActivity(item);
            }
        }));

        dialogView.findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void openFormActivity() {
        startActivity(new Intent(OptionsActivity.this, AgreementActivity.class));
        finish();
    }

    private void openHistoryActivity() {
        startActivity(new Intent(OptionsActivity.this, HistoryActivity.class));
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.logout))
                .setMessage(getString(R.string.logout_message))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        logout();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }

    private void handleExistingUsers(ArrayList<CreatePatientRequestModel> list) {
        layout_existing_users.setVisibility(list.isEmpty() ? View.GONE : View.VISIBLE);
        if (list.size() >= USER_THRESHOLD) {
            findViewById(R.id.button_form).setVisibility(View.GONE);
        }

        edit_existing_users.setOnClickListener(this);
        this.existingUsersList = list;
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(new ExistingUsersRecyclerAdapter(this, list, new ExistingUsersRecyclerAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(CreatePatientRequestModel item, int positon) {
//                openFormProfileActivity(item);
//            }
//        }));
    }

    private void openFormProfileActivity(CreatePatientRequestModel item) {
        Intent intent = new Intent(OptionsActivity.this, AgreementActivity.class);
        intent.putExtra("user", item);
        startActivity(intent);
        finish();
    }

    private void logout() {
        SharedPrefUtils.getInstance(this).resetAll();
        EventBus.getDefault().post(new CloseAllScreens());
        startActivity(new Intent(getApplicationContext(), SplashActivity.class));
    }

}