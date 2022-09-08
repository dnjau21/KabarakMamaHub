package com.clinkod.kabarak.auth;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import com.clinkod.kabarak.MainActivity;
import com.clinkod.kabarak.R;
import com.clinkod.kabarak.Registration;
import com.clinkod.kabarak.fhir.helper.FormatterClass;
import com.clinkod.kabarak.models.BioData;
import com.clinkod.kabarak.ui.OtpActivity;
import com.clinkod.kabarak.utils.Constants;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private Button loginButton;
    private TextInputEditText etPassword, idNumber;
    private BioData bioData;
    private static final String FIRST_TIME_USE = "first_time_use";
    private static final String IS_LOGGED_IN = "is_logged_in";
    private String TAG="LoginActivity";
    private FormatterClass formatterClass = new FormatterClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.button);
        etPassword = findViewById(R.id.etPassword);
        idNumber = findViewById(R.id.phoneConfirm);

        loginButton.setOnClickListener(v -> {

            String  idNumberText = idNumber.getText().toString().trim();
            if (!TextUtils.isEmpty(idNumberText)) {

                formatterClass.saveSharedPreference(LoginActivity.this,
                        "patientId",
                        "50851f53-a249-419c-80cc-a2a9fd3bb961");

                String  passwordText = etPassword.getText().toString().trim();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));



            } else {
                idNumber.setError("Please enter your id number");
                idNumber.requestFocus();
            }


        });

        findViewById(R.id.tvRegister).setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignUp.class)));

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}