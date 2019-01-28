package com.arafat.Pouroshova;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class ComplainApp extends Application {

    private static RequestQueue requestQueue;
    private String TAG = ComplainApp.class.getSimpleName();
    private static ComplainApp complain;
    private static Context context;

    public static Context getContext() {
        return context;
    }

    public static synchronized ComplainApp getComplain() {
        return complain;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        context =getApplicationContext();
        complain =this;
        MultiDex.install(this);
    }

    public static RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(complain);
        }
        return requestQueue;
    }


    public <T> void addToRequestQueue(Request<T> req, String tag) {
        Log.d(TAG, "addToRequestQueue called from :: " + tag);
        if (TextUtils.isEmpty(tag)) {
            tag = TAG;
        }
        req.setTag(tag);
        getRequestQueue().add(req);
    }

    public void setRequestQueue(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    public void setComplainBox(ComplainApp complain) {
        this.complain = complain;
    }
}
