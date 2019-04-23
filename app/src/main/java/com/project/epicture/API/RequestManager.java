package com.project.epicture.API;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import com.project.epicture.AsyncBitmapDownloader;
import com.project.epicture.BitmapAdapter;
import com.project.epicture.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class RequestManager extends AsyncTask<String, Void, JSONObject> {

    private LoginManager loginManager_;
    public RequestManager(LoginManager loginManager) { loginManager_ = loginManager;}
    private String client_id = "1186dbcde470502";
    private String client_secret = "043c563fc458d6f154ecf5bf4150c572962b4e11";

    @Override
    protected JSONObject doInBackground(String... strings) {

        URL url = null;
        HttpURLConnection urlConnection = null;
        String result = null;
        try {
            StringBuilder tokenUri=new StringBuilder("refresh_token=");
            tokenUri.append(URLEncoder.encode(loginManager_.getLoginfo().refresh_token,"UTF-8"));
            tokenUri.append("&client_id=");
            tokenUri.append(URLEncoder.encode(client_id,"UTF-8"));
            tokenUri.append("&client_secret=");
            tokenUri.append(URLEncoder.encode(client_secret,"UTF-8"));
            tokenUri.append("&grant_type=");
            tokenUri.append(URLEncoder.encode("refresh_token","UTF-8"));

            url = new URL(strings[0] + tokenUri);
            Log.i("URL", url.toString());
            urlConnection = (HttpURLConnection) url.openConnection(); // Open

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");

            InputStream in = new BufferedInputStream(urlConnection.getInputStream()); // Stream

            result = readStream(in); // Read stream
        }
        catch (MalformedURLException e) { e.printStackTrace(); }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }

        JSONObject json = null;
        try {
            Log.i("ERREUR", result);
            json = new JSONObject(result);
            Log.i("GROSSSE SHEISSE", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return json; // returns the result
    }

    @Override
    protected void onPostExecute(JSONObject s) {

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
