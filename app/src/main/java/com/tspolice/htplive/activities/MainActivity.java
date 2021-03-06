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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.tspolice.htplive.R;
import com.tspolice.htplive.gcm.GCMRegistrationIntentService;
import com.tspolice.htplive.network.Networking;
import com.tspolice.htplive.utils.Constants;
import com.tspolice.htplive.utils.SharedPrefManager;
import com.tspolice.htplive.utils.UiHelper;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // resolved again --> VCS root not found !
    private static final String TAG = "MainActivity-->";
    private final int SPLASH_DIALOG = 0, SPLASH_TIME_OUT = 2000;
    private Button btn_english;
    private UiHelper mUiHelper;
    private boolean doubleBackToExitPressedOnce = false;
    private SharedPrefManager mSharedPrefManager;
    private BroadcastReceiver gcmBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPrefManager = SharedPrefManager.getInstance(this);

        btn_english = findViewById(R.id.btn_english);

        mUiHelper = new UiHelper(MainActivity.this);

        showDialog(SPLASH_DIALOG);

        if (!Networking.isNetworkAvailable(MainActivity.this)) {
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
                        Log.i(TAG, "Success--> success");
                        break;
                    case Constants.REGISTRATION_ERROR:
                        Log.i(TAG, "Error--> error");
                        break;
                    case Constants.REGISTRATION_TOKEN_SENT:
                        Log.i(TAG, "Token Sent--> sent");
                        break;
                    default:
                        Log.i(TAG, "Default--> default");
                        break;
                }
            }
        };

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (ConnectionResult.SUCCESS != resultCode) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                mUiHelper.showToastLong("Google Play Service is not Install/Enabled in this device!");
                GooglePlayServicesUtil.showErrorNotification(resultCode, MainActivity.this);
            } else {
                mUiHelper.showToastLong("This device does not support for Google Play Service!");
            }
        } else {
            if (mSharedPrefManager.getString(Constants.GCM_TOKEN).isEmpty()) {
                startService(new Intent(MainActivity.this, GCMRegistrationIntentService.class));
            } else {
                Log.i(TAG, "GCM registration already done...!");
            }
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
        LocalBroadcastManager.getInstance(this).registerReceiver(gcmBroadcastReceiver,
                new IntentFilter(Constants.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(gcmBroadcastReceiver,
                new IntentFilter(Constants.REGISTRATION_ERROR));
        LocalBroadcastManager.getInstance(this).registerReceiver(gcmBroadcastReceiver,
                new IntentFilter(Constants.REGISTRATION_TOKEN_SENT));
    }

    @Override
    protected void onPause() {
        super.onPause();
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

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        mUiHelper.showToastShortCentre(getResources().getString(R.string.click_back_again_to_close));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, SPLASH_TIME_OUT);
    }
}
