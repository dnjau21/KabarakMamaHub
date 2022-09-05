package com.clinkod.kabarak.auth;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.clinkod.kabarak.R;

public class SignUp extends AppCompatActivity {


    private EditText etNationalId, etPassword, etConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etNationalId = findViewById(R.id.etNationalId);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        findViewById(R.id.btnSave).setOnClickListener(view -> {

            String nationalId = etNationalId.getText().toString();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();

            if(!TextUtils.isEmpty(nationalId) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword)) {
                if (password.equals(confirmPassword)) {
                    //save user to database


                } else {
                    //show error message
                    Toast.makeText(this, "The password do not match.", Toast.LENGTH_SHORT).show();
                }

            } else {
                //show error message

                if (TextUtils.isEmpty(nationalId)) etNationalId.setError("National Id is required");
                if (TextUtils.isEmpty(password)) etPassword.setError("Password is required");
                if (TextUtils.isEmpty(confirmPassword)) etConfirmPassword.setError("Confirm Password is required");

            }

        });
    }
}