package com.clinkod.kabarak.auth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.clinkod.kabarak.R
import com.clinkod.kabarak.fhir.helper.DbOtp
import com.clinkod.kabarak.fhir.helper.FormatterClass
import com.clinkod.kabarak.retrofit.RetrofitCallsAuthentication

class OtpReset : AppCompatActivity() {

    private lateinit var etPassword :EditText
    private lateinit var etConfirmPassword :EditText
    private lateinit var etOtpCode :EditText
    private lateinit var btnSave: Button
    private lateinit var tvReset: TextView
    private val retrofitCallsAuthentication = RetrofitCallsAuthentication()
    private val formatterClass = FormatterClass()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_reset)

        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        etOtpCode = findViewById(R.id.etOtpCode)
        tvReset = findViewById(R.id.tvReset)
        btnSave = findViewById(R.id.btnSave)

        btnSave.setOnClickListener {

            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()
            val otpCode = etOtpCode.text.toString()

            if(!TextUtils.isEmpty(password)
                && !TextUtils.isEmpty(confirmPassword)
                && !TextUtils.isEmpty(otpCode)){

                if (password == confirmPassword){

                    val idNumber = formatterClass.retrieveSharedPreference(this, "idNumber")
                    if (idNumber != null) {
                        val dbOtp = DbOtp(idNumber,password, otpCode)
                        retrofitCallsAuthentication.newPassword(this, dbOtp)
                    }else{
                        startActivity(Intent(this, SignUp::class.java))
                        finish()
                    }


                }else{
                    etPassword.error = "Password don't match"
                    etConfirmPassword.error = "Password don't match"
                }


            }else{
                if (TextUtils.isEmpty(password)) etPassword.error = "Password is required"
                if (TextUtils.isEmpty(confirmPassword)) etConfirmPassword.error = "Confirm Password is required"
                if (TextUtils.isEmpty(otpCode)) etOtpCode.error = "OTP Code is required"

            }


        }

    }

    override fun onStart() {
        super.onStart()
        getSavedOtp()
    }

    private fun getSavedOtp() {

        formatterClass.retrieveSharedPreference(this, "otpCode")?.let {
            tvReset.text = it
        }

    }
}