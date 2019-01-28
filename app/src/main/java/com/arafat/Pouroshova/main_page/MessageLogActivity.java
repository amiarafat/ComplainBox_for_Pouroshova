package com.arafat.Pouroshova.main_page;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.arafat.complainbox.R;

import java.util.ArrayList;
import java.util.Arrays;

public class MessageLogActivity extends AppCompatActivity {


    ArrayList<String> dateArray =new ArrayList<>(Arrays.asList("০২/১২/২০১৮","০৪/১২/২০১৮","১১/১০/২০১৮"));
    ArrayList<String> IdArray =new ArrayList<>(Arrays.asList("১","২","৩"));
    ArrayList<String> SubArray =new ArrayList<>(Arrays.asList("রাস্তা","কন্সট্রাকশন","পানি"));

    ListView lvMLog;
    MLogAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_log);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        lvMLog = findViewById(R.id.lvMLog);

        adapter = new MLogAdapter(this,dateArray,IdArray,SubArray);
        lvMLog.setAdapter(adapter);

    }

}
