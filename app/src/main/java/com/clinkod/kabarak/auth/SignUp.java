package com.clinkod.kabarak.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.clinkod.kabarak.R;
import com.clinkod.kabarak.fhir.helper.DbRegisterData;
import com.clinkod.kabarak.retrofit.RetrofitCallsAuthentication;
import com.dave.validations.PhoneNumberValidation;

public class SignUp extends AppCompatActivity {


    private EditText etNationalId, etPhoneNumber;
    private RetrofitCallsAuthentication retrofitCallsAuthentication = new RetrofitCallsAuthentication();
    private TextView tvVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etNationalId = findViewById(R.id.etNationalId);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        tvVerify = findViewById(R.id.tvVerify);

        tvVerify.setOnClickListener(view -> {

            String nationalId = etNationalId.getText().toString().trim();
            String phoneNumber = etPhoneNumber.getText().toString().trim();

            if (!TextUtils.isEmpty(nationalId) && !TextUtils.isEmpty(phoneNumber)){

                String validPhone = new PhoneNumberValidation().getStandardPhoneNumber(phoneNumber);
                if (validPhone != null){

                    DbRegisterData dbRegister = new DbRegisterData(nationalId, phoneNumber);
                    retrofitCallsAuthentication.resetPassword(this, dbRegister);

                }else {
                    etPhoneNumber.setError("Phone number is not valid");
                }

            }else {
                if (TextUtils.isEmpty(nationalId)) etNationalId.setError("National Id is required");
                if (TextUtils.isEmpty(phoneNumber)) etPhoneNumber.setError("Phone number is required");
            }

        });

        findViewById(R.id.btnSave).setOnClickListener(view -> {

            String nationalId = etNationalId.getText().toString();
            String phoneNumber = etPhoneNumber.getText().toString();

            if(!TextUtils.isEmpty(nationalId) && !TextUtils.isEmpty(phoneNumber)){

                String validPhoneNumber = new PhoneNumberValidation().getStandardPhoneNumber(phoneNumber);

                if (validPhoneNumber != null){

                    DbRegisterData dbRegister = new DbRegisterData(nationalId, phoneNumber);
                    retrofitCallsAuthentication.resetPassword(this, dbRegister);

                }else {
                    etPhoneNumber.setError("Phone number is not valid");
                }

            } else {
                //show error message

                if (TextUtils.isEmpty(nationalId)) etNationalId.setError("National Id is required");
                if (TextUtils.isEmpty(phoneNumber)) etPhoneNumber.setError("Phone number is required");

            }

        });
    }
}