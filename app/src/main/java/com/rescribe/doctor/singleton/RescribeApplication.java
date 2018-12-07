package com.rescribe.doctor.singleton;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.IBinder;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.evernote.android.job.JobManager;
import com.rescribe.doctor.helpers.database.AppDBHelper;
import com.rescribe.doctor.model.doctor_location.DoctorLocationModel;
import com.rescribe.doctor.services.job_creator_download_cities.AppJobCreator;
import com.smart.pen.core.services.PenService;
import com.smart.pen.core.services.SmartPenService;
import com.smart.pen.core.services.UsbPenService;
import com.smart.pen.core.symbol.Keys;
import com.smart.pen.core.symbol.RecordLevel;

import java.util.ArrayList;
import java.util.Hashtable;

import io.fabric.sdk.android.Fabric;

//import net.gotev.uploadservice.UploadService;

/**
 * Created by Sandeep Bahalkar
 */
public class RescribeApplication extends MultiDexApplication {
    private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();
    private static ArrayList<DoctorLocationModel> doctorLocationModels = new ArrayList<>();
    private static String SHOW_UPDATE_DIALOG_ON_SKIPPED = "";
    private static RescribeApplication mInstance;
    public final String TAG = this.getClass().getName();
    public boolean isBindPenService = false;
    private RequestQueue mRequestQueue;
    private PenService mPenService;
    private Intent mPenServiceIntent;
    private ServiceConnection mPenServiceConnection;

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

    public static ArrayList<DoctorLocationModel> getDoctorLocationModels() {
        return doctorLocationModels;
    }

    public static void setDoctorLocationModels(ArrayList<DoctorLocationModel> doctorLocationModels) {
        RescribeApplication.doctorLocationModels = doctorLocationModels;
    }

    ///////////////////////////////////////////////////

    public static synchronized RescribeApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        // UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        AppDBHelper.getInstance(this);
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(this);
//        new NukeSSLCerts().nuke(); // disable all ssl certificates (dangerous)
        JobManager.create(this).addJobCreator(new AppJobCreator());
        mInstance = this;
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

    /**
     * Get the recording level
     *
     * @return
     */
    public int getRecordLevel() {
        SharedPreferences preferences = this.getSharedPreferences(Keys.RECORD_SETTING_KEY, Context.MODE_PRIVATE);
        int type = preferences.getInt(Keys.RECORD_LEVEL_KEY, RecordLevel.level_13);
        return type;
    }

    /**
     * Set the recording level
     *
     * @param value
     * @return
     */
    public boolean setRecordLevel(int value) {
        SharedPreferences preferences = this.getSharedPreferences(Keys.RECORD_SETTING_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(Keys.RECORD_LEVEL_KEY, value);
        boolean result = editor.commit();

        return result;
    }

    /**
     * Get pen service
     *
     * @return
     */
    public PenService getPenService() {
        return mPenService;
    }

    public Intent getPenServiceIntent(String svrName) {
        if (mPenServiceIntent == null) {
            if (Keys.APP_PEN_SERVICE_NAME.equals(svrName)) {
                mPenServiceIntent = new Intent(this, SmartPenService.class);
            } else if (Keys.APP_USB_SERVICE_NAME.equals(svrName)) {
                mPenServiceIntent = new Intent(this, UsbPenService.class);
            }
        }
        return mPenServiceIntent;
    }

    private ServiceConnection getPenServiceConnection() {
        if (mPenServiceConnection == null) {
            mPenServiceConnection = new ServiceConnection() {
                public void onServiceConnected(ComponentName className,
                                               IBinder rawBinder) {
                    mPenService = ((PenService.LocalBinder) rawBinder).getService();
                    Log.v(TAG, "onServiceConnected:" + mPenService.getSvrTag());
                }

                public void onServiceDisconnected(ComponentName classname) {
                    Log.v(TAG, "onServiceDisconnected");
                    mPenService = null;
                    mPenServiceConnection = null;
                }
            };
        }
        return mPenServiceConnection;
    }

    /**
     * Start background service
     **/
    protected void startPenService(String svrName) {
        Log.v(TAG, "startPenService name:" + svrName);
        startService(getPenServiceIntent(svrName));
    }

    /**
     * Stop background service
     **/
    public void stopPenService(String svrName) {
        Log.v(TAG, "stopPenService");
        stopService(getPenServiceIntent(svrName));
    }

    /**
     * Bind the background service, if not started, start the service and then bind
     **/
    public void bindPenService(String svrName) {
        if (!isServiceRunning(svrName)) {
            isBindPenService = false;
            this.startPenService(svrName);
        }
        if (!isBindPenService) {
            mPenService = null;
            isBindPenService = bindService(getPenServiceIntent(svrName), getPenServiceConnection(), Context.BIND_AUTO_CREATE);
            Log.v(TAG, "bindService " + svrName);
        }
    }

    /**
     * Unbind background service
     **/
    public void unBindPenService() {
        if (isBindPenService) {
            if (mPenServiceConnection != null) {
                Log.v(TAG, "unBindPenService");
                unbindService(mPenServiceConnection);
                mPenServiceIntent = null;
            }
            isBindPenService = false;
        }
    }


    /**
     * Query whether the background service is enabled
     **/
    private boolean isServiceRunning(String serviceName) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getClassName().compareTo(serviceName) == 0) {
                return true;
            }
        }
        return false;
    }
}