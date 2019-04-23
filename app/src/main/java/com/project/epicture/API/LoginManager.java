package com.project.epicture.API;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.project.epicture.R;
import com.project.epicture.UploadActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class LoginManager{

    private boolean is_logged = false;
    WebView web = null;
    SharedPreferences pref;
    public Dialog auth_dialog = null;
    private LoginInformation loginfo = null;
    private String client_id = "1186dbcde470502";
    private String client_secret = "043c563fc458d6f154ecf5bf4150c572962b4e11";

    public class LoginInformation {
        public String access_token = null;
        public String expires_in = null;
        public String token_type = null;
        public String refresh_token = null;
        public String account_username = null;
        public String account_id = null;
        public boolean is_logged = false;
        public String email = null;
        public String bio = null;

        public LoginInformation(boolean is_logged, String... args) {
            this.access_token = args[0];
            this.expires_in = args[1];
            this.token_type = args[2];
            this.refresh_token = args[3];
            this.account_username = args[4];
            this.account_id = args[5];
            this.is_logged = is_logged;
            this.bio = "Aucune Bio";
        }

        public void close() {
            this.access_token = null;
            this.expires_in = null;
            this.token_type = null;
            this.refresh_token = null;
            this.account_username = null;
            this.account_id = null;
            this.is_logged = false;
        }
    }

    public LoginManager(Context context){
        Log.i("ERROR","i'm here");
        pref = context.getSharedPreferences("AppPref", MODE_PRIVATE);
        Map<String, ?> allEntries = pref.getAll();
        loginfo = new LoginInformation(pref.getBoolean("is_logged", false),
                pref.getString("access_token", null),
                pref.getString("expires_in", null),
                pref.getString("token_type", null),
                pref.getString("refresh_token", null),
                pref.getString("account_username", null),
                pref.getString("account_id", null));
        Log.i("Login Info", pref.getString("account_username", null));
        //web = new WebView(getApplicationContext());
    }

    public LoginInformation getLoginfo() {
        if (loginfo != null)
            return loginfo;
        else
            return null;
    }

    public void RefreshToken() throws IOException {
        URL url = new URL("https://api.imgur.com/oauth2/token");
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("refresh_token", loginfo.refresh_token);
        params.put("client_id", client_id);
        params.put("client_secret", client_secret);
        params.put("grant_type", "refresh_token");

        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String,Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);

        Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

        StringBuilder sb = new StringBuilder();
        for (int c; (c = in.read()) >= 0;)
            sb.append((char)c);
        String response = sb.toString();
        Log.i("BFMTV", response);
        try {
            JSONObject json = new JSONObject(response);
            loginfo.access_token = json.getString("access_token");
            loginfo.expires_in = json.getString("expires_in");
            loginfo.token_type = json.getString("refresh_token");
            SharedPreferences.Editor edit = pref.edit();
            edit.putString("access_token", loginfo.access_token);
            edit.putString("expires_in", loginfo.expires_in);
            edit.putString("refresh_token", loginfo.refresh_token);
            edit.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getAccountInfo() throws Exception{
        URL url = new URL("https://api.imgur.com/3/account/me/settings");

        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + loginfo.access_token);

        Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

        StringBuilder sb = new StringBuilder();
        for (int c; (c = in.read()) >= 0;)
            sb.append((char)c);
        String response = sb.toString();
        try {
            JSONObject json = new JSONObject(response);
            loginfo.email = json.getJSONObject("data").getString("email");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getAccountBase() throws Exception{
        URL url = new URL("https://api.imgur.com/3/account/" + loginfo.account_username);

        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Client-ID " + client_id);

        Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

        StringBuilder sb = new StringBuilder();
        for (int c; (c = in.read()) >= 0;)
            sb.append((char)c);
        String response = sb.toString();
        Log.i("Account base", response);
        try {
            JSONObject json = new JSONObject(response);
            if (json.getJSONObject("data").getString("bio") != null)
                loginfo.bio = json.getJSONObject("data").getString("bio");
                loginfo.account_username = json.getJSONObject("data").getString("url");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setAccountInfo (String input, String parameter) throws Exception {

        URL url = new URL("https://api.imgur.com/3/account/" + loginfo.account_username+ "/settings");
        Map<String,Object> params = new LinkedHashMap<>();
        params.put(parameter, input);

        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String,Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + loginfo.access_token);
        conn.setRequestProperty("Content-Type", "application/form-data");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);

        Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

        StringBuilder sb = new StringBuilder();
        for (int c; (c = in.read()) >= 0;)
            sb.append((char)c);
        String response = sb.toString();
        Log.i("BFMTV", response);
        try {
            JSONObject json = new JSONObject(response);
            if (json.getBoolean("success") == true) {
                if (parameter == "username")
                    loginfo.account_username = input;
                else if (parameter == "bio")
                    loginfo.bio = input;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean Logout() {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookies(null);
        SharedPreferences.Editor edit = pref.edit();
        edit.clear();
        edit.commit();
        loginfo.close();
        return true;
    }

    private String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is),1000);
        for (String line = r.readLine(); line != null; line =r.readLine()){
            System.out.println(line + "                ");
            sb.append(line);
        }
        is.close();

        // Extracting the JSON object from the String
        Log.i("CIOPO ", "START :" + sb.toString());
        return sb.toString();
    }

}
