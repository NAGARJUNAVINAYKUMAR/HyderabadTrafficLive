package com.tspolice.htplive.activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tspolice.htplive.R;
import com.tspolice.htplive.firebase.MyFirebaseInstanceIdService;
import com.tspolice.htplive.gcm.GCMRegistrationIntentService;
import com.tspolice.htplive.network.Networking;
import com.tspolice.htplive.network.URLs;
import com.tspolice.htplive.network.VolleySingleton;
import com.tspolice.htplive.utils.Constants;
import com.tspolice.htplive.utils.HardwareUtils;
import com.tspolice.htplive.utils.UiHelper;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "HomeActivity-->";
    private final int SPLASH_DIALOG = 0, SPLASH_TIME_OUT = 2000;
    private Button btn_english;
    private UiHelper mUiHelper;
    private boolean doubleBackToExitPressedOnce = false;
    private BroadcastReceiver gcmBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_english = findViewById(R.id.btn_english);

        mUiHelper = new UiHelper(this);

        showDialog(SPLASH_DIALOG);

        if (!Networking.isNetworkAvailable(this)) {
            mUiHelper.showToastLong(getResources().getString(R.string.network_error));
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    removeDialog(SPLASH_DIALOG);
                }
            }, SPLASH_TIME_OUT);
        }

        gcmBroadcastReceiver = new BroadcastReceiver() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (Objects.requireNonNull(intent.getAction())) {
                    case Constants.REGISTRATION_SUCCESS:
                        mUiHelper.showToastLong("Device is ready");
                        break;
                    case Constants.REGISTRATION_TOKEN_SENT:
                        mUiHelper.showToastLong("Ready to receive push notifications");
                        break;
                    case Constants.REGISTRATION_ERROR:
                        mUiHelper.showToastLong("GCM registration error!");
                        break;
                    default:
                        mUiHelper.showToastLong("Error occurred");
                        break;
                }
            }
        };

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (ConnectionResult.SUCCESS != resultCode) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                mUiHelper.showToastLong("Google Play Service is not install/enabled in this device!");
                GooglePlayServicesUtil.showErrorNotification(resultCode, MainActivity.this);
            } else {
                mUiHelper.showToastLong("This device does not support for Google Play Service!");
            }
        } else {
            startService(new Intent(MainActivity.this, GCMRegistrationIntentService.class));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        btn_english.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(gcmBroadcastReceiver,
                new IntentFilter(Constants.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(gcmBroadcastReceiver,
                new IntentFilter(Constants.REGISTRATION_ERROR));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG,  "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(gcmBroadcastReceiver);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case SPLASH_DIALOG:
                Dialog dialogSplash = new Dialog(this, android.R.style.Theme_Holo_Light_NoActionBar);
                dialogSplash.setCancelable(false);
                dialogSplash.setContentView(R.layout.layout_splash);
                RelativeLayout rel_splash = dialogSplash.findViewById(R.id.rel_splash);
                rel_splash.setOnClickListener(this);
                return dialogSplash;
            default:
                break;
        }
        return super.onCreateDialog(id);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_english:
                mUiHelper.intent(HomeActivity.class);
                //GoogleCloudMessaging.getInstance(MainActivity.this);//.subscribeToTopic("NEWS");
                //String token = FirebaseInstanceId.getInstance().getToken();
                //sendRegistrationToServer(token);
                startService(new Intent(this, GCMRegistrationIntentService.class));
                break;
            case R.id.rel_splash:
                if (!Networking.isNetworkAvailable(MainActivity.this)) {
                    mUiHelper.showToastLong(getResources().getString(R.string.network_error));
                } else {
                    removeDialog(SPLASH_DIALOG);
                }
                break;
            default:
                break;
        }
    }

    private void sendRegistrationToServer(String refreshedToken) {
        mUiHelper = new UiHelper(MainActivity.this);
        mUiHelper.showProgressDialog(getResources().getString(R.string.please_wait), false);
        String url = URLs.saveRegIds(refreshedToken, Constants.ANDROID, HardwareUtils.getDeviceUUID(MainActivity.this));
        Log.i("url-->", url);
        VolleySingleton.getInstance(this).addToRequestQueue(new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mUiHelper.dismissProgressDialog();
                        Log.i("response-->", response);
                        mUiHelper.showToastLong(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mUiHelper.dismissProgressDialog();
                        Log.i("error-->", error.toString());
                        mUiHelper.showToastShort(getResources().getString(R.string.error));
                    }
                }));
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        mUiHelper.showToastShort(getResources().getString(R.string.click_back_again_to_close));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, SPLASH_TIME_OUT);
    }
}
