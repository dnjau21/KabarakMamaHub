package com.clinkod.kabarak.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.clinkod.kabarak.MainActivity;
import com.clinkod.kabarak.models.Mother;
import com.clinkod.kabarak.models.PropertyUtils;
import com.clinkod.kabarak.utils.Constants;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;


import com.clinkod.kabarak.R;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OtpActivity extends AppCompatActivity {

//    tv_phone_no
    EditText et1,et2, et3, et4;
    private ProgressBar progressBar;
    private String TAG="OtpActivity";
    String idnumber;
    CoordinatorLayout coordinatorLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        Intent intent = getIntent();
        // check intent is null
        if(intent != null){
             idnumber = intent.getStringExtra("idnumber");

        }
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        progressBar = findViewById(R.id.progressBar2) ;
        et1 = findViewById(R.id.editText1);
        et2 = findViewById(R.id.editText2);
        et3 = findViewById(R.id.editText3);
        et4 = findViewById(R.id.editText4);

        et1.addTextChangedListener(new GenericTextWatcher(et1));
        et2.addTextChangedListener(new GenericTextWatcher(et2));
        et3.addTextChangedListener(new GenericTextWatcher(et3));
        et4.addTextChangedListener(new GenericTextWatcher(et4));

        et1.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

    }

    public class GenericTextWatcher implements TextWatcher
    {
        private View view;
        private GenericTextWatcher(View view)
        {
            this.view = view;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // TODO Auto-generated method stub
            String text = editable.toString();
            switch(view.getId())
            {

                case R.id.editText1:
                    if(text.length()==1)
                        et2.requestFocus();
                    break;
                case R.id.editText2:
                    if(text.length()==1)
                        et3.requestFocus();
                    else if(text.length()==0)
                        et1.requestFocus();
                    break;
                case R.id.editText3:
                    if(text.length()==1)
                        et4.requestFocus();
                    else if(text.length()==0)
                        et2.requestFocus();
                    break;
                case R.id.editText4:
                    if(text.length()==0)
                        et3.requestFocus();
                    else{
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et1.getWindowToken(), 0);
                        progressBar.setVisibility(View.VISIBLE);
                        String otp = et1.getText().toString().trim() + et2.getText().toString().trim()
                                + et3.getText().toString().trim() + et4.getText().toString().trim();
                        postOptRequest( idnumber, otp);
                        // Log.d("Proceed","Proceesd");
                    }

                    break;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }
    }



    private void postOptRequest(String idnumber, String otp){
        progressBar.setVisibility(View.VISIBLE);
        String url = Constants.API_SERVER_URL + "/api/mother/" + idnumber + "/" + otp + "/activate/otp" ;
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("idnumber", idnumber)
                .addFormDataPart("otp", otp)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
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
                    JSONObject dataMother = null;
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
                            Mother mother;
                            String  access_token = res.getString("access_token");
                            String  data = res.getString("data").toString();
                            // Log.d(TAG,access_token);
                            // Log.d(TAG,data);

                            dataMother = new JSONObject(data);
                            Mother.createMotherData(Integer.parseInt(dataMother.getString("id")),dataMother.getString("firstname") + dataMother.getString("lastname")
                                    , dataMother.getString("firstname"),dataMother.getString("lastname"),Integer.parseInt(dataMother.getString("id_number")),dataMother.getString("phone")
                                    ,dataMother.getString("dob"),Integer.parseInt(dataMother.getString("id")),Integer.parseInt(dataMother.getString("otp")), OtpActivity.this);
                            PropertyUtils.setHasCompletedOnboarding(true);
                            PropertyUtils.logIn(true);
                            Intent intent = new Intent(OtpActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

//
                        }else{
                            String  message = res.getString("message");
                            // Log.d(TAG,message);
                            Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT);
                            snackbar.setTextColor(ContextCompat.getColor(OtpActivity.this,R.color.white));
                            snackbar.setBackgroundTint(ContextCompat.getColor(OtpActivity.this,R.color.inactive_bottom_nav));
                            snackbar.show();
                        }
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                }else{
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            //do stuff like remove view etc
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Invalid OTP used", Snackbar.LENGTH_SHORT);
                            snackbar.setTextColor(ContextCompat.getColor(OtpActivity.this,R.color.white));
                            snackbar.setBackgroundTint(ContextCompat.getColor(OtpActivity.this,R.color.inactive_bottom_nav));
                            snackbar.show();
                }
            }
        });
    }

    private void goToMain(){
        //                PropertyUtils.logIn(true);
//                if (enterPhone.equals(phoneNumber)) {
//                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                } else {
//
//                    Snackbar snackbar =
//                            Snackbar.make(coordinatorLayout, "Phone number does not match", Snackbar.LENGTH_SHORT);
//                    // Set action with Retry Listener
//                    snackbar.setAction("Register", new goToRegistration())
//                            .setDuration(BaseTransientBottomBar.LENGTH_INDEFINITE);
//                    // show the Snackbar
//                    snackbar.show();
//
//                }
    }


}