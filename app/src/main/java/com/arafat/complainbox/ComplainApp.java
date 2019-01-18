package com.arafat.complainbox;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

public class ComplainApp extends Application {

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
}
