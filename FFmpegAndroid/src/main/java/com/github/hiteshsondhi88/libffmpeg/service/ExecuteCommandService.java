package com.github.hiteshsondhi88.libffmpeg.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.interfaces.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.util.FFmpegUtils;
import com.github.hiteshsondhi88.libffmpeg.util.Log;
import com.github.hiteshsondhi88.libffmpeg.util.ProgressNotification;

public class ExecuteCommandService extends Service {

    private String TAG = ExecuteCommandService.class.getSimpleName();
    private Context mContext;
    private FFmpeg ffmpeg;

    public static final String ACTION_TASK = ExecuteCommandService.class.getName();
    public static final String EXTRA_PERCENTAGE = "progress_percentage";
    public static final String EXTRA_MESSAGE = "progress_message";


    public int onStartCommand(Intent intent, int flags, int startId) {
        String[] command = intent.getStringArrayExtra("command");
        long duration = intent.getLongExtra("duration", 0);
        mContext = ExecuteCommandService.this;
        ffmpeg = FFmpeg.getInstance(this);

        FFmpegUtils.printCommand(command);
        Log.w("duration: " + duration);

        try {
            ffmpeg.execute(command, duration,
                    new FFmpegExecuteResponseHandler() {
                        @Override
                        public void onSuccess(String message) throws FFmpegCommandAlreadyRunningException {
                            sendMessage(FFmpegUtils.SUCCESS, message);
                        }

                        @Override
                        public void onProgress(String message) {
                            sendMessage(FFmpegUtils.PROGRESS, message);
                            // progressNotification.finishNotification(mContext);


                        }

                        @Override
                        public void onFailure(String message) {
                            sendMessage(FFmpegUtils.FAILURE, message);
                            ProgressNotification.getInstance(mContext).cancelNotification();


                        }

                        @Override
                        public void cancelled() {
                            sendAction(FFmpegUtils.CANCELLED);
                            ProgressNotification.getInstance(mContext).cancelNotification();

                        }

                        @Override
                        public void onProgressPercent(float percentage) {
                            sendProgress(Math.round(percentage));
                        }

                        @Override
                        public void onStart() {
                            sendAction(FFmpegUtils.START);
                            ProgressNotification.getInstance(mContext).createNotification(
                                    "Executing command");
                        }

                        @Override
                        public void onFinish() {
                            sendAction(FFmpegUtils.FINISH);
                            ProgressNotification.getInstance(mContext).finishNotification();
                            stopSelf();
                        }
                    });


        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private void sendProgress(int percentage) {
        if (percentage <= 100 && percentage >= 0) {
            Intent intent = new Intent(ACTION_TASK);
            intent.putExtra("type", FFmpegUtils.PERCENTAGE);
            intent.putExtra(EXTRA_PERCENTAGE, percentage);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            ProgressNotification.getInstance(mContext).setProgress(percentage);

        }
    }

    private void sendMessage(int type, String message) {
        Intent intent = new Intent(ACTION_TASK);
        intent.putExtra("type", type);
        intent.putExtra(EXTRA_MESSAGE, message);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    private void sendAction(int type) {
        Intent intent = new Intent(ACTION_TASK);
        intent.putExtra("type", type);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}