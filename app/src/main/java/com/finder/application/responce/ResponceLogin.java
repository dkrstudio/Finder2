package com.finder.application.responce;

import android.os.AsyncTask;
import okhttp3.*;

import java.io.IOException;

public class ResponceLogin extends AsyncTask<Void, String, String> {

    private String latitude;
    private String longitude;
    private String email;
    private String token;
    private String name;
    private String avatar;
    private ResponceListener listener;

    public ResponceLogin(String latitude, String longitude, String email, String token, String name, String avatar, ResponceListener listener) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.email = email;
        this.token = token;
        this.name = name;
        this.avatar = avatar;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        System.out.println("Run send data login");
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("latitude", latitude)
                .add("longitude", longitude)
                .add("email", email)
                .add("token", token)
                .add("name", name)
                .add("avatar", avatar)
                .build();
        System.out.println("send data");
        System.out.println(formBody);
        System.out.println(formBody.toString());
        System.out.println("send data");
        Request request = new Request.Builder()
                .url("http://kurum.ru/login/") // The URL to send the data to
                .post(formBody)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        listener.OnSuccess(s);
    }
}
