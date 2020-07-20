package com.finder.application.responce;

import android.os.AsyncTask;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;

public class ResponceAdd extends AsyncTask<Void, String, String> {

    private String user_id;
    private String unic;
    private String author_name;
    private String author_avatar;
    private String title;
    private String description;
    private String location;
    private String is_public;
    private String latitude;
    private String longitude;
    private ResponceListener listener;

    public ResponceAdd(String user_id, String author_name, String author_avatar, String unic, String title, String description, String location, String is_public, String latitude, String longitude, ResponceListener listener) {
        this.user_id = user_id;
        this.author_name = author_name;
        this.author_avatar = author_avatar;
        this.unic = unic;
        this.title = title;
        this.description = description;
        this.location = location;
        this.is_public = is_public;
        this.latitude = latitude;
        this.longitude = longitude;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        return Send(user_id,
                author_name,
                author_avatar,
                unic,
                title,
                description,
                location,
                is_public,
                latitude,
                longitude);
    }

    public static String Send(String user_id,
                              String author_name,
                              String author_avatar,
                              String unic,
                              String title,
                              String description,
                              String location,
                              String is_public,
                              String latitude,
                              String longitude) {

        HashMap<String, String> data = new HashMap<>();
        data.put("user_id", user_id);
        data.put("author_name", author_name);
        data.put("author_avatar", author_avatar);
        data.put("unic", unic);
        data.put("title", title);
        data.put("description", description);
        data.put("is_public", is_public);
        data.put("location", location);
        data.put("latitude", latitude);
        data.put("longitude", longitude);

        HttpPostClient httpPostClient = new HttpPostClient("http://kurum.ru/save/");
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
        listener.OnSuccess(s);
    }
}
