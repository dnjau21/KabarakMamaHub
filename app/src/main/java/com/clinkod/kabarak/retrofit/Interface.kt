package com.clinkod.kabarak.retrofit

import com.clinkod.kabarak.fhir.helper.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface Interface {


    @POST("client/login")
    suspend fun login(@Body user: DbLogin): Response<DbAuthResponse>

    @POST("client/register")
    suspend fun registerUser(@Body user: DbRegisterData): Response<AuthResponse>

    @POST("client/reset-password")
    suspend fun resetPassword(@Body user: DbRegisterData): Response<AuthResponse>

    @POST("client/new-password")
    suspend fun setNewPassword(@Body user: DbOtp): Response<AuthResponse>

    @GET("client/me/")
    fun getUserData(@HeaderMap headers: Map<String, String>): Call<DbUserData>


}