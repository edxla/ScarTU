package com.mobiletracker.scarTU.includes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mobiletracker.scarTU.R;

public class MyToolbar {

    //Metodo static
    public static  void  show(AppCompatActivity activity, String title, boolean upButton)
    {
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle(title);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
    }


}
