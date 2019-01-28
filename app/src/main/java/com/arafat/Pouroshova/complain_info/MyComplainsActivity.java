package com.arafat.Pouroshova.complain_info;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.arafat.Pouroshova.ComplainApp;
import com.arafat.Pouroshova.complain_info.adapter.ComplainListAdapter;
import com.arafat.complainbox.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyComplainsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private static final String TAG = MyComplainsActivity.class.getSimpleName();
    private String COMPLAIN_LIST_API = "http://marvelbd.com/piams/griev_img/search/";
    private static final String USER_PREF = "USER_INFO";
    SharedPreferences sp;
    String UID="";
    ListView lvComplains;

    ArrayList<String> typeIDList =new ArrayList<>();
    ArrayList<String> desList =new ArrayList<>();
    ArrayList<String> dateList =new ArrayList<>();
    ArrayList<String> latList =new ArrayList<>();
    ArrayList<String> langList =new ArrayList<>();
    ArrayList<String> remList =new ArrayList<>();
    ArrayList<String> image_pathList =new ArrayList<>();

    ComplainListAdapter adapter;

    TextView tvCSub,tvCDate,tvCAdd,tvCDesc;
    ImageView ivComplan;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_my_complains);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        initView();
        getMyComplain(UID);

        lvComplains.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(typeIDList.get(position).equals("1")){

                    tvCSub.setText("বিষয়: বাড়িঘর");

                }else if(typeIDList.get(position).equals("2")){

                    tvCSub.setText("বিষয়: স্ট্রাকচারাল");

                }else if(typeIDList.get(position).equals("3")){

                    tvCSub.setText("বিষয়: পানি");

                }else if(typeIDList.get(position).equals("4")){

                    tvCSub.setText("বিষয়: রাস্তা");

                }else if(typeIDList.get(position).equals("5")){

                    tvCSub.setText("বিষয়: ব্রিজ");

                }else if(typeIDList.get(position).equals("6")){

                    tvCSub.setText("বিষয়: টার্মিনাল");

                }else if(typeIDList.get(position).equals("7")){
                    tvCSub.setText("বিষয়: অন্যান্য");
                }

                tvCDate.setText("তারিখঃ "+dateList.get(position));
                tvCAdd.setText("ঠিকানাঃ "+remList.get(position));
                tvCDesc.setText("বর্ণনাঃ "+desList.get(position));
                Log.d("im::",image_pathList.get(position));


                Glide.with(MyComplainsActivity.this).load("http://"+image_pathList.get(position)).into(ivComplan);

                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(latList.get(position)),Double.valueOf(langList.get(position)))));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(latList.get(position)),Double.valueOf(langList.get(position))), 12.0f));

            }
        });

    }

    private void initView() {


        sp  = getApplicationContext().getSharedPreferences(USER_PREF, MODE_PRIVATE);
        UID = sp.getString("user_id","");
        lvComplains = findViewById(R.id.lvComplainList);

        tvCSub = findViewById(R.id.tvCType);
        tvCDate = findViewById(R.id.tvCDate);
        tvCAdd = findViewById(R.id.tvCAdd);
        tvCDesc = findViewById(R.id.tvCDesc);
        ivComplan= findViewById(R.id.ivComplans);

    }


    private void getMyComplain(final String UID){
        pd = new ProgressDialog(MyComplainsActivity.this);
        pd.setMessage("loading...");
        pd.show();


        StringRequest request = new StringRequest(Request.Method.POST, COMPLAIN_LIST_API, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if(pd.isShowing()){
                    pd.dismiss();
                }
                Log.d("resInfo::",response);

                try {

                    JSONObject object = new JSONObject(response);
                    String data = object.getString("data");
                    Log.d("data::",data);

                    JSONArray jsonArray =new JSONArray(data);

                    for (int i =0;i<jsonArray.length();i++){

                        JSONObject jObj =jsonArray.getJSONObject(i);

                        typeIDList.add(jObj.getString("type_id"));
                        desList.add(jObj.getString("des"));
                        dateList.add(jObj.getString("date"));
                        latList.add(jObj.getString("lat"));
                        langList.add(jObj.getString("lang"));
                        remList.add(jObj.getString("rem"));
                        image_pathList.add(jObj.getString("image_path"));


                    }

                    adapter = new ComplainListAdapter(MyComplainsActivity.this,typeIDList,desList,dateList,latList,langList,remList,image_pathList);
                    lvComplains.setAdapter(adapter);

                    if(typeIDList.get(0).equals("1")){

                        tvCSub.setText("বিষয়: বাড়িঘর");

                    }else if(typeIDList.get(0).equals("2")){

                        tvCSub.setText("বিষয়: স্ট্রাকচারাল");

                    }else if(typeIDList.get(0).equals("3")){

                        tvCSub.setText("বিষয়: পানি");

                    }else if(typeIDList.get(0).equals("4")){

                        tvCSub.setText("বিষয়: রাস্তা");

                    }else if(typeIDList.get(0).equals("5")){

                        tvCSub.setText("বিষয়: ব্রিজ");

                    }else if(typeIDList.get(0).equals("6")){

                        tvCSub.setText("বিষয়: টার্মিনাল");

                    }else if(typeIDList.get(0).equals("7")){
                        tvCSub.setText("বিষয়: অন্যান্য");
                    }

                    tvCDate.setText("তারিখঃ "+dateList.get(0));
                    tvCAdd.setText("ঠিকানাঃ "+remList.get(0));
                    tvCDesc.setText("ডেসক্রিপশনঃ "+desList.get(0));
                    Log.d("im::",image_pathList.get(0));

                    Glide.with(MyComplainsActivity.this).load("http://"+image_pathList.get(0)).into(ivComplan);

                    mMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(latList.get(0)),Double.valueOf(langList.get(0)))));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(latList.get(0)),Double.valueOf(langList.get(0))), 12.0f));

                }catch (Exception e){

                    e.printStackTrace();
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
                params.put("uid", UID);
                return params;
            }
        };
        request.setShouldCache(false);
        ComplainApp.getComplain().addToRequestQueue(request, TAG);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(latList.size()>0) {
            mMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(latList.get(0)), Double.valueOf(langList.get(0)))));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(latList.get(0)), Double.valueOf(langList.get(0))), 12.0f));
        }
    }
}
