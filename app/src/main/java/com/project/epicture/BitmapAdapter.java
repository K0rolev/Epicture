package com.project.epicture;

import android.app.LauncherActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class BitmapAdapter extends BaseAdapter {

    private Context context_ = null;
    private String title_;

    public BitmapAdapter(Context context) {
        context_ = context;
    }

    ArrayList<Bitmap> myList = new ArrayList<>();
    ArrayList<String> myTitles = new ArrayList<>();
    Context context;

    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public Bitmap getItem(int position) {
        return myList.get(position);
    }

    public  String getTitle(int position) {
        return myTitles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return myList.indexOf(getItem(position));
    }

    public void add(Bitmap image, String title) {
        myList.add(image);
        myTitles.add(title);
    }

    public void addTitle(String title) {
        title_ = title;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Bitmap image = (Bitmap)getItem(position);
        String title = (String)getTitle(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context_).inflate(R.layout.uneimage, parent, false);
        }

        TextView tv = (TextView) convertView.findViewById(R.id.textView);
        ImageView iv = (ImageView)convertView.findViewById(R.id.imageView);
        iv.setImageBitmap(image);
        tv.setText(title);
        return convertView;
    }


}
