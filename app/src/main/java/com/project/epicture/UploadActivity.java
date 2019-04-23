package com.project.epicture;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class UploadActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView = null;
    SharedPreferences pref;
    Context context = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.upload_activity_page);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        pref = getSharedPreferences("AppPref", MODE_PRIVATE);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.activity_main_bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener((item) -> {
            switch (item.getItemId()) {
                case R.id.action_recents:
                    Intent intent0 = new Intent(UploadActivity.this, RecentActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent0);
                    break;
                case R.id.action_search:
                    Intent intent1 = new Intent(UploadActivity.this, SearchActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent1);
                    break;
                case R.id.action_upload:
                    break;
                case R.id.action_settings:
                    Intent intent3 = new Intent(UploadActivity.this, SettingsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent3);
                    break;
            }
            return true;
        });
    }

}
