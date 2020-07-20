package com.finder.application.responce;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class HttpPostClient {
    private ArrayList<NameValuePair> nameValuePairs;
    private HttpPost httppost;
    private HttpClient httpclient = new DefaultHttpClient();
    private InputStream is = null;

    public HttpPostClient(String url) {
        this.httppost = new HttpPost(url);
        this.nameValuePairs = new ArrayList();
    }

    public void add(String key, String value) {
        this.nameValuePairs.add(new BasicNameValuePair(key, value));
    }

    public String execute()  {
        try {
            this.httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
            httpclient.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
            httpclient.getParams().setParameter("http.protocol.content-charset", "UTF-8");
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;

            while((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            this.is.close();
            String res = sb.toString();
            return res;
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public void setParams(List<BasicNameValuePair> pairs) {
        nameValuePairs.addAll(pairs);
    }

    public ArrayList<NameValuePair> getParams() { return  nameValuePairs; }
}
