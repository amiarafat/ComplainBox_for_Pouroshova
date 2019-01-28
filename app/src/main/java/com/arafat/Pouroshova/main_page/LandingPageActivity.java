package com.arafat.Pouroshova.main_page;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;

import com.arafat.Pouroshova.auth.StartPageActivity;
import com.arafat.complainbox.R;
import com.facebook.accountkit.AccountKit;

public class LandingPageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Button btnGenInfo;
    ImageButton btnMainCall;
    private static final int MY_PERMISSION_REQUEST_CALL = 101;

    private static final String USER_PREF = "USER_INFO";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        btnGenInfo = findViewById(R.id.btnGenInfo);
        btnMainCall = findViewById(R.id.btnMainCall);

        btnGenInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in  =new Intent(LandingPageActivity.this, ComplainBoxActivity.class);
                startActivity(in);
            }
        });

        btnMainCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPermissionCallGranted()) {
                    callNumber("8801816810643");
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_complain) {
            Intent in  =new Intent(LandingPageActivity.this, ComplainBoxActivity.class);
            startActivity(in);
        } else if (id == R.id.nav_profile) {

        } else if (id == R.id.nav_logout) {

            AccountKit.logOut();

            SharedPreferences spUser = getApplicationContext().getSharedPreferences(USER_PREF, MODE_PRIVATE);
            SharedPreferences.Editor editor = spUser.edit();
            editor.clear();
            editor.commit();

            Intent in =new Intent(LandingPageActivity.this, StartPageActivity.class);
            startActivity(in);
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void callNumber(String number) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * check call permission
     *
     * @param @null
     */
    private boolean isPermissionCallGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG", "Permission is granted");
                return true;
            } else {
                Log.v("TAG", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSION_REQUEST_CALL);
                return false;
            }

        } else {
            //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG", "Permission is granted");
            return true;
        }

    }

    /**
     * after getting permission
     *
     * @param requestCode : 1001 => MY_PERMISSION_REQUEST_CALL
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CALL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("permission", "MY_PERMISSION_REQUEST_CALL :: " + "granted");
                            callNumber("8801816810643");

                } else {
                    Log.d("permission", "MY_PERMISSION_REQUEST_CALL :: " + "not granted");
                }
                break;
            }
        }
    }
}
