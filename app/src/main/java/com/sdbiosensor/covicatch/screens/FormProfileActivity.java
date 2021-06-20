package com.sdbiosensor.covicatch.screens;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.sdbiosensor.covicatch.R;
import com.sdbiosensor.covicatch.adapters.DistrictRecyclerAdapter;
import com.sdbiosensor.covicatch.adapters.JsonArrayRecyclerAdapter;
import com.sdbiosensor.covicatch.adapters.MultiRecyclerAdapter;
import com.sdbiosensor.covicatch.adapters.StateRecyclerAdapter;
import com.sdbiosensor.covicatch.adapters.StringRecyclerAdapter;
import com.sdbiosensor.covicatch.constants.Constants;
import com.sdbiosensor.covicatch.customcomoponents.BaseActivity;
import com.sdbiosensor.covicatch.network.ApiClient;
import com.sdbiosensor.covicatch.network.models.CreatePatientRequestModel;
import com.sdbiosensor.covicatch.network.models.GenericResponseModel;
import com.sdbiosensor.covicatch.network.models.LocalDataModel;
import com.sdbiosensor.covicatch.utils.SharedPrefUtils;
import com.sdbiosensor.covicatch.utils.Utils;
import com.sdbiosensor.covicatch.utils.ValidationUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormProfileActivity extends BaseActivity implements View.OnClickListener{

    private TextView edit_mobile, edit_address, edit_pincode, edit_id_no,
            edit_city, edit_first_name, edit_last_name, edit_gender, edit_state, edit_district, edit_id_type, edit_symptoms,
            edit_conditions, edit_nationality, edit_dob, edit_occupation, edit_contact_number_belongs,
            edit_vaccinated, edit_vaccine;
    private View progress, layout_vaccine;
    private int selectedGender = -1, selectedIdType = -1;
    private JSONArray stateMaster, stateDistrictMaster, selectedStateDistrictMaster, nationalityList;
    private ArrayList<String> selectedSymptoms = new ArrayList<>(), selectedConditions = new ArrayList<>();
    private String otherSymptoms, otherConditions;
    private String selectedStateId, selectedDistrictId, verifiedMobileNumber = "";
    private Calendar dobCalendar = Calendar.getInstance();
    private boolean hasOTPVerified = false;
    private CreatePatientRequestModel existingUser;

    static final int CAMERA_PERMISSIONS_CODE  = 1001;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_profile);

        initView();
        setDefaultValues();
        handleClicks();
        initJsonArray();
    }

    private void initView() {
        edit_first_name = findViewById(R.id.edit_first_name);
        edit_last_name = findViewById(R.id.edit_last_name);
        edit_mobile = findViewById(R.id.edit_mobile_number);
        edit_address = findViewById(R.id.edit_address);
        edit_pincode = findViewById(R.id.edit_pin_code);
        edit_gender = findViewById(R.id.edit_gender);
        edit_state = findViewById(R.id.edit_state);
        edit_district = findViewById(R.id.edit_district);
        edit_id_type = findViewById(R.id.edit_id_type);
        edit_id_no = findViewById(R.id.edit_id_no);
        edit_symptoms = findViewById(R.id.edit_symptoms);
        edit_conditions = findViewById(R.id.edit_conditions);
        edit_nationality = findViewById(R.id.edit_nationality);
        edit_nationality = findViewById(R.id.edit_nationality);
        edit_dob = findViewById(R.id.edit_dob);
        edit_occupation = findViewById(R.id.edit_occupation);
        edit_city = findViewById(R.id.edit_city);
        edit_contact_number_belongs = findViewById(R.id.edit_contact_number_belongs);
        edit_vaccinated = findViewById(R.id.edit_vaccinated);
        edit_vaccine = findViewById(R.id.edit_vaccine);
        progress = findViewById(R.id.progress);
        layout_vaccine = findViewById(R.id.layout_vaccine);

        //handleMobileEditText();
    }

    private void setDefaultValues() {
        try {
            existingUser = (CreatePatientRequestModel) getIntent().getSerializableExtra("user");
            edit_first_name.setText(existingUser.getFirstName());
            edit_last_name.setText(existingUser.getLastName());
            edit_dob.setText(existingUser.getDob());
            edit_gender.setText(existingUser.getGender());
            if (existingUser.getGender().equals(Constants.GENDER.MALE.name())) {
                selectedGender = 0;
            } else  if (existingUser.getGender().equals(Constants.GENDER.FEMALE.name())) {
                selectedGender = 1;
            } else {
                selectedGender = 2;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date date = sdf.parse(existingUser.getDob());
            dobCalendar.setTime(date);

            edit_nationality.setText(existingUser.getNationality());
            edit_contact_number_belongs.setText(existingUser.getContactNumberBelongsTo());
            edit_state.setText(existingUser.getState());
            selectedStateId = existingUser.getStateCode();
            edit_district.setText(existingUser.getDistrict());
            selectedDistrictId = existingUser.getDistrictCode();

            ArrayList<String> idTypeList = new ArrayList<String>();
            idTypeList.add(getString(R.string.aadhar_card));
            idTypeList.add(getString(R.string.driving_license));
            idTypeList.add(getString(R.string.pan_card));
            idTypeList.add(getString(R.string.voter_id_card));
            idTypeList.add(getString(R.string.passport));
            for (int i = 0; i < idTypeList.size(); i++) {
                if (existingUser.getIdType().equalsIgnoreCase(idTypeList.get(i))) {
                    selectedIdType = i;
                    break;
                }
            }
            edit_id_type.setText(existingUser.getIdType());
            edit_occupation.setText(existingUser.getOccupation());
            edit_mobile.setText(existingUser.getMobileNo());
            edit_address.setText(existingUser.getAddress().getAddress1());
            edit_pincode.setText(existingUser.getPinCode());
            edit_city.setText(existingUser.getCity());
            edit_id_no.setText(existingUser.getUserIdNo());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //TODO fill other fields if required
    }

    private void handleMobileEditText() {
        edit_mobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    sendOtp(edit_mobile.getText().toString().trim());
                }
            }
        });
    }

    private void handleClicks() {
        findViewById(R.id.button_next).setOnClickListener(this);
        edit_symptoms.setOnClickListener(this);
        edit_conditions.setOnClickListener(this);
        edit_vaccinated.setOnClickListener(this);
        edit_vaccine.setOnClickListener(this);
    }

    private void initJsonArray() {
        try {
            stateMaster = Utils.stateMasterJson(this);
            stateDistrictMaster = Utils.stateDistrictJson(this);
            nationalityList = Utils.nationalityJson(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createSelectedStateDistrictArray(String state) {
        try {
            selectedStateDistrictMaster = new JSONArray();
            for (int i = 0; i < stateDistrictMaster.length(); i++) {
                JSONObject item = stateDistrictMaster.getJSONObject(i);
                if (item.getString("State").equalsIgnoreCase(state)) {
                    selectedStateDistrictMaster.put(item);
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.edit_gender) {
            openChooseGenderDialog();
        } else if (view.getId() == R.id.edit_state) {
            openChooseStateDialog();
        } else if (view.getId() == R.id.edit_district) {
            openChooseDistrictDialog();
        } else if (view.getId() == R.id.edit_id_type) {
            openChooseIdTypeDialog();
        } else if (view.getId() == R.id.edit_symptoms) {
            openChooseSymptomsDialog();
        } else if (view.getId() == R.id.edit_conditions) {
            openChooseConditionsDialog();
        } else if (view.getId() == R.id.edit_nationality) {
            openChooseNationalityDialog();
        } else if (view.getId() == R.id.edit_dob) {
            openDobDialog();
        } else if (view.getId() == R.id.edit_occupation) {
            openChooseOccupationDialog();
        } else if (view.getId() == R.id.edit_contact_number_belongs) {
            openChooseContactBelongsDialog();
        } else if (view.getId() == R.id.edit_vaccinated) {
            openVaccinatedDialog();
        } else if (view.getId() == R.id.edit_vaccine) {
            openVaccineDialog();
        } else {
            Utils.hideKeyboard(this);
            validateForm();
        }
    }

    private void openChooseGenderDialog() {
        ArrayList<String> genderList = new ArrayList<String>();
        genderList.add(getString(R.string.male));
        genderList.add(getString(R.string.female));
        genderList.add(getString(R.string.others));

        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialogView = inflater.inflate(R.layout.dialog_string_selection, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setView(dialogView);

        RecyclerView dialogRecyclerView = dialogView.findViewById(R.id.recyclerView);
        dialogRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dialogRecyclerView.setAdapter(new StringRecyclerAdapter(this, genderList, new StringRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item, int position) {
                selectedGender = position;
                edit_gender.setText(item);
                alertDialog.dismiss();
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

    private void openChooseStateDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialogView = inflater.inflate(R.layout.dialog_string_selection, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setView(dialogView);

        RecyclerView dialogRecyclerView = dialogView.findViewById(R.id.recyclerView);
        dialogRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dialogRecyclerView.setAdapter(new StateRecyclerAdapter(this, stateMaster, new StateRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(JSONObject item, int position) {
                try {
                    edit_state.setText(item.getString("State"));
                    selectedStateId = item.getString("State Code");
                    createSelectedStateDistrictArray(item.getString("State"));
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
                edit_district.setText("");
                alertDialog.dismiss();
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

    private void openChooseDistrictDialog() {
        if (edit_state.getText().toString().isEmpty()) {
            showErrorDialog(getResources().getString(R.string.error_select_valid_state));
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialogView = inflater.inflate(R.layout.dialog_string_selection, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setView(dialogView);

        RecyclerView dialogRecyclerView = dialogView.findViewById(R.id.recyclerView);
        dialogRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dialogRecyclerView.setAdapter(new DistrictRecyclerAdapter(this, selectedStateDistrictMaster, new DistrictRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(JSONObject item, int position) {
                try {
                    edit_district.setText(item.getString("District"));
                    selectedDistrictId = item.getString("District Code");
                    alertDialog.dismiss();
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
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

    private void openChooseNationalityDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialogView = inflater.inflate(R.layout.dialog_string_selection, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setView(dialogView);

        RecyclerView dialogRecyclerView = dialogView.findViewById(R.id.recyclerView);
        dialogRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dialogRecyclerView.setAdapter(new JsonArrayRecyclerAdapter(this, nationalityList, new JsonArrayRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item, int position) {
                edit_nationality.setText(item);
                if (!item.equalsIgnoreCase("Indian")) {
                    selectedIdType = 4;
                    edit_id_type.setText(getString(R.string.passport));
                    edit_id_type.setOnClickListener(null);
                } else {
                    edit_id_type.setOnClickListener(FormProfileActivity.this::onClick);
                }
                alertDialog.dismiss();
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

    private void openDobDialog() {
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dobCalendar.set(year, monthOfYear, dayOfMonth);
                edit_dob.setText(Utils.getFormattedDate(dobCalendar));
            }
        }, dobCalendar.get(Calendar.YEAR), dobCalendar.get(Calendar.MONTH), dobCalendar.get(Calendar.DATE)).show();
    }

    private void openChooseOccupationDialog() {
        ArrayList<String> genderList = new ArrayList<String>();
        genderList.add(Constants.OCCUPATION.HCW.name());
        genderList.add(Constants.OCCUPATION.POLICE.name());
        genderList.add(Constants.OCCUPATION.SNTN.name());
        genderList.add(Constants.OCCUPATION.SECG.name());
        genderList.add(Constants.OCCUPATION.OTHER.name());

        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialogView = inflater.inflate(R.layout.dialog_string_selection, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setView(dialogView);

        RecyclerView dialogRecyclerView = dialogView.findViewById(R.id.recyclerView);
        dialogRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dialogRecyclerView.setAdapter(new StringRecyclerAdapter(this, genderList, new StringRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item, int position) {
                edit_occupation.setText(item);
                alertDialog.dismiss();
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

    private void openChooseContactBelongsDialog() {
        ArrayList<String> genderList = new ArrayList<String>();
        genderList.add(Constants.CONTACT_BELONGS.PATIENT.name());
        genderList.add(Constants.CONTACT_BELONGS.RELATIVE.name());

        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialogView = inflater.inflate(R.layout.dialog_string_selection, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setView(dialogView);

        RecyclerView dialogRecyclerView = dialogView.findViewById(R.id.recyclerView);
        dialogRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dialogRecyclerView.setAdapter(new StringRecyclerAdapter(this, genderList, new StringRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item, int position) {
                edit_contact_number_belongs.setText(item);
                alertDialog.dismiss();
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

    private void openVaccinatedDialog() {
        ArrayList<String> genderList = new ArrayList<String>();
        genderList.add(Constants.VACCINATED.YES.name());
        genderList.add(Constants.VACCINATED.NO.name());

        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialogView = inflater.inflate(R.layout.dialog_string_selection, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setView(dialogView);

        RecyclerView dialogRecyclerView = dialogView.findViewById(R.id.recyclerView);
        dialogRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dialogRecyclerView.setAdapter(new StringRecyclerAdapter(this, genderList, new StringRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item, int position) {
                edit_vaccinated.setText(item);
                if (position == 0) {
                    layout_vaccine.setVisibility(View.VISIBLE);
                    edit_vaccine.requestFocus();
                } else {
                    layout_vaccine.setVisibility(View.GONE);
                }
                alertDialog.dismiss();
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

    private void openVaccineDialog() {
        ArrayList<String> genderList = new ArrayList<String>();
        genderList.add(Constants.VACCINE.COVISHIELD.name());
        genderList.add(Constants.VACCINE.COVAXIN.name());
        genderList.add(Constants.VACCINE.SPUTNIK.name());

        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialogView = inflater.inflate(R.layout.dialog_string_selection, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setView(dialogView);

        RecyclerView dialogRecyclerView = dialogView.findViewById(R.id.recyclerView);
        dialogRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dialogRecyclerView.setAdapter(new StringRecyclerAdapter(this, genderList, new StringRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item, int position) {
                edit_vaccine.setText(item);
                alertDialog.dismiss();
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

    private void openChooseIdTypeDialog() {
        ArrayList<String> genderList = new ArrayList<String>();
        genderList.add(getString(R.string.aadhar_card));
        genderList.add(getString(R.string.driving_license));
        genderList.add(getString(R.string.pan_card));
        genderList.add(getString(R.string.voter_id_card));
        genderList.add(getString(R.string.passport));

        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialogView = inflater.inflate(R.layout.dialog_string_selection, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setView(dialogView);

        RecyclerView dialogRecyclerView = dialogView.findViewById(R.id.recyclerView);
        dialogRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dialogRecyclerView.setAdapter(new StringRecyclerAdapter(this, genderList, new StringRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item, int position) {
                selectedIdType = position;
                edit_id_type.setText(item);
                alertDialog.dismiss();
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

    private void openChooseSymptomsDialog() {
        ArrayList<String> tempSelectedList = new ArrayList<>();

        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialogView = inflater.inflate(R.layout.dialog_multi_selection, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setView(dialogView);

        EditText editOthers = dialogView.findViewById(R.id.edit_others);

        if (selectedSymptoms.contains("Others")) {
            editOthers.setText(otherSymptoms);
            editOthers.setVisibility(View.VISIBLE);
        }

        RecyclerView dialogRecyclerView = dialogView.findViewById(R.id.recyclerView);
        dialogRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dialogRecyclerView.setAdapter(new MultiRecyclerAdapter(this, Constants.SYMPTOMS_LIST, selectedSymptoms, new MultiRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemCheckChange(String item, int position, boolean isSelected) {
                if (isSelected) {
                    tempSelectedList.add(item);
                } else {
                    tempSelectedList.remove(item);
                }

                if (item.equals("Others")) {
                    if (isSelected) {
                        editOthers.setVisibility(View.VISIBLE);
                    } else {
                        editOthers.setVisibility(View.GONE);
                    }
                }
            }
        }));

        dialogView.findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedSymptoms = tempSelectedList;
                if (tempSelectedList.contains("Others")) {
                    otherSymptoms = editOthers.getText().toString();
                }
                edit_symptoms.setText(Utils.getCsvFromArrayList(selectedSymptoms));
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void openChooseConditionsDialog() {
        ArrayList<String> tempSelectedList = new ArrayList<>();

        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialogView = inflater.inflate(R.layout.dialog_multi_selection, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setView(dialogView);

        EditText editOthers = dialogView.findViewById(R.id.edit_others);

        if (selectedConditions.contains("Others")) {
            editOthers.setText(otherConditions);
            editOthers.setVisibility(View.VISIBLE);
        }

        RecyclerView dialogRecyclerView = dialogView.findViewById(R.id.recyclerView);
        dialogRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dialogRecyclerView.setAdapter(new MultiRecyclerAdapter(this, Constants.CONDITIONS_LIST, selectedConditions, new MultiRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemCheckChange(String item, int position, boolean isSelected) {
                if (isSelected) {
                    tempSelectedList.add(item);
                } else {
                    tempSelectedList.remove(item);
                }

                if (item.equals("Others")) {
                    if (isSelected) {
                        editOthers.setVisibility(View.VISIBLE);
                    } else {
                        editOthers.setVisibility(View.GONE);
                    }
                }
            }
        }));

        dialogView.findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedConditions = tempSelectedList;
                if (tempSelectedList.contains("Others")) {
                    otherConditions = editOthers.getText().toString();
                }
                edit_conditions.setText(Utils.getCsvFromArrayList(selectedConditions));
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void validateForm() {
        if (!ValidationUtils.blankValidation(edit_first_name) ||
                !ValidationUtils.blankValidation(edit_last_name) ||
                !ValidationUtils.blankValidation(edit_gender) ||
                !ValidationUtils.blankValidation(edit_dob) ||
                !ValidationUtils.blankValidation(edit_nationality) ||
                !ValidationUtils.blankValidation(edit_mobile) ||
                !ValidationUtils.blankValidation(edit_contact_number_belongs) ||
                !ValidationUtils.blankValidation(edit_address) ||
                !ValidationUtils.blankValidation(edit_pincode) ||
                !ValidationUtils.blankValidation(edit_state) ||
                !ValidationUtils.blankValidation(edit_district) ||
                !ValidationUtils.blankValidation(edit_city) ||
                !ValidationUtils.blankValidation(edit_id_type) ||
                !ValidationUtils.blankValidation(edit_id_no) ||
                !ValidationUtils.blankValidation(edit_occupation) ||
                !ValidationUtils.blankValidation(edit_vaccinated) ||
                !ValidationUtils.blankValidation(edit_symptoms) ||
                !ValidationUtils.blankValidation(edit_conditions)) {
            return;
        }

        if (edit_vaccinated.getText().toString().equals(Constants.VACCINATED.YES.name()) &&
                !ValidationUtils.blankValidation(edit_vaccine)) {
            return;
        }

        scanQr();
    }

    private void sendOtp(String mobileNo) {
        if(hasOTPVerified && verifiedMobileNumber.equals(mobileNo)) return;

        if (mobileNo.isEmpty() || mobileNo.length() < 10) {
            edit_mobile.setText("");
            showErrorDialog(getString(R.string.error_valid_mobile_number));
            return;
        }
        progress.setVisibility(View.VISIBLE);
        if (ApiClient.getBaseInstance(this) != null) {
            ApiClient.getBaseInstance(this).sendOtp(mobileNo).enqueue(new Callback<GenericResponseModel>() {
                @Override
                public void onResponse(Call<GenericResponseModel> call, Response<GenericResponseModel> response) {
                    progress.setVisibility(View.GONE);
                    if (response.errorBody() == null) {
                        showOtpDialog(response, mobileNo);
                    } else {
                        try {
                            edit_mobile.setText("");
                            showErrorDialog(getString(R.string.error_server_error));
                        } catch (Exception e) {
                            e.printStackTrace();
                            edit_mobile.setText("");
                            showErrorDialog(response.errorBody().toString());
                        }
                    }
                }

                @Override
                public void onFailure(Call<GenericResponseModel> call, Throwable t) {
                    progress.setVisibility(View.GONE);
                    Log.v("Debug", t.getLocalizedMessage());
                    edit_mobile.setText("");
                    showErrorDialog(t.getLocalizedMessage());
                }
            });
        }
    }

    private void showOtpDialog(Response<GenericResponseModel> response, String mobileNo) {
        LinearLayout lin = new LinearLayout(this);
        lin.setPadding(50, 0, 50, 0);
        final EditText editText = new EditText(this);
        editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        lin.addView(editText);

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle(R.string.otp_sent);
        builder.setMessage(R.string.otp_sent_message);
        builder.setCancelable(false);
        builder.setView(lin);
        builder.setPositiveButton(R.string.ok, null);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                edit_mobile.setText("");
                dialog.cancel();
            }
        });

        final androidx.appcompat.app.AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            public void onShow(DialogInterface dialogInterface) {

                Button button = ((androidx.appcompat.app.AlertDialog) dialog).getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String otp = editText.getText().toString();
                        if (!otp.trim().isEmpty()) {
                            verifyOtp(otp, mobileNo);
                            dialog.cancel();
                        } else {
                            editText.setError(getString(R.string.error_text_blank));
                        }
                    }
                });
            }
        });

        dialog.show();
    }

    private void verifyOtp(String otp, String mobileNo) {
        progress.setVisibility(View.VISIBLE);
        if (ApiClient.getBaseInstance(this) != null) {
            ApiClient.getBaseInstance(this).verifyOtp(mobileNo, otp).enqueue(new Callback<GenericResponseModel>() {
                @Override
                public void onResponse(Call<GenericResponseModel> call, Response<GenericResponseModel> response) {
                    progress.setVisibility(View.GONE);
                    if (response.errorBody() == null) {
                        handleOtpVerifyResponse(mobileNo, response.body());
                    } else {
                        edit_mobile.setText("");
                        showErrorDialog(getString(R.string.error_server_error));
                    }
                }

                @Override
                public void onFailure(Call<GenericResponseModel> call, Throwable t) {
                    progress.setVisibility(View.GONE);
                    Log.v("Debug", t.getLocalizedMessage());
                    edit_mobile.setText("");
                    showErrorDialog(t.getLocalizedMessage());
                }
            });
        }
    }

    private void handleOtpVerifyResponse(String mobileNo, GenericResponseModel response) {
        if(response.getStatus().equalsIgnoreCase("SUCCESS")) {
            edit_contact_number_belongs.requestFocus();
            Utils.hideKeyboard(this);
            hasOTPVerified = true;
            verifiedMobileNumber = mobileNo;
            showDialog(response.getMessage());
        } else {
            edit_mobile.setText("");
            showErrorDialog(response.getMessage());
        }
    }

    private void scanQr() {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                // check again permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                                , Manifest.permission.CAMERA
                                , Manifest.permission.MANAGE_EXTERNAL_STORAGE},
                        CAMERA_PERMISSIONS_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE
                                , Manifest.permission.CAMERA
                                , Manifest.permission.MANAGE_EXTERNAL_STORAGE},
                        CAMERA_PERMISSIONS_CODE);
                // Grant Permission
            }
        } else {

            new IntentIntegrator(this).initiateScan();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                showErrorDialog("Cancelled");
            } else {
                saveLocalModel(result.getContents());
                startActivity(new Intent(FormProfileActivity.this, InstructionActivity.class));
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void saveLocalModel(String qrCode) {
        LocalDataModel model = new LocalDataModel();
        model.setQrCode(qrCode);
        model.setNationality(edit_nationality.getText().toString().trim());
        model.setDob(edit_dob.getText().toString().trim());
        model.setFirstName(edit_first_name.getText().toString().trim());
        model.setLastName(edit_last_name.getText().toString().trim());
        model.setMobile(edit_mobile.getText().toString().trim());
        model.setContactNumberBelongsTo(edit_contact_number_belongs.getText().toString().trim());
        model.setAddress(edit_address.getText().toString().trim());
        model.setOccupation(edit_occupation.getText().toString().trim());
        model.setPincode(edit_pincode.getText().toString().trim());
        switch (selectedGender) {
            case 0:
                model.setGender(Constants.GENDER.MALE.name());
                break;
            case 1:
                model.setGender(Constants.GENDER.FEMALE.name());
                break;
            case 2:
                model.setGender(Constants.GENDER.OTHERS.name());
                break;
            default:
                edit_gender.setError(getString(R.string.error_text_blank));
                break;
        }
        model.setState(edit_state.getText().toString().trim());
        model.setStateId(selectedStateId);
        model.setDistrict(edit_district.getText().toString().trim());
        model.setDistrictId(selectedDistrictId);
        model.setCity(edit_city.getText().toString().trim());
        switch (selectedIdType) {
            case 0:
                model.setId_type(Constants.ID_TYPE.AADHAR_CARD.name());
                break;
            case 1:
                model.setId_type(Constants.ID_TYPE.DRIVING_LICENSE.name());
                break;
            case 2:
                model.setId_type(Constants.ID_TYPE.PAN_CARD.name());
                break;
            case 3:
                model.setId_type(Constants.ID_TYPE.VOTER_ID_CARD.name());
                break;
            case 4:
                model.setId_type(Constants.ID_TYPE.PASSPORT.name());
                break;
            default:
                edit_id_type.setError(getString(R.string.error_text_blank));
                break;
        }
        model.setId_no(edit_id_no.getText().toString().trim());
        model.setSymptoms(selectedSymptoms);
        model.setConditions(selectedConditions);
        model.setExistingId(existingUser.getProfileId());

        ArrayList<String> editableFields = new ArrayList<>();
        editableFields.add("isVaccineReceived");
        editableFields.add("symptoms");
        editableFields.add("underlyingMedicalCondition");
        editableFields.add("vaccineType");
        editableFields.add("kitSerialNumber");

        model.setEditableProfileFields(editableFields);

        boolean isVaccinated = false;
        if (edit_vaccinated.getText().toString().equals(Constants.VACCINATED.YES.name())) {
            isVaccinated = true;
        }

        model.setVaccinated(isVaccinated);

        if (isVaccinated) {
            model.setVaccineType(edit_vaccine.getText().toString());
        }

        if (selectedSymptoms.contains("Others")) {
            model.setOtherSymptoms(otherSymptoms);
        }

        if (selectedConditions.contains("Others")) {
            model.setOtherConditions(otherConditions);
        }
        SharedPrefUtils.getInstance(this).putString(Constants.PREF_LOCAL_MODEL, new Gson().toJson(model));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_PERMISSIONS_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    new IntentIntegrator(this).initiateScan();
                }  else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

}