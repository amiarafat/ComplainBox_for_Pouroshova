package com.arafat.Pouroshova.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.arafat.Pouroshova.main_page.LandingPageActivity;
import com.arafat.complainbox.R;

public class RegisterActivity extends AppCompatActivity {

    EditText etName,etEmail,etLocation;
    Button btnRegister;

    SharedPreferences spUser;
    String USER_PREF = "USER_INFO";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        initializeView();



        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = etName.getText().toString();
                String email = etEmail.getText().toString();
                String location =etLocation.getText().toString();

                if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(location)){


                }else {

                    RegisterUser(name,email,location);
                }
            }
        });

    }

    private void RegisterUser(String name, String email, String location) {


        spUser = getApplicationContext().getSharedPreferences(USER_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = spUser.edit();
        editor.putString("user_name", name);
        editor.putString("user_email", email);
        editor.putString("user_location", location);
        editor.apply();

        Intent in =new Intent(RegisterActivity.this, LandingPageActivity.class);
        startActivity(in);
    }

    private void initializeView() {

        etName = findViewById(R.id.etRName);
        etEmail = findViewById(R.id.etREmail);
        etLocation = findViewById(R.id.etRLocation);
        btnRegister = findViewById(R.id.btnSignUp);
    }

}
