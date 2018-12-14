package com.tspolice.htplive.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.tspolice.htplive.R;
import com.tspolice.htplive.network.URLs;
import com.tspolice.htplive.network.VolleySingleton;
import com.tspolice.htplive.utils.Constants;
import com.tspolice.htplive.utils.HardwareUtils;
import com.tspolice.htplive.utils.UiHelper;

public class GCMRegistrationIntentService extends IntentService {

    private static final String TAG = "GCMRegnIntentService-->";
    private UiHelper mUiHelper;

    //Class constructor
    public GCMRegistrationIntentService() {
        super("");
    }

    /*public GCMRegistrationIntentService(String name) {
        super("");
    }*/

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        registerGCM();
    }

    private void registerGCM() {
        Intent registrationComplete;
        try {
            InstanceID instanceID = InstanceID.getInstance(GCMRegistrationIntentService.this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Log.i(TAG, "token:" + token);

            sendRegistrationTokenToServer(token);

            registrationComplete = new Intent(Constants.REGISTRATION_SUCCESS);
            registrationComplete.putExtra("token", token);
        } catch (Exception e) {
            Log.i(TAG, "Registration error");
            registrationComplete = new Intent(Constants.REGISTRATION_ERROR);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationTokenToServer(String token) {
        mUiHelper = new UiHelper(GCMRegistrationIntentService.this);
        mUiHelper.showProgressDialog(getResources().getString(R.string.please_wait), false);
        VolleySingleton.getInstance(this).addToRequestQueue(new StringRequest(Request.Method.GET,
                URLs.saveRegIds(token, Constants.ANDROID, HardwareUtils.getDeviceUUID(this)),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mUiHelper.dismissProgressDialog();
                        mUiHelper.showToastLong(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mUiHelper.dismissProgressDialog();
                mUiHelper.showToastShort(getResources().getString(R.string.error));
            }
        }));
    }

}
