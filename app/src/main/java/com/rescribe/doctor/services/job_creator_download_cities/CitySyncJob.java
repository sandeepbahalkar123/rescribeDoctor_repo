package com.rescribe.doctor.services.job_creator_download_cities;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.rescribe.doctor.helpers.database.AppDBHelper;
import com.rescribe.doctor.network.RequestPool;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.singleton.Device;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import static com.android.volley.Request.Method.POST;
import static com.rescribe.doctor.util.Config.GET_ALL_CITIES_STATE_WISE;
import static com.rescribe.doctor.util.Config.BASE_URL;

public class CitySyncJob extends Job {

    public static final String TAG = RescribeConstants.TASK_GET_STATE_AND_CITY_TO_ADD_NEW_PATIENT;
    private Context mContext;

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {

        if (!isCanceled()) {
            PersistableBundleCompat requestExtras = params.getExtras();
            int[] keys = requestExtras.getIntArray("key");
            mContext = getContext();
            check(keys);
        }
        return Result.SUCCESS;

    }


    public void runJobImmediately(HashSet<Integer> states) {

        PersistableBundleCompat extras = new PersistableBundleCompat();

        int[] statesInt = new int[states.size()];

        Iterator iterator = states.iterator();

        int count = 0;
        while (iterator.hasNext()) {
            statesInt[count] = Integer.parseInt("" + iterator.next());
            count = count + 1;
        }

        extras.putIntArray("key", statesInt);

        int jobId = new JobRequest.Builder(CitySyncJob.TAG)
                .startNow()
                .setExtras(extras)
                .build()
                .schedule();
    }

    private void check(int[] statesList) {

        JSONArray jsonArray = new JSONArray();
        for (Integer state :
                statesList) {
            jsonArray.put(state);
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.putOpt("stateId", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CommonMethods.Log(TAG, "REQUEST:" + jsonObject.toString());

        String url = BASE_URL + GET_ALL_CITIES_STATE_WISE;

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        CommonMethods.Log(TAG, "RESPONSE:" + response.toString());

                        if (!isCanceled() ) {
                            CommonMethods.Log(TAG, "RESPONSE:" + response.toString());
                            AppDBHelper.getInstance(mContext).insertData(TAG, response.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Device device = Device.getInstance(mContext);
                Map<String, String> headerParams = new HashMap<>();
                String authorizationString = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.AUTHTOKEN, mContext);
                headerParams.put(RescribeConstants.CONTENT_TYPE, RescribeConstants.APPLICATION_JSON);
                headerParams.put(RescribeConstants.AUTHORIZATION_TOKEN, authorizationString);
                headerParams.put(RescribeConstants.DEVICEID, device.getDeviceId());
                headerParams.put(RescribeConstants.OS, device.getOS());
                headerParams.put(RescribeConstants.OSVERSION, device.getOSVersion());
                headerParams.put(RescribeConstants.DEVICE_TYPE, device.getDeviceType());

                CommonMethods.Log(TAG, "headerParams:" + headerParams.toString());

                return headerParams;
            }
        };
        // jsonRequest.setRetryPolicy(new DefaultRetryPolicy(1000 * 60, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonRequest.setTag("SyncingCities");
        jsonRequest.setShouldCache(false);
        RequestPool.getInstance(mContext).addToRequestQueue(jsonRequest);

    }


}
