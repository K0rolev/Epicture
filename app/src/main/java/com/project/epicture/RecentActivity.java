package com.project.epicture;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

public class RecentActivity extends AppCompatActivity {

    private AsyncFlickrJSONData task = null;
    private BottomNavigationView bottomNavigationView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recent_activity_page);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.activity_main_bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener( (item) -> {
            switch (item.getItemId()) {
                case R.id.action_recents:
                    break;
                case R.id.action_search:
                    Intent intent1 = new Intent(RecentActivity.this, SearchActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent1);
                    break;
                case R.id.action_upload:
                    Intent intent2 = new Intent(RecentActivity.this, UploadActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent2);
                    break;
                case R.id.action_settings:
                    Intent intent3 = new Intent(RecentActivity.this, SettingsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent3);
                    break;
            }
            return true;
        });

        if (task != null)
            task.cancel(true);
        task = new AsyncFlickrJSONData(RecentActivity.this);
        task.execute("https://api.imgur.com/3/gallery/hot/viral/0.json");
    }

}
