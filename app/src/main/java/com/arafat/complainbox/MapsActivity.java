package com.arafat.complainbox;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.circulardialog.CDialog;
import com.example.circulardialog.extras.CDConstants;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 100;
    private static final int VIDEO_REQUEST_CODE = 101;
    private static final int REQUEST_CAMERA = 102;
    private static final int MY_PERMISSION_REQUEST_CAMERA = 103;

    LocationManager locationManager;
    boolean GpsStatus = false;

    private static final String TAG = MainActivity.class.getSimpleName();
    Double lat,lng;

    TextView tvAdddress;
    Button btnAddMidea;
    ImageView ivImage;

    LinearLayout llMap;
    File v_file;
    private ProgressDialog mProgressDialog;

    EditText etDate;
    Calendar myCalender;
    DatePickerDialog.OnDateSetListener date;

    Button btnComplainSubmit;

    private String complainuploadUrl ="http://orapal689.com/piams/griev_img/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);

        initializeView();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }

        showProgressDialog();


        btnAddMidea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //recordVideo();
                //chooseMedia();
                captureImage();
            }
        });


        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DatePickerDialog(MapsActivity.this, date, myCalender
                        .get(Calendar.YEAR), myCalender.get(Calendar.MONTH),
                        myCalender.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                Log.d("mo::",month+"--"+year+"--"+dayOfMonth);

                myCalender.set(Calendar.YEAR,year);
                myCalender.set(Calendar.MONTH,month);
                myCalender.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                updateView( year,month+1,dayOfMonth);
            }
        };


        btnComplainSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                showProgressDialog();
                UploadComplain();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

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
                if (isPermissionCameraGranted()) {
                    captureImage();
                }
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
    private boolean isPermissionCameraGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG", "Permission is granted");
                captureImage();
                return true;
            } else {
                Log.v("TAG", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSION_REQUEST_CAMERA);
                return false;
            }

        } else {
            //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG", "Permission is granted");
            return true;
        }
    }


    private void captureImage() {
        PackageManager pm = getPackageManager();

        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {

            Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

            i.putExtra(MediaStore.EXTRA_OUTPUT, MyFileContentProvider.CONTENT_URI);

            startActivityForResult(i, REQUEST_CAMERA);
        } else {

            Toast.makeText(getBaseContext(), "Camera is not available", Toast.LENGTH_LONG).show();
        }
    }

    private void recordVideo() {

        Intent in =new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        v_file = getFile();
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("অবহিতকরন");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));


        myCalender = Calendar.getInstance();

        tvAdddress = findViewById(R.id.tvAddress);
        btnAddMidea = findViewById(R.id.btnAddMedia);
        ivImage = findViewById(R.id.ivImage);
        llMap = findViewById(R.id.llMap);

        etDate = findViewById(R.id.etComplainDate);
        etDate.setFocusable(false);
        Date date=myCalender. getTime();
        updateView(myCalender.get(Calendar.YEAR),myCalender.get(Calendar.MONTH)+1,myCalender.get(Calendar.DAY_OF_MONTH));

        btnComplainSubmit = findViewById(R.id.btnComplainSubmit);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Getting Location Info...");
    }


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
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

            case MY_PERMISSION_REQUEST_CAMERA:{
                if (grantResults.length > 0 && grantResults[0] ==   PackageManager.PERMISSION_GRANTED) {
                    captureImage();
                } else {
                    Log.d(TAG, "REQUEST_CAMERA :: " + "not granted");
                }
                break;
            }

        }
    }



    private void getCurrentLocation() {
        Log.d(TAG + " getCurrentLocation :: ", "called");
        SmartLocation.with(MapsActivity.this).location().oneFix().start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                lat = location.getLatitude();
                lng = location.getLongitude();
                Log.d(TAG + " location::", lat + "---" + lng);
                hideProgressDialog();
                getAddress(lat,lng);
                getMapView(lat,lng);
            }
        });

    }

    private void getMapView(Double lat, Double lng) {
        LatLng sydney = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Complain Place"));
        CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(14).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(MapsActivity.this).addApi(LocationServices.API).build();
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
                            status.startResolutionForResult(MapsActivity.this, MY_PERMISSIONS_REQUEST_LOCATION);
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

        Log.d("reId::", requestCode + ""+ resultCode);

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

                Log.d("result::","Video Captured"+  v_file.getAbsolutePath());
                btnAddMidea.setText(v_file.getName());


            }

        }
        if (requestCode == REQUEST_CAMERA){

            if(resultCode == RESULT_OK){



                File out = new File(getFilesDir(), "newImage.jpg");
                if (!out.exists()) {

                    Toast.makeText(getBaseContext(),

                            "Error while capturing image", Toast.LENGTH_LONG)

                            .show();

                    return;

                }


                Bitmap mBitmap = BitmapFactory.decodeFile(out.getAbsolutePath());


                final int destWidth = 400;
                double originHeight = mBitmap.getHeight();
                double originWidth = mBitmap.getWidth();
                Log.d("resulation::", mBitmap.getHeight() + "--" + mBitmap.getWidth());

                if (originWidth > destWidth) {


                    double ratio = originWidth / destWidth;
                    Log.d("resulation::", ratio + "---" + destWidth + "----" + originWidth);

                    int destHeight = (int) (originHeight / ratio);
                    Log.d("resulation::", ratio + "----");
                    Bitmap b2 = Bitmap.createScaledBitmap(mBitmap, destWidth, destHeight, false);

                    Log.d("resulation::", destHeight + "---" + destWidth);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    mBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                    ivImage.setImageBitmap(b2);

                } else {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    mBitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream);
                    ivImage.setImageBitmap(mBitmap);

                }



                /*    File out = new File(getFilesDir(), "newImage.jpg");
                    if (!out.exists()) {
                        Toast.makeText(getBaseContext(), "Error while capturing image", Toast.LENGTH_LONG).show();

                        return;
                    }

                    Bitmap mBitmap =     BitmapFactory.decodeFile(out.getAbsolutePath());
                    btnAddMidea.setText(out.getName());
                    ivImage.setImageBitmap(mBitmap);
*/
            }
        }

    }

    public void showProgressDialog() {

        if (mProgressDialog != null && !mProgressDialog.isShowing())
            mProgressDialog.show();
    }


    public void hideProgressDialog() {

        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    private void updateView(int y, int m, int d) {

        String month = "",day ="";

            if(m<10)
            {
                month = "0"+m;
            }else {
                month = ""+m;
            }

            if(d<10)
            {
                day = "0"+d;
            }else {
                day = ""+d;
            }
            etDate.setText("DATE:  "+y+"-"+month+"-"+day);

    }


    private void UploadComplain(){

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, complainuploadUrl, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                Log.d("caseResponse::", resultResponse);
                hideProgressDialog();

                new CDialog(MapsActivity.this).createAlert(resultResponse,
                        CDConstants.SUCCESS,   // Type of dialog
                        CDConstants.MEDIUM)    //  size of dialog
                        .setAnimation(CDConstants.SCALE_FROM_BOTTOM_TO_TOP)     //  Animation for enter/exit
                        .setDuration(2000)   // in milliseconds
                        .setTextSize(CDConstants.NORMAL_TEXT_SIZE)  // CDConstants.LARGE_TEXT_SIZE, CDConstants.NORMAL_TEXT_SIZE
                        .show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hideProgressDialog();
                error.printStackTrace();
                //showErrorDialog("Server Error. Please try again later");
            }
        }) {

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("image_tag", new DataPart("new_image.jpg", getFileDataFromDrawable(getBaseContext(), ivImage.getDrawable()), "image/jpeg"));
                return params;
            }


        };

        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multipartRequest);
    }

    public static byte[] getFileDataFromDrawable(Context context, Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


}
