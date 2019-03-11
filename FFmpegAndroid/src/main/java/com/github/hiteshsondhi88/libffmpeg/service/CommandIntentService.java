
package com.github.hiteshsondhi88.libffmpeg.service;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.util.Log;
import com.github.hiteshsondhi88.libffmpeg.util.ProgressNotification;


public class CommandIntentService extends IntentService {

    public static final String ACTION_CANCEL = "com.github.hiteshsondhi88.libffmpeg.service" +
            ".action.CANCEL";
    public static final String ACTION_OPEN = "com.github.hiteshsondhi88.libffmpeg.service.action" +
            ".OPEN";

    public CommandIntentService() {
        super("CommandIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.w("CommandIntentService ");
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CANCEL.equals(action)) {
                Log.w("CommandIntentService " + "CANCEL");
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
                FFmpeg.getInstance(this).cancel();
                ProgressNotification.getInstance(this).cancelNotification();
            } else if (ACTION_OPEN.equals(action)) {
                Log.w("CommandIntentService " + "OPEN");
                Toast.makeText(this, "Open", Toast.LENGTH_SHORT).show();
                ProgressNotification.getInstance(this).cancelNotification();
            }
        }
    }
}
