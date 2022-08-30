package com.clinkod.kabarak;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;

import android.view.View;
import android.widget.*;

import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.fragment.app.DialogFragment;
import com.androidadvance.androidsurvey.SurveyActivity;
import com.clinkod.kabarak.exceptions.LocalPropertyNotFound;
import com.clinkod.kabarak.models.BioData;
import com.clinkod.kabarak.models.Mother;
import com.clinkod.kabarak.models.PropertyUtils;
import com.clinkod.kabarak.preference.PrefManager;
import com.clinkod.kabarak.utils.DatePickerFragment;
import com.clinkod.kabarak.utils.Utils;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Registration extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = Registration.class.getSimpleName();

    private static final int ONBOARDING_DATA_REQUEST = 1337;
    private static final int PERIODIC_DATA_REQUEST = 1338;
    private static final int DAILY_DATA_REQUEST = 1339;
    private static final int ACTIVITY_MOOD_REQUEST = 1340;
    private static final String FIRST_TIME_USE = "first_time_use";

    public static final String PROPERTY_ACTIVITY_AND_MOOD = "take_activity_and_mood";
    public static final String PROPERTY_DAILY_DATA = "take_daily_data";

    private AlertDialog alertDialog;
    private BioData bioData;
    private Context context;
    private TextInputEditText firstname, lastname, other_names, dob, phoneNumber, kin, kinContact, tedHeight, tedWeight, id_number;
    private TextInputLayout dobEdtLayout;
    private Button btnSignUp;
    private DatePicker dobDate;
    private DatePickerDialog mDatePickerDialog;
    private Calendar myCal;
    private String maritalStatus;
    AppCompatRadioButton rbLeft, rbRight;
    RadioGroup radioGroup;
    boolean isAllFieldsChecked = false;
    String firstname_v, lastname_v, phone_number_v, date_of_birth_v;
    int mother_id_v, id_number_v, otp;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_personal_info);

        context = this;

        myCal = Calendar.getInstance();


        firstname = findViewById(R.id.ted_first_name);
        lastname = findViewById(R.id.ted_last_name);
        btnSignUp = findViewById(R.id.btnSave);
        dob = findViewById(R.id.dobEdt);
        phoneNumber = findViewById(R.id.tedPhone);
        radioGroup = findViewById(R.id.marital);
        rbLeft = findViewById(R.id.rbLeft);
        rbRight = findViewById(R.id.rbRight);
        id_number = findViewById(R.id.ted_id_number);

        selectMaritalStatus();

          firstname_v = PrefManager.getFirstName(Registration.this);
          lastname_v = PrefManager.getLastname(Registration.this);
          id_number_v = PrefManager.getIdNumber(Registration.this);
          mother_id_v = PrefManager.getId(Registration.this);
          otp = PrefManager.getOtp(Registration.this);
          phone_number_v = PrefManager.getPhoneNumber(Registration.this);
          date_of_birth_v = PrefManager.getDateOfBirth(Registration.this);

            firstname.setText(firstname_v);
            lastname.setText(lastname_v);
            id_number.setText(String.valueOf(id_number_v));
            phoneNumber.setText(phone_number_v);
            dob.setText(date_of_birth_v);



        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dFragment = new DatePickerFragment();
                dFragment.show(getSupportFragmentManager(), "Date Picker");
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAllFieldsChecked = checkAllFields();

                String lastName ;

                if (isAllFieldsChecked) {

                    String name = firstname.getText().toString().trim();
                    String firstName = firstname.getText().toString().trim();
                    lastName = lastname.getText().toString().trim();
                    String dateOfBirth = dob.getText().toString().trim();
                    String phone = phoneNumber.getText().toString().trim();
                    String idnumber = id_number.getText().toString().trim();
                    String marital_status = maritalStatus;

                    Mother.createMotherData(mother_id_v,firstName + lastName , firstName,lastName, Integer.parseInt(idnumber),phone ,dateOfBirth,mother_id_v,otp, Registration.this);

                    PropertyUtils.setHasCompletedOnboarding(true);
                    PropertyUtils.logIn(true);
                    goToMainActivity();

                }
            }
        });
    }

    private void selectMaritalStatus() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbLeft){
                    maritalStatus = "MARRIED";
                    rbLeft.setTextColor(Color.WHITE);
                    rbRight.setTextColor(getResources().getColor(R.color.green_700));
                }else if (checkedId == R.id.rbRight){
                    maritalStatus = "SINGLE";
                    rbRight.setTextColor(Color.WHITE);
                    rbLeft.setTextColor(getResources().getColor(R.color.green_700));
                }
            }
        });

    }



    private boolean checkAllFields() {
        String required = "This field is required";
        if (firstname.length() == 0) {
            firstname.setError(required);
            return false;
        }
        if (lastname.length() == 0) {
            firstname.setError(required);
            return false;
        }
        if (dob.length() == 0 ) {
            dob.setError(required);
            return false;
        }
        if (id_number.length() < 7 || id_number.length() > 9 ){
            other_names.setError("Input a valid ID number");
            return false;

        }
        if (phoneNumber.length() == 0) {
            phoneNumber.setError(required);
            return false;
        } else if (phoneNumber.length() < 10) {
            phoneNumber.setError("Ensure the number is correct");
            return false;
        }

        return  true;

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        final Date date = c.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fDate = sdf.format(date);
        dob.setText(fDate);
    }

    public void onRadioButtonClicked(View view) {
        boolean isSelected = ((AppCompatRadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.rbLeft:
                if (isSelected) {
                    /*rbLeft.setTextColor(Color.WHITE);
                    rbRight.setTextColor(getResources().getColor(R.color.green_700));*/
//                    Toast.makeText(getApplicationContext(), "Married",Toast.LENGTH_SHORT).show();

                }
                break;
            case R.id.rbRight:
                if (isSelected) {
                    /*rbRight.setTextColor(Color.WHITE);
                    rbLeft.setTextColor(getResources().getColor(R.color.green_700));*/
//                   Toast.makeText(getApplicationContext(), "Single",Toast.LENGTH_SHORT).show();

                }
                break;
        }
    }


    private void checkFirstTimeUse() {
        if (isFirstTimeUse()) {// TODO: remember to set this to false after initial setup
            // TODO:
            // check internet connection
            // fetch required initial data
            // init first-time wizard

            Utils.setUpAlarms(this);

        }
    }


    public void checkInternetConnection() {
        if (isConnected()) {
            Toast.makeText(getApplicationContext(), "Internet Connected", Toast.LENGTH_SHORT).show();
//
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            alertDialog = new AlertDialog.Builder(context)
                    .setMessage("You are not connected! First time use requires internet connection.")
                    .setCancelable(false)
                    .setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                            startActivity(intent);
                        }
                    }).setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();
            alertDialog.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            connected = activeNetworkInfo != null && activeNetworkInfo.isConnected();
            return connected;
        } catch (Exception e) {
            // Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }


    //    TODO -- Check Previous data collection
    private void previousDataCollection() {

        Date previousCollection = PropertyUtils.getLastPeriodicCollection();
        long diff;
        if (previousCollection != null) {
            long diffInMillies = Math.abs((new Date()).getTime() - previousCollection.getTime());
            diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        } else {
            diff = 8;
        }

        if (!PropertyUtils.hasFinishedOnboardingData()) {
            openOnboardingSurvey();

        } else {
            //DataSynchronizationService.startActionSynchronizeData(getApplicationContext());
            PropertyUtils.putProperty(FIRST_TIME_USE, false);
            goToMainActivity();
        }

    }

    private boolean isFirstTimeUse() {
        try {
            return PropertyUtils.getBooleanValue(FIRST_TIME_USE, false);
        } catch (LocalPropertyNotFound e) {

            PropertyUtils.putProperty(FIRST_TIME_USE, false);
            return true;
        }
    }

    private void openOnboardingSurvey() {
        String onboardingQuestions = Utils.loadSurveyJson(this, "onboarding.json");
        Intent i_survey = new Intent(Registration.this, SurveyActivity.class);
        i_survey.putExtra("json_survey", onboardingQuestions);
        startActivityForResult(i_survey, ONBOARDING_DATA_REQUEST);
    }

    private void goToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }
        /*switch (requestCode) {
            case ONBOARDING_DATA_REQUEST: {
                String answers_json = data.getExtras().getString("answers");
                // Log.i(TAG, "Result: " + answers_json);
                try {
                    JSONObject answers = new JSONObject(answers_json);

                    String name = "";
                    String dateOfBirth = "";
                    String phone = "";
                    String gender = "";
                    int height = -1;
                    double weight = -1;
                    String nextOfKin = "";
                    String nextOfKinContact = "";
                    String relWithNextOfKin = "";
                    boolean familyhypertensionHistory = false;
                    String careGiverName = "";
                    String careGiverPhone = "";

                    Iterator<String> questions = answers.keys();
                    while (questions.hasNext()) {
                        String question = questions.next();

                        if (question.contains("Full")) {
                            name = answers.getString(question).trim();
                        } else if (question.contains("Date")) {
                            dateOfBirth = answers.getString(question).trim();
                        } else if (question.contains("phone")) {
                            phone = answers.getString(question).trim();
                        } else if (question.contains("gender")) {
                            gender = answers.getString(question).trim();
                        } else if (question.contains("height")) {
                            height = Integer.parseInt(answers.getString(question).trim());
                        } else if (question.contains("weigh")) {
                            weight = Double.parseDouble(answers.getString(question));
                        } else if (question.contains("next of")) {
                            nextOfKin = answers.getString(question).trim();
                        } else if (question.contains("contact")) {
                            nextOfKinContact = answers.getString(question).trim();
                        } else if (question.contains("Relationship")) {
                            relWithNextOfKin = answers.getString(question).trim();
                        } else if (question.contains("name")) {
                            careGiverName = answers.getString(question).trim();
                        } else if (question.contains("number of your caregiver")) {
                            careGiverPhone = answers.getString(question).trim();
                        } else if (question.contains("hypertension")) {
                            if (answers.getString(question).contains("Yes")) {
                                familyhypertensionHistory = true;
                            }
                        }
                    }

                    BioData.createBioData(name,firsName, middleName, lastName,  dateOfBirth, phone, gender, height, weight,
                            nextOfKin, nextOfKinContact, relWithNextOfKin, careGiverName,
                            careGiverPhone, familyhypertensionHistory);

                    PropertyUtils.setHasCompletedOnboarding(true);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                goToMainActivity();

            }
            break;

        }*/
    }


}

