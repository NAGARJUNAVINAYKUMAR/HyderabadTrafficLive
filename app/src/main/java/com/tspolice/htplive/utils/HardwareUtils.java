package com.tspolice.htplive.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class HardwareUtils {

    private Context mContext;
    private static String uniqueID = null;

    public HardwareUtils(Context context) {
        this.mContext = context;
    }

    public synchronized static String getDeviceUUID(Context context) {
        SharedPrefManager mSharedPrefManager = SharedPrefManager.getInstance(context);
        if (uniqueID == null) {
            uniqueID = mSharedPrefManager.getString(Constants.DEVICE_UUID);
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                mSharedPrefManager.putString(Constants.DEVICE_UUID, uniqueID);
            }
        }
        return uniqueID;
    }

    @SuppressLint("HardwareIds")
    public String getDeviceUid() {
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String uniqueDeviceId = telephonyManager.getDeviceId();
        if (TextUtils.isEmpty(uniqueDeviceId)) {
            // for tablets
            uniqueDeviceId = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);
        }
        return uniqueDeviceId;
    }

    public String getMacId() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF));
                }
                Log.e("afterForLoop", res1.toString());
                return res1.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }
}
