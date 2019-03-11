package com.github.hiteshsondhi88.libffmpeg.util;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import com.github.hiteshsondhi88.libffmpeg.R;
import com.github.hiteshsondhi88.libffmpeg.service.CommandIntentService;

public class ProgressNotification {

    public static ProgressNotification instance;

    public static Context mContext;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private final String NOTIFICATION_CHANNEL_ID = "10001";
    private int notification_id = 1;

    public static ProgressNotification getInstance(Context mContext) {
        if (instance == null) {
            synchronized (ProgressNotification.class) {
                if (instance == null) {
                    instance = new ProgressNotification();
                    instance.mContext = mContext;
                }
            }
        }
        return instance;
    }


    /**
     * Create and push the notification
     */
    public void createNotification(String message) {
        /**Creates an explicit intent for an Activity in your app**/

        // Dismiss Action.
        Intent dismissIntent = new Intent(mContext, CommandIntentService.class);
        dismissIntent.setAction(CommandIntentService.ACTION_CANCEL);

        PendingIntent dismissPendingIntent = PendingIntent.getService(mContext, 0, dismissIntent,
                0);
        NotificationCompat.Action dismissAction =
                new NotificationCompat.Action.Builder(
                        R.mipmap.ic_launcher,
                        "CANCEL",
                        dismissPendingIntent)
                        .build();


        mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(message)
                .setContentText("0/100")
                .setAutoCancel(false)
                .addAction(dismissAction)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel =
                    new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME",
                            importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300,
                    200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;

        mBuilder.setProgress(100, 0, false);
        mNotificationManager.notify(notification_id, mBuilder.build());


    }

    public void setProgress(int percentage) {
        mBuilder.setProgress(100, percentage, false);
        mBuilder.setContentText(percentage + "/" + 100);
        mNotificationManager.notify(notification_id, mBuilder.build());
    }

    @SuppressLint("RestrictedApi")
    public void finishNotification() {
        mBuilder.setContentText("Execution complete");
        mBuilder.setProgress(0, 0, false);
        // Remove CANCEL Action.
        mBuilder.mActions.clear();
        // Dismiss Action.
        Intent dismissIntent = new Intent(mContext, CommandIntentService.class);
        dismissIntent.setAction(CommandIntentService.ACTION_CANCEL);

        PendingIntent dismissPendingIntent = PendingIntent.getService(mContext, 0, dismissIntent,
                0);
        NotificationCompat.Action dismissAction =
                new NotificationCompat.Action.Builder(
                        R.mipmap.ic_launcher,
                        "OPEN",
                        dismissPendingIntent)
                        .build();
        mBuilder.addAction(dismissAction);

        mNotificationManager.notify(notification_id, mBuilder.build());
    }

    public void cancelNotification() {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) mContext.getSystemService(ns);
        nMgr.cancel(notification_id);
    }


}