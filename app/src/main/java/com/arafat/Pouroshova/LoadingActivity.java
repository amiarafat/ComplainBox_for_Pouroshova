package com.arafat.Pouroshova;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.arafat.Pouroshova.auth.StartPageActivity;
import com.arafat.Pouroshova.main_page.LandingPageActivity;
import com.arafat.complainbox.R;

public class LoadingActivity extends AppCompatActivity {

    private static final String USER_PREF = "USER_INFO";
    SharedPreferences sp;
    private SharedPreferences spUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);




        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                sp  = getApplicationContext().getSharedPreferences(USER_PREF, MODE_PRIVATE);
                Log.d("id::",sp.getString("user_id",""));


                if(sp.contains("user_id") && !TextUtils.isEmpty(sp.getString("user_id",""))){

                    Intent in =new Intent(LoadingActivity.this, LandingPageActivity.class);
                    startActivity(in);
                    finish();
                }else {
                    Intent in =new Intent(LoadingActivity.this, StartPageActivity.class);
                    startActivity(in);
                    finish();
                }

                }
        }, 3000);




    }
}
