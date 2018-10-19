package com.rescribe.doctor.singleton;

import android.content.Context;
import android.graphics.Typeface;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.evernote.android.job.JobManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.rescribe.doctor.BuildConfig;
import com.rescribe.doctor.helpers.database.AppDBHelper;
import com.rescribe.doctor.model.doctor_location.DoctorLocationModel;
import com.rescribe.doctor.services.job_creator_download_cities.AppJobCreator;

//import net.gotev.uploadservice.UploadService;

import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by Sandeep Bahalkar
 */
public class RescribeApplication extends MultiDexApplication {
    public  final String TAG = this.getClass().getName();
    private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();
    private static ArrayList<DoctorLocationModel> doctorLocationModels = new ArrayList<>();
    private static String SHOW_UPDATE_DIALOG_ON_SKIPPED = "";

    private RequestQueue mRequestQueue;

    private static RescribeApplication mInstance;


    public static String getShowUpdateDialogOnSkipped() {
        return SHOW_UPDATE_DIALOG_ON_SKIPPED;
    }

    public static void setShowUpdateDialogOnSkipped(String showUpdateDialogOnSkipped) {
        SHOW_UPDATE_DIALOG_ON_SKIPPED = showUpdateDialogOnSkipped;
    }

    public static Typeface get(Context c, String name) {
        synchronized (cache) {
            if (!cache.containsKey(name)) {
                Typeface t = Typeface.createFromAsset(c.getAssets(), "fonts/"
                        + name);
                cache.put(name, t);
            }
            return cache.get(name);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
       // UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        AppDBHelper.getInstance(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
//        new NukeSSLCerts().nuke(); // disable all ssl certificates (dangerous)
        JobManager.create(this).addJobCreator(new AppJobCreator());

        mInstance = this;
    }
    public static ArrayList<DoctorLocationModel> getDoctorLocationModels() {
        return doctorLocationModels;
    }

    public static void setDoctorLocationModels(ArrayList<DoctorLocationModel> doctorLocationModels) {
        RescribeApplication.doctorLocationModels = doctorLocationModels;
    }


    public static synchronized RescribeApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

}