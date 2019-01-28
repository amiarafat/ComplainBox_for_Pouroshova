package com.arafat.Pouroshova.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.arafat.complainbox.R;
import com.arafat.Pouroshova.main_page.LandingPageActivity;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class StartPageActivity extends AppCompatActivity {


    private static final String USER_PREF = "USER_INFO";

    private final static int REQUEST_CODE =99;
    public static final String GET_PHONE_FROM_FB_ACCOUNT_KIT = "https://graph.accountkit.com/v1.2/me/?access_token=";
    public static final String MOBILE_NUMBER_CHECK_API = "http://marvelbd.com/piams/check/";


    CardView btnLogin;
    private String countryCode="";
    private String nationalNumber="";
    private SharedPreferences spUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        btnLogin = findViewById(R.id.btnStart);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startLogin();
            }
        });


    }

    private void startLogin() {

        Intent in =new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE,AccountKitActivity.ResponseType.TOKEN);

        configurationBuilder.setDefaultCountryCode("BD");
        in.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,configurationBuilder.build());
        startActivityForResult(in,REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == REQUEST_CODE){

            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);

            Log.d("result::",loginResult.getAccessToken()+"");




            if (loginResult.getError() != null) {
                //toastMessage = loginResult.getError().getErrorType().getMessage();
                //Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
            } else if (loginResult.wasCancelled()) {
                //toastMessage = "Login Cancelled";
            } else {
                if (loginResult.getAccessToken() != null) {
                    //toastMessage = "Success:" + loginResult.getAccessToken().getAccountId();
                 /*   //startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    Log.d("Token", "" + loginResult.getAccessToken().getToken());
                    String secret = hmacDigest(loginResult.getAccessToken().getToken(), getResources().getString(R.string.fb_client_secret), "HmacSHA256");
                    Log.d("st::", secret);
                    if (secret.length() > 0) {
                        //showProgressDialog();
                        getPhoneNumberFromFacebookAccountKit(loginResult.getAccessToken().getToken(), secret);
                    }*/

                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(Account account) {
                            String accountKitId = account.getId();
                            PhoneNumber phoneNumber = account.getPhoneNumber();
                            String phoneNumberString = phoneNumber.toString();
                            Log.d("number:",phoneNumberString);

                            String prefix = phoneNumberString.substring(0, Math.min(phoneNumberString.length(), 4));
                            String national_number = phoneNumberString.substring(phoneNumberString.length()-10);
                            Log.d("number:",prefix+"--"+national_number);

                            getNumberCheck(prefix,national_number);
                        }

                        @Override
                        public void onError(AccountKitError accountKitError) {

                        }
                    });

                } else {

                }

            }


            /*if (result.getError() != null){

                Toast.makeText(this,""+result.getError().getErrorType().getMessage(),Toast.LENGTH_LONG).show();
                return;
            }
            else if(result.wasCancelled()){

                Toast.makeText(this,"Canceled",Toast.LENGTH_LONG).show();
                return;

            }else {


                //Toast.makeText(this,""+result.getAuthorizationCode()+"---"+result.getAccessToken(),Toast.LENGTH_LONG).show();
                startActivity(new Intent(this,MainPageActivity.class));
            }*/
        }

    }

    public static String hmacDigest(String msg, String keyString, String algo) {
        String digest = null;
        try {
            SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), algo);
            Mac mac = Mac.getInstance(algo);
            mac.init(key);

            byte[] bytes = mac.doFinal(msg.getBytes("ASCII"));

            StringBuffer hash = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xFF & bytes[i]);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
            digest = hash.toString();
        } catch (UnsupportedEncodingException e) {
        } catch (InvalidKeyException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        Log.d("st::", digest);
        return digest;
    }

    private void getPhoneNumberFromFacebookAccountKit(String fbAccToken, String secretHash) {
        Log.e("getPhoneNumber::", "called" + fbAccToken);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_PHONE_FROM_FB_ACCOUNT_KIT.concat(fbAccToken).concat("&appsecret_proof=" + secretHash),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("resFB::", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            Log.d("obj2::", obj.toString());
                            String phone = obj.getString("phone");
                            Log.d("phone2::", phone);
                            JSONObject obj2 = new JSONObject(phone);
                            countryCode = obj2.getString("country_prefix");
                            if (countryCode.equals("+88")) {
                                countryCode = "880";
                            }
                            nationalNumber = obj2.getString("national_number");
                            Log.d("number2::", countryCode + "-" + nationalNumber);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //getNumberCheck(countryCode,nationalNumber);


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.d("volleyError::", error.toString());
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getNumberCheck(final String countryCode, final String nationalNumber) {

        StringRequest request = new StringRequest(Request.Method.POST, MOBILE_NUMBER_CHECK_API, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("res::",response);

                try {

                    JSONObject jObj =new JSONObject(response);

                    String code =jObj.getString("code");
                    String uId =jObj.getString("uid");
                    Log.d("uid::",uId);

                    spUser = getApplicationContext().getSharedPreferences(USER_PREF, MODE_PRIVATE);
                    SharedPreferences.Editor editor = spUser.edit();
                    editor.putString("user_mobile", countryCode+nationalNumber);
                    editor.putString("user_id", uId);
                    editor.apply();



                    Log.d("mobilr::",spUser.getString("user_mobile",""));


                    Intent in =new Intent(StartPageActivity.this,LandingPageActivity.class);
                    startActivity(in);
                    finish();

                }catch (Exception e){


                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("perfix", countryCode);
                params.put("mobile_number", nationalNumber);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

    }
/*private void getKeyhash() {

        try {

            PackageInfo packageInfo = getPackageManager().getPackageInfo("com.arafat.complainbox",PackageManager.GET_SIGNATURES);

            for (Signature signature :packageInfo.signatures){

                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                Log.d("keyHash::",Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }

        }catch (Exception e){


        }
    }
*/


}
