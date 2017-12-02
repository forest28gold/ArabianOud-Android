package com.ecommerce.dell.mcommerce.jsonparser;


import android.app.Application;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

public class JsonParser extends Application {

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    public JsonParser() {
        Log.d("parser","constructor" );
    }

    public JSONObject makeHttpRequest(String url,  List<NameValuePair> params) {

        Log.d("url",url);

        try {
            // check for request method
            String paramString = URLEncodedUtils.format(params, "UTF-8");
            Log.d("Parser", paramString);
            url += "?" + paramString;
            Log.d("url",url);

            URL link = new URL(url);
            HttpURLConnection con = (HttpURLConnection) link.openConnection();
            con.setDoInput(true);
            is = con.getInputStream();

        } catch (Exception e) {

            Log.d("parser" , e.toString());
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            Log.d("parser", json);

        } catch (Exception e) {
            Log.d("parser", "Error converting result " + e.toString());
            return null;
        }

        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.d("JSON Parser", "Error parsing data " + e.toString());
            return null;
        }

        return jObj;
    }
}