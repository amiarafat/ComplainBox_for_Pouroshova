package com.arafat.complainbox;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.logging.LogManager;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 100;
    private static final int VIDEO_REQUEST_CODE = 101;
    private static final int REQUEST_CAMERA = 102;

    LocationManager locationManager;
    boolean GpsStatus = false;

    private static final String TAG = MainActivity.class.getSimpleName();
    Double lat,lng;

    TextView tvAdddress;
    Button btnAddMidea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Firebase Apatoto OFF
        //firebaseCall();

        initializeView();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }


        if(GpsStatus){


            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                if(checkLocationPermission()) {
                    getCurrentLocation();
                }

            }else {
                getCurrentLocation();
            }


        }else {

            displayLocationSettingsRequestThis();
        }

        btnAddMidea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //recordVideo();
                chooseMedia();
            }
        });

    }


    private void chooseMedia(){

        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
        final View sheetView = getLayoutInflater().inflate(R.layout.media_choice_dialog, null);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.setCancelable(false);
        mBottomSheetDialog.show();

        TextView txtViewCancelDialog = (TextView) sheetView.findViewById(R.id.tvBSCancel);

        ImageView imgViewImage = (ImageView) sheetView.findViewById(R.id.imgViewImage);
        ImageView imgViewVideo = (ImageView) sheetView.findViewById(R.id.imageViewVideo);

        txtViewCancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
            }
        });
        imgViewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mBottomSheetDialog.dismiss();

            }
        });

        imgViewVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordVideo();
                mBottomSheetDialog.dismiss();
            }
        });

    }

    private void recordVideo() {

        Intent in =new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        File v_file = getFile();
        Uri uri = Uri.fromFile(v_file);

        in.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        in.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);

        startActivityForResult(in,VIDEO_REQUEST_CODE);
    }



    private File getFile() {

        File folder = new File("sdcard/myfolder");
        if(!folder.exists()){
            folder.mkdir();
        }

        File video_file= new File(folder,"complain.mp4");
        return video_file;
    }

    private void initializeView() {

        tvAdddress = findViewById(R.id.tvAddress);
        btnAddMidea = findViewById(R.id.btnAddMedia);
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                    }

                    getCurrentLocation();

                } else {

                    checkLocationPermission();
                }
                return;
            }

        }
    }



    private void getCurrentLocation() {
        Log.d(TAG + " getCurrentLocation :: ", "called");
        SmartLocation.with(MainActivity.this).location().oneFix().start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                lat = location.getLatitude();
                lng = location.getLongitude();
                Log.d(TAG + " location::", lat + "---" + lng);

                getAddress(lat,lng);
            }
        });

    }

    private void getAddress (double lat, double lng){

        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

        tvAdddress.setText(address);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void displayLocationSettingsRequestThis() {
        Log.d(TAG, "displayLocationSettingsRequestThis called");
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(MainActivity.this).addApi(LocationServices.API).build();
        googleApiClient.connect();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        Log.d("loc::", "here satisfied");

                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                            if(checkLocationPermission()) {
                                getCurrentLocation();
                            }

                        }else {
                            getCurrentLocation();
                        }


                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                        Log.d("loc::", "here canceld");
                        try {
                            status.startResolutionForResult(MainActivity.this, MY_PERMISSIONS_REQUEST_LOCATION);
                            Log.d("loc::", "here required");

                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }

                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        displayLocationSettingsRequestThis();

                        break;
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("reId::", requestCode + "");

        if(requestCode == MY_PERMISSIONS_REQUEST_LOCATION){

            Log.d("locR::", "Loc Request");

            switch (resultCode) {
                case Activity.RESULT_OK: {
                    getCurrentLocation();
                    Log.d("locR::", "Loc Request Approved");
                    break;
                }
                case Activity.RESULT_CANCELED: {
                    Log.d("locR::", "Loc Request not Approved");
                    displayLocationSettingsRequestThis();
                    break;
                }
                default: {
                    break;
                }
            }
        }

        if (requestCode == VIDEO_REQUEST_CODE){

            if (resultCode == RESULT_OK){

                Log.d("result::","Video Captured");


            }
        }
    }

}
