package com.clinkod.kabarak.retrofit

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.clinkod.kabarak.MainActivity
import com.clinkod.kabarak.auth.LoginActivity
import com.clinkod.kabarak.auth.OtpReset
import com.clinkod.kabarak.fhir.helper.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RetrofitCallsAuthentication {

    fun loginUser(context: Context, dbLogin: DbLogin){

        CoroutineScope(Dispatchers.Main).launch {
            loginUserBac(context, dbLogin)
        }

    }
    private suspend fun loginUserBac(context: Context, dbLogin: DbLogin) {

        val progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please wait..")
        progressDialog.setMessage("Login in progress..")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()

        val job = Job()
        CoroutineScope(Dispatchers.IO + job).launch {

            val formatter = FormatterClass()

            val baseUrl = context.getString(UrlData.BASE_URL.message)
            val apiService = RetrofitBuilder.getRetrofit(baseUrl).create(Interface::class.java)

            try {

                val apiInterface = apiService.login(dbLogin)

                val statusCode = apiInterface.code()
                val body = apiInterface.body()

                if (statusCode == 200 || statusCode == 201){

                    if (body != null){

                        val token = body.token
                        val expires = body.expires

                        formatter.saveSharedPreference(context, "token", token)
                        formatter.saveSharedPreference(context, "expires", expires)

                        getUserData(context)

                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                        (context as Activity).finish()

                    }



                }else if (statusCode == 401){
                    val jsonError = apiInterface.errorBody()?.string()?.let { JSONObject(it) }
                    if (jsonError?.has("message") == true) {
                        val message = jsonError.getString("message")
                        showToast(context, message)
                    }
                    if (jsonError?.has("error") == true) {
                        val message = jsonError.getString("error")
                        showToast(context, message)
                    }



                }else if (statusCode == 500){
                    showToast(context, "There is an issue processing your request. Please try again later")
                }else{
                    val jsonError = apiInterface.errorBody()?.string()?.let { JSONObject(it) }
                    val error = jsonError?.getString("error")

                    showToast(context, error.toString())
                }



            }catch (e: Exception){
                Log.e("-*-*error ", e.localizedMessage)
            }

        }.join()
        progressDialog.dismiss()

    }


    fun registerUser(context: Context, dbRegister: DbRegisterData){

        CoroutineScope(Dispatchers.Main).launch {
            startRegister(context, dbRegister)
        }

    }
    private suspend fun startRegister(context: Context, dbRegister: DbRegisterData) {

        val progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please wait..")
        progressDialog.setMessage("Registration in progress..")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()

        val job = Job()
        CoroutineScope(Dispatchers.IO + job).launch {

            val baseUrl = context.getString(UrlData.BASE_URL.message)
            val apiService = RetrofitBuilder.getRetrofit(baseUrl).create(Interface::class.java)

            try {

                val apiInterface = apiService.registerUser(dbRegister)

                val statusCode = apiInterface.code()
                val body = apiInterface.body()

                if (apiInterface.isSuccessful){

                    val status = body?.status

                    if(status == "success"){
                        val message = body.message
                        if (message != null){

                            //Get otp code

                            showToast(context, message)

                            resetPassword(context, dbRegister)

                        }else{
                            showToast(context, "Registration successful")
                        }
                    }else{
                        showToast(context, "Invalid credentials. Ensure client is registered on Mama's Hub")
                    }

                }else{

                    //Get error message
                    val jsonError = apiInterface.errorBody()?.string()?.let { JSONObject(it) }
                    val error = jsonError?.getString("error")

                    showToast(context, error.toString())

                }


            }catch (e: Exception){
                Log.e("-*-*error ", e.localizedMessage)
            }

        }.join()
        progressDialog.dismiss()

    }

    fun resetPassword(context: Context, dbReset: DbRegisterData){

        CoroutineScope(Dispatchers.Main).launch {
            startReset(context, dbReset)
        }

    }
    private suspend fun startReset(context: Context, dbReset: DbRegisterData) {

        val progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please wait..")
        progressDialog.setMessage("Account verification..")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()

        val job = Job()
        CoroutineScope(Dispatchers.IO + job).launch {

            val baseUrl = context.getString(UrlData.BASE_URL.message)
            val apiService = RetrofitBuilder.getRetrofit(baseUrl).create(Interface::class.java)

            try {

                val apiInterface = apiService.resetPassword(dbReset)

                val statusCode = apiInterface.code()
                val body = apiInterface.body()

                if (apiInterface.isSuccessful){

                    if (body != null) {

                        val message = body.message.toString()
                        val otpCode = body.otp.toString()

                        FormatterClass().saveSharedPreference(context, "otpCode", otpCode)
                        FormatterClass().saveSharedPreference(context, "idNumber", dbReset.idNumber)

                        val intent = Intent(context, OtpReset::class.java)
                        context.startActivity(intent)
                        (context as Activity).finish()

                        showToast(context, message)

                    }else{
                        showToast(context, "We could not send an otp code.")
                    }


                }else{

                    //Get error message
//                    val jsonError = apiInterface.errorBody()?.string()?.let { JSONObject(it) }
//                    val error = jsonError?.getString("error")

                    showToast(context, "We could not send an otp code.")

                }


            }catch (e: Exception){
                Log.e("-*-*error ", e.localizedMessage)
            }

        }.join()
        progressDialog.dismiss()

    }

    fun newPassword(context: Context, dbOtp: DbOtp){

        CoroutineScope(Dispatchers.Main).launch {
            newPasswordBac(context, dbOtp)
        }

    }
    private suspend fun newPasswordBac(context: Context, dbOtp: DbOtp) {

        val progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please wait..")
        progressDialog.setMessage("Otp verification..")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()

        val job = Job()
        CoroutineScope(Dispatchers.IO + job).launch {

            val baseUrl = context.getString(UrlData.BASE_URL.message)
            val apiService = RetrofitBuilder.getRetrofit(baseUrl).create(Interface::class.java)

            try {

                val apiInterface = apiService.setNewPassword(dbOtp)

                val statusCode = apiInterface.code()
                val body = apiInterface.body()

                if (apiInterface.isSuccessful){

                    if (body != null) {

                        val message = body.message.toString()

                        showToast(context, message)

                        val intent = Intent(context, LoginActivity::class.java)
                        context.startActivity(intent)
                        (context as Activity).finish()


                    }else{
                        showToast(context, "We could not send an otp code.")
                    }


                }else{

                    //Get error message
//                    val jsonError = apiInterface.errorBody()?.string()?.let { JSONObject(it) }
//                    val error = jsonError?.getString("error")

                    showToast(context, "We could not send an otp code.")

                }


            }catch (e: Exception){
                Log.e("-*-*error ", e.localizedMessage)
            }

        }.join()
        progressDialog.dismiss()

    }

    private fun getUserData(context: Context) {

        var formatter = FormatterClass()
        val stringStringMap = formatter.getHeaders(context)

        val baseUrl = context.getString(UrlData.BASE_URL.message)

        val apiService = RetrofitBuilder.getRetrofit(baseUrl).create(Interface::class.java)
        val apiInterface = apiService.getUserData(stringStringMap)
        apiInterface.enqueue(object : Callback<DbUserData> {
            override fun onResponse(
                call: Call<DbUserData>,
                response: Response<DbUserData>
            ) {

                if (response.isSuccessful) {

                    val responseData = response.body()

                    if (responseData != null){

                        val data = responseData.data

                        val names = data.names
                        val idNumber = data.idNumber
                        val fhirPatientId = data.fhirPatientId

                        formatter.saveSharedPreference(context, "names", names)
                        formatter.saveSharedPreference(context, "idNumber", idNumber)
                        formatter.saveSharedPreference(context, "patientId", fhirPatientId)

                    }

                }

            }

            override fun onFailure(call: Call<DbUserData>, t: Throwable) {
                Log.e("-*-*error ", t.localizedMessage)

            }
        })
    }

    private fun showToast(context: Context, message: String) {
        CoroutineScope(Dispatchers.Main).launch{
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

}

