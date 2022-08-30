package com.clinkod.kabarak.utils;

import android.util.Log;

import com.clinkod.kabarak.models.BioData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkManager {

    private static final String TAG = NetworkManager.class.getSimpleName();

    public interface NetworkChangeListener{
        void onChange(boolean isConnected);
    }

    private volatile static NetworkManager instance;
    private Boolean isConnected = true;

    private static final MediaType JSONMedia = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType textMedia = MediaType.parse("text/plain");
    public static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");

    private OkHttpClient client;
    private String accessToken = null;
    private Date lastLogin = null;

    private ArrayList<NetworkChangeListener> listeners;

    public static NetworkManager getInstance() {

        if(instance == null){
            synchronized (NetworkManager.class){
                if(instance == null){
                    instance = new NetworkManager();
                }
            }
        }

        return instance;
    }

    private NetworkManager() {
        this.client = new OkHttpClient();
        this.listeners = new ArrayList<>();
    }

    public void setListener(NetworkChangeListener listener){
        listeners.add(listener);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Boolean isConnected() {
        return isConnected;
    }

    public void setConnected(Boolean connected) {
        isConnected = connected;

        for(NetworkChangeListener listener : listeners){
            try{
                listener.onChange(this.isConnected);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void addAuthHeader(Headers.Builder headers) throws IOException{
        if(this.accessToken == null || this.lastLogin == null
                || Utils.getDateDiff(this.lastLogin, new Date(), TimeUnit.MINUTES) > 1){
            Response response = this.login();

            String responseString  = response.body().string();
            // Log.i(TAG, "Login response: " + responseString);

            if(response.isSuccessful()){
                try {
                    JSONObject res = new JSONObject(responseString);

                    this.accessToken = res.getString("access_token");
                    this.lastLogin = new Date();
                } catch (JSONException e) {
                    e.printStackTrace();

                    throw new IOException("Error extracting access token");
                }
            }else{
                throw new IOException("Login failed");
            }
        }

        headers.add("Authorization", "Bearer " + this.accessToken);
    }

    public Response login() throws IOException{
        System.out.println(TAG + " Login called");

        RequestBody requestBody = new MultipartBody.Builder()
                //.type(MultipartBuilder.FORM)
                .addFormDataPart("grant_type", "password")
                .addFormDataPart("username", BioData.getBioData().getEmail())
                .addFormDataPart("password", "deviceuserpassword")
                .build();

        Request request = new Request.Builder()
                .url(Constants.BASE_SERVER_URL + "/rest/v2/oauth/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "Basic bnJmYmFja2VuZC1LSm1lZDZYVzpjMmNmZTJjODg4NTE4NWFlNjQyYzZkNDc1ZmRiNDQ4NDZhMDAwNGYwNTlmNTA2ZWU5ZTIwMjdjMzEzY2U5NDM1")
                .post(requestBody)
                .build();

        Call call = this.client.newCall(request);

        return  call.execute();
    }

    public Response getOtp() throws IOException{
        System.out.println(TAG + " Login called");


        Request request = new Request.Builder()
                .url(Constants.BASE_SERVER_URL + "/rest/v2/oauth/token")
                .get()
                .build();

        Call call = this.client.newCall(request);

        return  call.execute();
    }

    public Response postReadings(String url, @Nullable Headers.Builder headers, JSONObject data,
                         @Nullable Callback callback, Boolean requireAuth) throws IOException, JSONException {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("heart_rate",data.getString("heartRate"))
                .addFormDataPart("systole",data.getString("systole"))
                .addFormDataPart("diastole",data.getString("diastole"))
                .addFormDataPart("mother_id",data.getString("mother_id"))
                .addFormDataPart("id",data.getString("mother_id"))
                .addFormDataPart("category",data.getString("category"))
                .addFormDataPart("random_id",data.getString("random_id"))
                .addFormDataPart("activity",data.getString("activity"))
                .addFormDataPart("mood",data.getString("mood"))
                .build();

        Request.Builder requestBuilder = new Request.Builder();

        requestBuilder.url(url)
                .addHeader("Content-Type", requestBody.contentType().toString())
                .post(requestBody);

        Request request = requestBuilder.build();

        Call call = this.client.newCall(request);

        if(callback == null){
            return call.execute();
        }

        call.enqueue(callback);

        return null;
    }

    public Response post(String url, @Nullable Headers.Builder headers, JSONObject data,
                         @Nullable Callback callback, Boolean requireAuth) throws IOException {

        RequestBody requestBody = RequestBody.create(JSONMedia, data.toString());
        Request.Builder requestBuilder = new Request.Builder();

        if(headers == null)
            headers = new Headers.Builder();

        if(requireAuth)
            addAuthHeader(headers);

        requestBuilder.url(url)
                .headers(headers.build())
                .post(requestBody);

        Request request = requestBuilder.build();

        Call call = this.client.newCall(request);

        if(callback == null){
            return call.execute();
        }

        call.enqueue(callback);

        return null;
    }

    public Response put(String url, @Nullable Headers.Builder headers, JSONObject data,
                    @Nullable Callback callback, Boolean requireAuth) throws IOException{
        RequestBody requestBody = RequestBody.create(JSONMedia, data.toString());
        Request.Builder requestBuilder = new Request.Builder();

        if(headers == null)
            headers = new Headers.Builder();

        if(requireAuth)
            addAuthHeader(headers);

        headers.add("Content-Type", "application/json");
        requestBuilder.url(url)
                .headers(headers.build())
                .put(requestBody);

        Request request = requestBuilder.build();

        Call call = this.client.newCall(request);

        if(callback == null){
            return call.execute();
        }

        call.enqueue(callback);

        return null;
    }

    public Response get(String url, @Nullable Headers.Builder headers, @Nullable Callback callback,
                        Boolean requireAuth)
            throws IOException {

        if(headers == null)
            headers = new Headers.Builder();

        if(requireAuth)
            addAuthHeader(headers);

        Request request = new Request.Builder()
                .url(url)
                .headers(headers.build())
                .get()
                .build();

        Call call = this.client.newCall(request);

        if(callback == null){
            return call.execute();
        }else{
            call.enqueue(callback);
        }

        return null;
    }
    public Response getThread(String url, @Nullable Headers.Builder headers, @Nullable Callback callback,
                        Boolean requireAuth)
            throws IOException {

        if(headers == null)
            headers = new Headers.Builder();

        if(requireAuth)
            addAuthHeader(headers);

        Request request = new Request.Builder()
                .url(url)
                .headers(headers.build())
                .get()
                .build();

//        Call call = this.client.newCall(request);
        // Get a handler that can be used to post to the main thread
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {

                }
            }
        });

        return null;
    }
}
