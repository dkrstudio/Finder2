package com.finder.application.responce;

import android.os.AsyncTask;

import java.util.HashMap;

public class ResponceTest extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... strings) {
        HashMap<String, String> data = new HashMap<>();
        data.put("login", strings[0]);

        HttpPostClient httpPostClient = new HttpPostClient("http://kurum.ru/test2.php");
        for (String key : data.keySet()) {
            httpPostClient.add(key, data.get(key));
        }
        try {
            String result = httpPostClient.execute();
            return result;
        } catch (Exception e) {
            System.out.println("ResponceTest error " + e.getMessage());
            System.out.println("ResponceTest error " + e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        System.out.println("result ResponceTest");
        System.out.println(result);
        System.out.println("result ResponceTest");
    }
}
