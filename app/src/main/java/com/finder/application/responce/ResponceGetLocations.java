package com.finder.application.responce;

import android.os.AsyncTask;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;

public class ResponceGetLocations extends AsyncTask<Void, String, String> {

    private String latitude;
    private String longitude;
    private String email;
    private String token;
    private ResponceListener listener;

    public ResponceGetLocations(String latitude, String longitude, String email, String token, ResponceListener listener) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.email = email;
        this.token = token;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
//        OkHttpClient client = new OkHttpClient();
//        RequestBody formBody = new FormBody.Builder()
//                .add("latitude", latitude)
//                .add("longitude", longitude)
//                .add("email", email)
//                .add("token", token)
//                .add("distance", "1")
//                .build();
//        System.out.println("send data");
//        System.out.println(formBody);
//        System.out.println(formBody.toString());
//        System.out.println("send data");
//        Request request = new Request.Builder()
//                .url("http://kurum.ru/get/") // The URL to send the data to
//                .post(formBody)
//                .build();
//        try {
//            Response response = client.newCall(request).execute();
//            return response.body().string();
//        } catch (IOException e) {
//            return e.getMessage();
//        }
        HashMap<String, String> data = new HashMap<>();
        data.put("distance", 1+"");
        data.put("email", email);
        data.put("token", token);
        data.put("latitude", latitude);
        data.put("longitude", longitude);

        HttpPostClient httpPostClient = new HttpPostClient("http://kurum.ru/get/");
        for (String key : data.keySet()) {
            httpPostClient.add(key, data.get(key));
        }
        try {
            String result = httpPostClient.execute();
            System.out.println("result add update " + result);
            System.out.println(result);
            System.out.println("result add update " + result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        System.out.println("responce get " + s);
        listener.OnSuccess(s);
    }
}
