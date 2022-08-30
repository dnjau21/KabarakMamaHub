package com.clinkod.kabarak;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

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
    private TextInputEditText idNumber;
    private BioData bioData;
    private ProgressBar progressBar;
    CoordinatorLayout coordinatorLayout;
    private static final String FIRST_TIME_USE = "first_time_use";
    private static final String IS_LOGGED_IN = "is_logged_in";
    private String TAG="LoginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        loginButton = findViewById(R.id.button);
        idNumber = findViewById(R.id.phoneConfirm);
        progressBar = findViewById(R.id.progressBar2) ;
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String  enterIdNumber = idNumber.getText().toString().trim();

                if(validateIdNumber()){
                    getOptRequest(enterIdNumber);

                }
            }
        });
    }

    private void getOptRequest(String idnumber){
        progressBar.setVisibility(View.VISIBLE);

        String url = Constants.API_SERVER_URL + "/api/mother/" + idnumber + "/generate/otp" ;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)   //request for url by passing url
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful())
                {
                    // task after data is received
                    String responseString = response.body().string();
                    // Log.d(TAG,responseString);
                    JSONObject res = null;
                    try {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                //do stuff like remove view etc
                                progressBar.setVisibility(View.GONE);
                            }
                        });

                        res = new JSONObject(responseString);
                        Boolean success=res.getBoolean("success");
                        if(success==true){
                            Intent i = new Intent(LoginActivity.this, OtpActivity.class);
                            i.putExtra("idnumber",idnumber);
                            startActivity(i);
                            finish();
                        }else{
                            String  message = res.getString("message");
                            Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT);
                            snackbar.setTextColor(ContextCompat.getColor(LoginActivity.this,R.color.white));
                            snackbar.setBackgroundTint(ContextCompat.getColor(LoginActivity.this,R.color.inactive_bottom_nav));
                            snackbar.show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private boolean validateIdNumber() {
        if (idNumber.length() < 7){
            idNumber.setError("Input a valid national Id number");
            return false;
        }

        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private class goToOTP implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(LoginActivity.this, OtpActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private class goToRegistration implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(LoginActivity.this, Registration.class);
            startActivity(intent);
            finish();
        }
    }
}