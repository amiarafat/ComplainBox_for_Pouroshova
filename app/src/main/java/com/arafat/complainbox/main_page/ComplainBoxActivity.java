package com.arafat.complainbox.main_page;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.arafat.complainbox.MapsActivity;
import com.arafat.complainbox.R;

public class ComplainBoxActivity extends AppCompatActivity implements View.OnClickListener {


    ImageView ivHouse,ivStructure,ivWater,ivTerminal,ivBridge,ivRoad,ivOthers;
    Button btnMLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain_box);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView();


    }

    private void initView() {

        ivHouse = findViewById(R.id.ivHousehold);
        ivHouse.setOnClickListener(this);
        ivStructure = findViewById(R.id.ivStructure);
        ivStructure.setOnClickListener(this);
        ivWater = findViewById(R.id.ivWater);
        ivWater.setOnClickListener(this);
        ivTerminal = findViewById(R.id.ivTerminal);
        ivTerminal.setOnClickListener(this);
        ivBridge = findViewById(R.id.ivBridge);
        ivBridge.setOnClickListener(this);
        ivRoad = findViewById(R.id.ivRoad);
        ivRoad.setOnClickListener(this);
        ivOthers = findViewById(R.id.ivOthers);
        ivOthers.setOnClickListener(this);

        btnMLog = findViewById(R.id.btnMLog);
        btnMLog.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(v == ivHouse || v == ivBridge || v == ivOthers || v == ivRoad || v == ivStructure || v == ivTerminal || v == ivWater){

            Intent in  =new Intent(ComplainBoxActivity.this, MapsActivity.class);
            startActivity(in);
        }

        else  if(v == btnMLog){

            Intent in  =new Intent(ComplainBoxActivity.this, MessageLogActivity.class);
            startActivity(in);
        }

    }
}
