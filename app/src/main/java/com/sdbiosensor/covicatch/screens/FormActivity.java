package com.sdbiosensor.covicatch.screens;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.sdbiosensor.covicatch.R;
import com.sdbiosensor.covicatch.adapters.JsonArrayRecyclerAdapter;
import com.sdbiosensor.covicatch.adapters.MultiRecyclerAdapter;
import com.sdbiosensor.covicatch.adapters.StringRecyclerAdapter;
import com.sdbiosensor.covicatch.constants.Constants;
import com.sdbiosensor.covicatch.customcomoponents.BaseActivity;
import com.sdbiosensor.covicatch.events.AfterSubmitCloseEvent;
import com.sdbiosensor.covicatch.network.models.LocalDataModel;
import com.sdbiosensor.covicatch.utils.SharedPrefUtils;
import com.sdbiosensor.covicatch.utils.Utils;
import com.sdbiosensor.covicatch.utils.ValidationUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FormActivity extends BaseActivity implements View.OnClickListener{

    private EditText edit_first_name, edit_last_name, edit_mobile, edit_address, edit_pincode, edit_id_no;
    private TextView edit_gender, edit_state, edit_city, edit_id_type, edit_symptoms, edit_conditions;
    private View progress;
    private int selectedGender = -1, selectedIdType = -1;
    private JSONObject stateCityMapping;
    private JSONArray statesList;
    private ArrayList<String> selectedSymptoms = new ArrayList<>(), selectedConditions = new ArrayList<>();
    private String otherSymptoms, otherConditions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        initView();
        handleClicks();
        initStateCityArray();
    }

    private void initView() {
        edit_first_name = findViewById(R.id.edit_first_name);
        edit_last_name = findViewById(R.id.edit_last_name);
        edit_mobile = findViewById(R.id.edit_mobile_number);
        edit_address = findViewById(R.id.edit_address);
        edit_pincode = findViewById(R.id.edit_pin_code);
        edit_gender = findViewById(R.id.edit_gender);
        edit_state = findViewById(R.id.edit_state);
        edit_city = findViewById(R.id.edit_city);
        edit_id_type = findViewById(R.id.edit_id_type);
        edit_id_no = findViewById(R.id.edit_id_no);
        edit_symptoms = findViewById(R.id.edit_symptoms);
        edit_conditions = findViewById(R.id.edit_conditions);
        progress = findViewById(R.id.progress);
    }

    private void handleClicks() {
        findViewById(R.id.button_next).setOnClickListener(this);
        edit_gender.setOnClickListener(this);
        edit_state.setOnClickListener(this);
        edit_city.setOnClickListener(this);
        edit_id_type.setOnClickListener(this);
        edit_symptoms.setOnClickListener(this);
        edit_conditions.setOnClickListener(this);
    }

    private void initStateCityArray() {
        try {
            stateCityMapping = new JSONObject(Constants.STATE_CITY_JSON);
            statesList = stateCityMapping.names();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.edit_gender) {
            openChooseGenderDialog();
        } else if (view.getId() == R.id.edit_state) {
            openChooseStateDialog();
        } else if (view.getId() == R.id.edit_city) {
            openChooseCityDialog();
        } else if (view.getId() == R.id.edit_id_type) {
            openChooseIdTypeDialog();
        } else if (view.getId() == R.id.edit_symptoms) {
            openChooseSymptomsDialog();
        } else if (view.getId() == R.id.edit_conditions) {
            openChooseConditionsDialog();
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
            public void onItemClick(String item, int positon) {
                selectedGender = positon;
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
        dialogRecyclerView.setAdapter(new JsonArrayRecyclerAdapter(this, statesList, new JsonArrayRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item, int positon) {
                edit_state.setText(item);
                edit_city.setText("");
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

    private void openChooseCityDialog() {
        if (edit_state.getText().toString().isEmpty() || !stateCityMapping.has(edit_state.getText().toString())) {
            showErrorDialog(getResources().getString(R.string.error_select_valid_state));
            return;
        }

        try {
            JSONArray cityList = stateCityMapping.getJSONArray(edit_state.getText().toString());

            LayoutInflater inflater = LayoutInflater.from(this);
            final View dialogView = inflater.inflate(R.layout.dialog_string_selection, null);
            final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setView(dialogView);

            RecyclerView dialogRecyclerView = dialogView.findViewById(R.id.recyclerView);
            dialogRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            dialogRecyclerView.setAdapter(new JsonArrayRecyclerAdapter(this, cityList, new JsonArrayRecyclerAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(String item, int positon) {
                    edit_city.setText(item);
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
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
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
            public void onItemClick(String item, int positon) {
                selectedIdType = positon;
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
                !ValidationUtils.blankValidation(edit_mobile) ||
                !ValidationUtils.blankValidation(edit_address) ||
                !ValidationUtils.blankValidation(edit_pincode) ||
                !ValidationUtils.blankValidation(edit_pincode) ||
                !ValidationUtils.blankValidation(edit_state) ||
                !ValidationUtils.blankValidation(edit_city) ||
                !ValidationUtils.blankValidation(edit_id_type) ||
                !ValidationUtils.blankValidation(edit_id_no)) {
            return;
        }

        saveModelAndMoveToNextScreen();
    }

    private void saveModelAndMoveToNextScreen() {
        LocalDataModel model = new LocalDataModel();
        model.setFirstName(edit_first_name.getText().toString().trim());
        model.setLastName(edit_last_name.getText().toString().trim());
        model.setMobile(edit_mobile.getText().toString().trim());
        model.setAddress(edit_address.getText().toString().trim());
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
        model.setCity(edit_city.getText().toString().trim());
        switch (selectedIdType) {
            case 0:
                model.setId_type(Constants.ID_TYPE.AADHAAR_CARD.name());
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

        if (selectedSymptoms.contains("Others")) {
            model.setOtherSymptoms(otherSymptoms);
        }

        if (selectedConditions.contains("Others")) {
            model.setOtherConditions(otherConditions);
        }
        SharedPrefUtils.getInstance(this).putString(Constants.PREF_LOCAL_MODEL, new Gson().toJson(model));
        startActivity(new Intent(FormActivity.this, InstructionActivity.class));
        finish();
    }

}