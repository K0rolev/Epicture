package com.project.epicture;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.project.epicture.API.LoginManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private AsyncFlickrJSONData task = null;
    private BottomNavigationView bottomNavigationView = null;
    private Button logout= null;
    private Button sub_us = null;
    private Button sub_bio = null;
    private EditText user_input = null;
    private EditText bio_input = null;
    private Context context;
    private WebView web;
    SharedPreferences pref;
    public LoginManager login = null;
    public String[] params = null;
    public TextView email_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity_page);
        logout = (Button)findViewById(R.id.logout);
        context = getApplicationContext();
        Button sub_us = (Button)findViewById(R.id.button6);
        Button sub_bio = (Button)findViewById(R.id.button5);
        user_input = (EditText) findViewById(R.id.plain_text_input);
        user_input.addTextChangedListener(textWatcher);
        bio_input = (EditText)findViewById(R.id.plain_text_input3);
        bio_input.addTextChangedListener(textWatcher);
        email_text = (TextView)findViewById(R.id.textView5);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        pref = getSharedPreferences("AppPref", MODE_PRIVATE);
        if (login == null) {
            Login();
        } else if (login.getLoginfo().is_logged != true){
            Login();
        }
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (login != null) {
                    login.Logout();
                }
                Login();

            }
        });

        sub_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (login != null) {
                    try {
                        login.setAccountInfo(user_input.getText().toString(), "username");
                        user_input.setHint(login.getLoginfo().account_username);
                        View view = getCurrentFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        sub_bio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (login != null) {
                    try {
                        login.setAccountInfo(bio_input.getText().toString(), "bio");
                        bio_input.setHint(login.getLoginfo().bio);
                        login.getAccountBase();
                        View view = getCurrentFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.activity_main_bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener( (item) -> {
            switch (item.getItemId()) {
                case R.id.action_recents:
                    Intent intent0 = new Intent(SettingsActivity.this, RecentActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent0);
                    break;
                case R.id.action_search:
                    Intent intent1 = new Intent(SettingsActivity.this, SearchActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent1);
                    break;
                case R.id.action_upload:
                    Intent intent2 = new Intent(SettingsActivity.this, UploadActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent2);
                    break;
            }
            return true;
        });
    }
    public void Login() {

        Dialog auth_dialog;
        auth_dialog = new Dialog(SettingsActivity.this);
        auth_dialog.setContentView(R.layout.auth_dialog);
        web = (WebView) auth_dialog.findViewById(R.id.webv);
        Map<String, String> extraHeaders = new HashMap<String, String>();
        extraHeaders.put("Content-Type","application/x-www-form-urlencoded");
        extraHeaders.put("Access-Control-Allow-Origin", "http://localhost");
        extraHeaders.put("Accept", "*/*");
        extraHeaders.put("Cache-Control", "no-cache");
        extraHeaders.put("Access-Control-Request-Headers", "authorization,cache-control,x-requested-with");
        extraHeaders.put("Origin", "http://localhost");
        web.getSettings().setJavaScriptEnabled(true);
        web.loadUrl(
                "https://api.imgur.com/oauth2/authorize?client_id=1186dbcde470502&response_type=token&state=READY", extraHeaders);
        web.setWebViewClient(new WebViewClient() {

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                Log.d("WESH", "request.getRequestHeaders()::"+request.getRequestHeaders());
                //Log.d("LES LOGS PUTAIN ",view.getSettings().toString());
                return null;
            }

            boolean authComplete = false;
            Intent resultIntent = new Intent();

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }
            String authCode;
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i("URL", url);
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
                    SettingsActivity.this.setResult(Activity.RESULT_OK, resultIntent);
                    setResult(Activity.RESULT_CANCELED, resultIntent);
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString("access_token", params[0]);
                    edit.putString("expires_in", params[1]);
                    edit.putString("token_type", params[2]);
                    edit.putString("refresh_token", params[3]);
                    edit.putString("account_username", params[4]);
                    edit.putString("account_id", params[5]);
                    edit.putBoolean("is_logged", true);
                    edit.commit();
                    auth_dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Hello " + pref.getString("account_username", null), Toast.LENGTH_SHORT).show();
                    login = new LoginManager(context);
                } else if (url.contains("error=access_denied")) {
                    Log.i("", "ACCESS_DENIED_HERE");
                    resultIntent.putExtra("token", authCode);
                    authComplete = true;
                    setResult(Activity.RESULT_CANCELED, resultIntent);
                    Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_SHORT).show();
                    auth_dialog.dismiss();
                }
                auth_dialog.dismiss();
                try {
                    login.getAccountInfo();
                    email_text.setText(login.getLoginfo().email);
                    login.getAccountBase();
                    user_input.setHint(login.getLoginfo().account_username);
                    bio_input.setHint(login.getLoginfo().bio);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        auth_dialog.show();
        auth_dialog.setCancelable(true);
    }

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
