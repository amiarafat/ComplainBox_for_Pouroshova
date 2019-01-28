package com.arafat.Pouroshova.main_page;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.arafat.complainbox.R;

public class MainPageActivity extends AppCompatActivity {

    Button btnGenInfo;
    ImageButton btnMainCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnGenInfo = findViewById(R.id.btnGenInfo);
        btnMainCall = findViewById(R.id.btnMainCall);

        btnGenInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in  =new Intent(MainPageActivity.this, ComplainBoxActivity.class);
                startActivity(in);
            }
        });


        btnMainCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isCallPermissionGranted()){

                    if (Build.VERSION.SDK_INT < 23) {
                        callStart();
                    }else {

                        if (ActivityCompat.checkSelfPermission(MainPageActivity.this,
                                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

                            callStart();
                        }else {
                            final String[] PERMISSIONS_STORAGE = {Manifest.permission.CALL_PHONE};
                            //Asking request Permissions
                            ActivityCompat.requestPermissions(MainPageActivity.this, PERMISSIONS_STORAGE, 9);
                        }
                    }
                }
            }
        });


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean permissionGranted = false;
        switch(requestCode){
            case 9:
                permissionGranted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                break;
        }
        if(permissionGranted){
            callStart();
        }else {

        }
    }

    private void callStart() {

        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "01977443939"));
        startActivity(intent);
    }

    private boolean isCallPermissionGranted() {
        return false;
    }

}
