package com.tspolice.htplive.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.tspolice.htplive.R;
import com.tspolice.htplive.activities.AlertsActivity;

public class GCMPushReceiverService extends GcmListenerService {

    private static final String TAG = "GCMPushReceivService-->";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        if (data != null) {
            Log.i(TAG, "data-->" + data.toString());
            String title = data.getString("title");
            String message = data.getString("message");
            Log.i(TAG, "title-->" + title);
            Log.i(TAG, "message-->" + message);
            sendNotificationToDevice(message, title);
        }
    }

    private void sendNotificationToDevice(String message, String title) {
        /*Intent intent = new Intent(context, AlertsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(GCMPushReceiverService.this);
        builder.setSmallIcon(R.drawable.ic_logo);
        builder.setContentIntent(PendingIntent.getActivity(GCMPushReceiverService.this, 0, intent, 0));
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo));
        builder.setContentTitle(title);
        builder.setContentText(message);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());*/

        PendingIntent pendingIntent = PendingIntent.getActivity(GCMPushReceiverService.this, 0,
                new Intent(GCMPushReceiverService.this, AlertsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
                PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(GCMPushReceiverService.this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(message)
                .setContentTitle(title)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }
}