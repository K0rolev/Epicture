package com.project.epicture;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SearchActivity extends AppCompatActivity {

    private EditText search_bar = null;
    private ImageButton search_button = null;
    private AsyncFlickrJSONData task = null;
    private BottomNavigationView bottomNavigationView = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity_page);

        search_bar = (EditText) findViewById(R.id.plain_text_input);
        search_bar.addTextChangedListener(textWatcher);
        search_button = (ImageButton) findViewById(R.id.imageButton);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.activity_main_bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener( (item) -> {
            switch (item.getItemId()) {
                case R.id.action_recents:
                    Intent intent0 = new Intent(SearchActivity.this, RecentActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent0);
                    break;
                case R.id.action_search:
                    break;
                case R.id.action_upload:
                    Intent intent2 = new Intent(SearchActivity.this, UploadActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent2);
                    break;
                case R.id.action_settings:
                    Intent intent3 = new Intent(SearchActivity.this, SettingsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent3);
                    break;
            }
            return true;
        });

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (task != null)
                        task.cancel(true);
                    task = new AsyncFlickrJSONData(SearchActivity.this);
                    task.execute("https://api.imgur.com/3/gallery/t/"
                            + URLEncoder.encode(search_bar.getText().toString(), "UTF-8"));

                    View view = getCurrentFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                } catch (UnsupportedEncodingException e) {
                    System.err.println(e);
                }
            }
        });

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

    private void configureBottomView(){
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> updateMainFragment(item.getItemId()));
    }

    private Boolean updateMainFragment(Integer integer){
        Log.i("CIO","EHO");
        switch (integer) {
            case R.id.action_upload:
                setContentView(R.layout.growingtext);
                break;
            /*case R.id.action_logo:
                //this.mainFragment.updateDesignWhenUserClickedBottomView(MainFragment.REQUEST_LOGO);
                break;
            case R.id.action_landscape:
                //this.mainFragment.updateDesignWhenUserClickedBottomView(MainFragment.REQUEST_LANDSCAPE);
                break;*/
        }
        return true;
    }
}
