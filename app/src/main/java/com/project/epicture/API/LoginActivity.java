package com.project.epicture.API;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.project.epicture.R;
import com.project.epicture.RecentActivity;
import com.project.epicture.SearchActivity;
import com.project.epicture.UploadActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    WebView web;
    SharedPreferences pref;
    public String[] params = null;
    public Button button;

    static final SimpleDateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy kk:mm:ss", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_page);
        pref = getSharedPreferences("AppPref", MODE_PRIVATE);
        button = (Button)findViewById(R.id.button3);

        button.setOnClickListener(new View.OnClickListener() {
            Dialog auth_dialog;
            @Override
            public void onClick(View arg0) {
                Date date = new Date();
                final String dateString = formatter.format(date);
                auth_dialog = new Dialog(LoginActivity.this);
                auth_dialog.setContentView(R.layout.auth_dialog);
                web = (WebView)auth_dialog.findViewById(R.id.webv);
                Map<String, String> extraHeaders = new HashMap<String, String>();
                extraHeaders.put("Content-Type","application/x-www-form-urlencoded");
                extraHeaders.put("Access-Control-Allow-Origin", "http://localhost");
                extraHeaders.put("Accept", "*/*");
                extraHeaders.put("Cache-Control", "no-cache");
                extraHeaders.put("Access-Control-Request-Headers", "authorization,cache-control,x-requested-with");
                extraHeaders.put("Origin", "http://localhost");
                web.getSettings().setJavaScriptEnabled(true);
                web.loadUrl("https://api.imgur.com/oauth2/authorize?client_id=1186dbcde470502&response_type=token&state=APPLICATION_STATE", extraHeaders);
                web.setWebViewClient(new WebViewClient() {

                    boolean authComplete = false;
                    Intent resultIntent = new Intent();

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);

                    }
                    String authCode;
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Log.i("URLO", url);
                        if (url.contains("READY") && authComplete != true) {
                            Uri uri = Uri.parse(url);
                            Log.i("CIO ", uri.toString());
                            String result = uri.toString();
                            result = result.substring(result.indexOf("access_token=") + 13, result.indexOf("&"));
                            Log.i("TEST", result);
                            authCode = result;
                            params = uri.getFragment().split("&");
                            for (int i = 0; i < params.length ; i++) {
                                params[i] = params[i].substring(params[i].indexOf("=") + 1);
                                Log.i("PARAMS", params[i] + "\n");
                            }
                            Log.i("TOKEN", authCode);
                            authComplete = true;
                            resultIntent.putExtra("token", authCode);
                            LoginActivity.this.setResult(Activity.RESULT_OK, resultIntent);
                            setResult(Activity.RESULT_CANCELED, resultIntent);
                            SharedPreferences.Editor edit = pref.edit();
                            edit.putString("access_token", params[0]);
                            edit.putString("expires_in", params[1]);
                            edit.putString("token_type", params[2]);
                            edit.putString("refresh_token", params[3]);
                            edit.putString("account_username", params[4]);
                            edit.putString("account_id", params[5]);
                            edit.commit();
                            //auth_dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Authorization Code is: " + authCode, Toast.LENGTH_SHORT).show();
                        }/* else if (url.contains("error=access_denied")) {
                            Log.i("", "ACCESS_DENIED_HERE");
                            resultIntent.putExtra("token", authCode);
                            authComplete = true;
                            setResult(Activity.RESULT_CANCELED, resultIntent);
                            Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_SHORT).show();
                            auth_dialog.dismiss();
                        }*/
                        //auth_dialog.dismiss();
                        return true;
                    }
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                    }
                });
                auth_dialog.show();
                auth_dialog.setTitle("Authorize Learn2Crack");
                auth_dialog.setCancelable(true);
            }
        });
        }

}
