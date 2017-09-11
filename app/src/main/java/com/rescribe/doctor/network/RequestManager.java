package com.rescribe.doctor.network;

/**
 * @author Sandeep Bahalkar
 */

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.rescribe.doctor.R;
import com.rescribe.doctor.helpers.database.AppDBHelper;
import com.rescribe.doctor.interfaces.ConnectionListener;
import com.rescribe.doctor.interfaces.Connector;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.model.Common;
import com.rescribe.doctor.model.doctor_connect_search.DoctorConnectSearchBaseModel;
import com.rescribe.doctor.model.login.LoginModel;
import com.rescribe.doctor.model.login.SignUpModel;
import com.rescribe.doctor.model.parceable_doctor_connect.DoctorConnectBaseModel;
import com.rescribe.doctor.model.parceable_doctor_connect_chat.DoctorConnectChatBaseModel;
import com.rescribe.doctor.model.requestmodel.login.LoginRequestModel;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.singleton.Device;
import com.rescribe.doctor.ui.customesViews.CustomProgressDialog;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.Config;
import com.rescribe.doctor.util.RescribeConstants;
import com.rescribe.doctor.util.NetworkUtil;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RequestManager extends ConnectRequest implements Connector, RequestTimer.RequestTimerListener {
    private final String TAG = this.getClass().getName();
    private static final int CONNECTION_TIME_OUT = 1000 * 60;
    private static final int N0OF_RETRY = 0;
    private AppDBHelper dbHelper;
    private String requestTag;
    private int connectionType = Request.Method.POST;

    private String mDataTag;
    private RequestTimer requestTimer;
    private JsonObjectRequest jsonRequest;
    private StringRequest stringRequest;

    public RequestManager(Context mContext, ConnectionListener connectionListener, String dataTag, View viewById, boolean isProgressBarShown, String mOldDataTag, int connectionType, boolean isOffline) {
        super();
        this.mConnectionListener = connectionListener;
        this.mContext = mContext;
        this.mDataTag = dataTag;
        this.mViewById = viewById;
        this.isProgressBarShown = isProgressBarShown;
        this.mOldDataTag = mOldDataTag;
        this.requestTag = String.valueOf(dataTag);
        this.requestTimer = new RequestTimer();
        this.requestTimer.setListener(this);
        this.mProgressDialog = new CustomProgressDialog(mContext);
        this.connectionType = connectionType;
        this.isOffline = isOffline;
        this.dbHelper = new AppDBHelper(mContext);
    }

    @Override
    public void connect() {

        if (NetworkUtil.isInternetAvailable(mContext)) {

            RequestPool.getInstance(this.mContext).cancellAllPreviousRequestWithSameTag(requestTag);

            if (isProgressBarShown) {
                mProgressDialog.setCancelable(true);
                mProgressDialog.show();
            } else {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }

            if (mPostParams != null) {
                stringRequest(mURL, connectionType, mHeaderParams, mPostParams, false);
            } else if (customResponse != null) {
                jsonRequest(mURL, connectionType, mHeaderParams, customResponse, false);
            } else {
                jsonRequest();
            }
        } else {

            if (isOffline) {
                if (getOfflineData() != null)
                    succesResponse(getOfflineData(), false);
                else
                    mConnectionListener.onResponse(ConnectionListener.NO_INTERNET, null, mOldDataTag);
            } else {
                mConnectionListener.onResponse(ConnectionListener.NO_INTERNET, null, mOldDataTag);
            }

            if (mViewById != null)
                CommonMethods.showSnack(mViewById, mContext.getString(R.string.internet));
            else
                CommonMethods.showToast(mContext, mContext.getString(R.string.internet));
        }
    }

    private void jsonRequest(String url, int connectionType, final Map<String, String> headerParams, CustomResponse customResponse, final boolean isTokenExpired) {

        Gson gson = new Gson();
        JSONObject jsonObject = null;
        try {
            CommonMethods.Log(TAG, "customResponse:--" + customResponse.toString());
            String jsonString = gson.toJson(customResponse);

            CommonMethods.Log(TAG, "jsonRequest:--" + jsonString);

            if (!jsonString.equals("null"))
                jsonObject = new JSONObject(jsonString);
        } catch (JSONException | JsonSyntaxException e) {
            e.printStackTrace();
        }

        jsonRequest = new JsonObjectRequest(connectionType, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        succesResponse(response.toString(), isTokenExpired);
                        if (isOffline)
                            dbHelper.insertData(mDataTag, response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorResponse(error, isTokenExpired);
            }
        })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (headerParams == null) {
                    return Collections.emptyMap();
                } else {
                    return headerParams;
                }

            }
        };
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(CONNECTION_TIME_OUT, N0OF_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonRequest.setTag(requestTag);
        requestTimer.start();
        RequestPool.getInstance(this.mContext).addToRequestQueue(jsonRequest);
    }


    private void jsonRequest() {

        Gson gson = new Gson();
        JSONObject jsonObject = null;
        try {
            String jsonString = gson.toJson(customResponse);

            CommonMethods.Log(TAG, "jsonRequest:--" + jsonString);

            if (!jsonString.equals("null"))
                jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        jsonRequest = new JsonObjectRequest(connectionType, this.mURL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        succesResponse(response.toString(), false);
                        if (isOffline)
                            dbHelper.insertData(mDataTag, response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorResponse(error, false);
            }
        })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (mHeaderParams == null) {
                    return Collections.emptyMap();
                } else {
                    return mHeaderParams;
                }

            }
        };
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(CONNECTION_TIME_OUT, N0OF_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonRequest.setTag(requestTag);
        requestTimer.start();
        RequestPool.getInstance(this.mContext).addToRequestQueue(jsonRequest);
    }

    private void stringRequest(String url, int connectionType, final Map<String, String> headerParams, final Map<String, String> postParams, final boolean isTokenExpired) {

        // ganesh for string request and delete method with string request

        stringRequest = new StringRequest(connectionType, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        succesResponse(response, isTokenExpired);
                        if (isOffline)
                            dbHelper.insertData(mDataTag, response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        errorResponse(error, isTokenExpired);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headerParams;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return postParams;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(CONNECTION_TIME_OUT, N0OF_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setTag(requestTag);
        requestTimer.start();
        RequestPool.getInstance(this.mContext).addToRequestQueue(stringRequest);
    }

    private void succesResponse(String response, boolean isTokenExpired) {
        requestTimer.cancel();
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        parseJson(fixEncoding(response), isTokenExpired);
    }

    private void errorResponse(VolleyError error, boolean isTokenExpired) {

        requestTimer.cancel();

        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        try {

//            VolleyError error1 = new VolleyError(new String(error.networkResponse.data));
//            error = error1;
//            CommonMethods.Log("Error Message", error.getMessage() + "\n error Localize message" + error.getLocalizedMessage());
            CommonMethods.Log(TAG, "Goes into error response condition");

            if (error instanceof TimeoutError) {

                if (error.getMessage().equalsIgnoreCase("java.io.IOException: No authentication challenges found") || error.getMessage().equalsIgnoreCase("invalid_grant")) {
                    if (!isTokenExpired) {
                        tokenRefreshRequest();
                    }
                }

//                if (error.getMessage().equalsIgnoreCase("java.io.IOException: No authentication challenges found") || error.getMessage().equalsIgnoreCase("invalid_grant")) {
//                    if (mViewById != null)
//                        CommonMethods.showSnack(mViewById, mContext.getString(R.string.authentication));
//                    else
//                        CommonMethods.showToast(mContext, mContext.getString(R.string.authentication));
//                } else if (error.getMessage().equalsIgnoreCase("javax.net.ssl.SSLHandshakeException: java.security.cert.CertPathValidatorException: Trust anchor for certification path not found.")) {
//                    showErrorDialog("Something went wrong.");
//                }

                if (mViewById != null)
                    CommonMethods.showSnack(mViewById, mContext.getString(R.string.timeout));
                else
                    CommonMethods.showToast(mContext, mContext.getString(R.string.timeout));

            } else if (error instanceof NoConnectionError) {

                if (error.getMessage().equalsIgnoreCase("java.io.IOException: No authentication challenges found") || error.getMessage().equalsIgnoreCase("invalid_grant")) {
                    if (!isTokenExpired) {
                        tokenRefreshRequest();
                    }
                }

                if (mViewById != null)
                    CommonMethods.showSnack(mViewById, mContext.getString(R.string.internet));
                else {
                    mConnectionListener.onResponse(ConnectionListener.NO_CONNECTION_ERROR, null, mOldDataTag);
                }

            } else if (error instanceof ServerError) {
                if (isTokenExpired) {
                    // Redirect to SplashScreen then Login
//                    Intent intent = new Intent(mContext, LoginActivity.class);
//                    mContext.startActivity(intent);
//                    ((AppCompatActivity) mContext).finishAffinity();

//                    RescribePreferencesManager.clearSharedPref(mContext);
//                    Intent intent = new Intent(mContext, SplashScreenActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    mContext.startActivity(intent);

                    //  loginRequest();
                } else {
                    mConnectionListener.onResponse(ConnectionListener.SERVER_ERROR, null, mOldDataTag);
                    CommonMethods.showToast(mContext, mContext.getResources().getString(R.string.server_error));
                }
            } else if (error instanceof NetworkError) {

                if (isOffline) {
                    succesResponse(getOfflineData(), false);
                } else {
                    mConnectionListener.onResponse(ConnectionListener.NO_INTERNET, null, mOldDataTag);
                }

                if (mViewById != null)
                    CommonMethods.showSnack(mViewById, mContext.getString(R.string.internet));
                else
                    CommonMethods.showToast(mContext, mContext.getString(R.string.internet));
            } else if (error instanceof ParseError) {
                mConnectionListener.onResponse(ConnectionListener.PARSE_ERR0R, null, mOldDataTag);
            } else if (error instanceof AuthFailureError) {
                if (!isTokenExpired) {
                    tokenRefreshRequest();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getOfflineData() {
        if (dbHelper.dataTableNumberOfRows(this.mDataTag) > 0) {
            Cursor cursor = dbHelper.getData(this.mDataTag);
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex(AppDBHelper.COLUMN_DATA));
        } else {
            return null;
        }
    }

    private String fixEncoding(String response) {
        try {
            byte[] u = response.getBytes("ISO-8859-1");
            response = new String(u, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return response;
    }

    @Override
    public void parseJson(String data, boolean isTokenExpired) {
        try {
            CommonMethods.Log(TAG, data);
            Gson gson = new Gson();

            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(data);
                Common common = gson.fromJson(jsonObject.optString("common"), Common.class);
                if (!common.getStatusCode().equals(RescribeConstants.SUCCESS)) {
                    CommonMethods.showToast(mContext, common.getStatusMessage());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            /*MessageModel messageModel = gson.fromJson(data, MessageModel.class);
            if (!messageModel.getCommon().getStatusCode().equals(RescribeConstants.SUCCESS))
                CommonMethods.showToast(mContext, messageModel.getCommon().getStatusMessage());*/

            if (isTokenExpired) {
                // This success response is for refresh token
                // Need to Add
                LoginModel loginModel = gson.fromJson(data, LoginModel.class);
                RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.AUTHTOKEN, loginModel.getAuthToken(), mContext);
                RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.LOGIN_STATUS, RescribeConstants.YES, mContext);
                RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.PATIENT_ID, loginModel.getPatientId(), mContext);

                mHeaderParams.put(RescribeConstants.AUTHORIZATION_TOKEN, loginModel.getAuthToken());

                connect();

            } else {
                // This success response is for respective api's

                switch (this.mDataTag) {
                    // Need to add
                    case RescribeConstants.TASK_LOGIN: //This is for get archived list
                        LoginModel loginModel = gson.fromJson(data, LoginModel.class);
                        this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, loginModel, mOldDataTag);
                        break;
                    case RescribeConstants.TASK_LOGIN_WITH_PASSWORD: //This is for get archived list
                        LoginModel loginWithPasswordModel = new Gson().fromJson(data, LoginModel.class);
                        this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, loginWithPasswordModel, mOldDataTag);
                        break;
                    case RescribeConstants.TASK_LOGIN_WITH_OTP: //This is for get archived list
                        LoginModel loginWithOtpModel = new Gson().fromJson(data, LoginModel.class);
                        this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, loginWithOtpModel, mOldDataTag);
                        break;
                    case RescribeConstants.TASK_SIGN_UP: //This is for get sign-up
                        this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, new Gson().fromJson(data, SignUpModel.class), mOldDataTag);
                        break;
                    case RescribeConstants.TASK_VERIFY_SIGN_UP_OTP: //This is for to verify sign-up otp
                        this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, new Gson().fromJson(data, LoginModel.class), mOldDataTag);
                        break;

                    case RescribeConstants.TASK_DOCTOR_CONNECT_CHAT: //This is for get archived list
                        DoctorConnectChatBaseModel doctorConnectChatBaseModel = new Gson().fromJson(data, DoctorConnectChatBaseModel.class);
                        this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, doctorConnectChatBaseModel, mOldDataTag);
                        break;
                    case RescribeConstants.TASK_DOCTOR_CONNECT: //This is for get archived list
                        DoctorConnectBaseModel doctorConnectBaseModel = new Gson().fromJson(data, DoctorConnectBaseModel.class);
                        this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, doctorConnectBaseModel, mOldDataTag);
                        break;
                    case RescribeConstants.TASK_DOCTOR__FILTER_DOCTOR_SPECIALITY_LIST: //This is for get archived list
                        DoctorConnectSearchBaseModel doctorConnectSearchBaseModel = new Gson().fromJson(data, DoctorConnectSearchBaseModel.class);
                        this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, doctorConnectSearchBaseModel, mOldDataTag);
                        break;

                    default:


                }
            }

        } catch (JsonSyntaxException e) {
            CommonMethods.Log(TAG, "JsonException" + e.getMessage());
            mConnectionListener.onResponse(ConnectionListener.PARSE_ERR0R, null, mOldDataTag);
        }

    }

    @Override
    public void setPostParams(CustomResponse customResponse) {
        this.customResponse = customResponse;
    }

    @Override
    public void setPostParams(Map<String, String> postParams) {
        this.mPostParams = postParams;
    }

    @Override
    public void setHeaderParams(Map<String, String> headerParams) {
        this.mHeaderParams = headerParams;
    }

    @Override
    public void abort() {
        if (jsonRequest != null)
            jsonRequest.cancel();
        if (stringRequest != null)
            stringRequest.cancel();
    }

    @Override
    public void setUrl(String url) {
        this.mURL = url;
    }

    @Override
    public void onTimeout(RequestTimer requestTimer) {
        if (mContext instanceof AppCompatActivity) {
            if (mContext != null) {
                ((AppCompatActivity) this.mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                    }
                });
            }
        }

        RequestPool.getInstance(mContext)
                .cancellAllPreviousRequestWithSameTag(requestTag);
        mConnectionListener.onTimeout(this);
    }

    public void showErrorDialog(String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setMessage(msg);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void tokenRefreshRequest() {
        loginRequest();
    }

    private void loginRequest() {
        CommonMethods.Log(TAG, "Refresh token while sending refresh token api: ");
        String url = Config.BASE_URL + Config.LOGIN_URL;

        LoginRequestModel loginRequestModel = new LoginRequestModel();

        loginRequestModel.setMobileNumber(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.MOBILE_NUMBER, mContext));
        loginRequestModel.setPassword(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.PASSWORD, mContext));
        if (!(RescribeConstants.BLANK.equalsIgnoreCase(loginRequestModel.getMobileNumber()) &&
                RescribeConstants.BLANK.equalsIgnoreCase(loginRequestModel.getPassword()))) {
            Map<String, String> headerParams = new HashMap<>();
            headerParams.putAll(mHeaderParams);
            Device device = Device.getInstance(mContext);

            headerParams.put(RescribeConstants.CONTENT_TYPE, RescribeConstants.APPLICATION_JSON);
            headerParams.put(RescribeConstants.DEVICEID, device.getDeviceId());
            headerParams.put(RescribeConstants.OS, device.getOS());
            headerParams.put(RescribeConstants.OSVERSION, device.getOSVersion());
            headerParams.put(RescribeConstants.DEVICE_TYPE, device.getDeviceType());
            CommonMethods.Log(TAG, "setHeaderParams:" + headerParams.toString());
            jsonRequest(url, Request.Method.POST, headerParams, loginRequestModel, true);
        } else {
            mConnectionListener.onResponse(ConnectionListener.PARSE_ERR0R, null, mOldDataTag);
        }
    }
}