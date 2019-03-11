package com.github.hiteshsondhi88.libffmpeg.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

public class FFmpegUtils {

    public static ProgressDialog dialog;
    private static PowerManager.WakeLock wakeLock;


    public static final int START = 0;
    public static final int PROGRESS = 1;
    public static final int FAILURE = 2;
    public static final int CANCELLED = 3;
    public static final int PERCENTAGE = 4;
    public static final int FINISH = 5;
    public static final int SUCCESS = 6;


    public static boolean isDebug(Context context) {
        return (0 != (context.getApplicationContext().getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE));
    }

    public static void close(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                // Do nothing
            }
        }
    }

    public static void close(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                // Do nothing
            }
        }
    }

    public static String convertInputStreamToString(InputStream inputStream) {
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            String str;
            StringBuilder sb = new StringBuilder();
            while ((str = r.readLine()) != null) {
                sb.append(str);
            }
            return sb.toString();
        } catch (IOException e) {
            Log.e("error converting input stream to string", e);
        }
        return null;
    }

    public static void destroyProcess(Process process) {
        if (process != null)
            process.destroy();
    }

    public static boolean killAsync(AsyncTask asyncTask) {
        return asyncTask != null && !asyncTask.isCancelled() && asyncTask.cancel(true);
    }

    public static boolean isProcessCompleted(Process process) {
        try {
            if (process == null) return true;
            process.exitValue();
            return true;
        } catch (IllegalThreadStateException e) {
            // do nothing
        }
        return false;
    }

    public static float getFpsFromCommandMessage(String message) {
        String[] stream = message.split("Stream")[1].split("fps")[0].split(",");
        return Float.parseFloat(stream[stream.length - 1].trim());
    }

    public static long getFramePositionFromCommandMessage(String message) {
        return Long.parseLong(message.split("frame=")[1].split("fps=")[0].trim());
    }

    @TargetApi(23)
    public static void checkForPermissionsMAndAbove(Activity act, boolean isBlocking) {
        Log.w("checkForPermissions() called");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Here, thisActivity is the current activity
            if (act.checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED ||

                    act.checkSelfPermission(
                            Manifest.permission.INTERNET)
                            != PackageManager.PERMISSION_GRANTED
            ) {


                // No explanation needed, we can request the permission.
                act.requestPermissions(
                        new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.INTERNET
                        },
                        0);
                if (isBlocking) {
                    while (true) {
                        if (act.checkSelfPermission(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_GRANTED) {

                            FFmpegUtils.copyDemoVideoFromAssetsToSDIfNeeded(act);

                            Log.w("Got permissions, exiting block loop");
                            break;
                        }
                        Log.w("Sleeping, waiting for permissions");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
            // permission already granted
            else {
                FFmpegUtils.copyDemoVideoFromAssetsToSDIfNeeded(act);
                Log.w("permission already granted");
            }
        } else {
            Log.w("Below M, permissions not via code");
        }

    }

    public static String[] utilConvertToComplex(String str) {
        String[] complex = str.split(" ");
        return complex;
    }


    public static long getVideoLength(Context mContext, String video_path) {
        MediaPlayer mp = MediaPlayer.create(mContext, Uri.parse(video_path));
        long videoLengthInMillis = TimeUnit.MILLISECONDS.toMillis(mp.getDuration());
        mp.release();
        Log.w("onStart: VideoLeng -> " + videoLengthInMillis);
        return (long) (videoLengthInMillis * 1.0f / 1000);
    }

    public static void printCommand(String[] command) {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        for (int i = 0; i < command.length; i++) {
            sb.append("\"");
            sb.append(command[i]);
            sb.append("\"");
            if (i < command.length - 1)
                sb.append(",");
        }
        sb.append("}");
        Log.w(sb.toString());
    }

    public static boolean checkIfFolderExists(String fullFileName) {
        File f = new File(fullFileName);
        //Log.d(Prefs.TAG,"Checking if : " +  fullFileName + " exists" );
        if (f.exists() && f.isDirectory()) {
            //Log.d(Prefs.TAG,"Direcory: " +  fullFileName + " exists" );
            return true;
        } else {
            return false;
        }
    }

    public static boolean createFolder(String folderPath) {
        File f = new File(folderPath);
        return f.mkdirs();
    }

    public static void copyDemoVideoFromAssetsToSDIfNeeded(Activity act) {

        String destinationFolderPath =
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/videokit/";
        File destVid = new File(destinationFolderPath + "in.mp4");
        try {
            if (!checkIfFolderExists(destinationFolderPath)) {
                createFolder(destinationFolderPath);
                Log.w("Demo videos folder was created.");

            } else {
                Log.w("Demo videos folder already exist.");
            }


            if (!destVid.exists()) {
                Log.w("Adding vid file at " + destVid.getAbsolutePath());
                InputStream is = act.getAssets().open("in.mp4");
                BufferedOutputStream o = null;
                try {
                    byte[] buff = new byte[10000];
                    int read = -1;
                    o = new BufferedOutputStream(new FileOutputStream(destVid), 10000);
                    while ((read = is.read(buff)) > -1) {
                        o.write(buff, 0, read);
                    }
                    Log.w("Copy " + destVid.getAbsolutePath() + " from assets to SDCARD " +
                            "finished succesfully");
                } catch (Exception e) {
                    Log.w("Failed copying: " + destVid.getAbsolutePath());
                } finally {
                    is.close();
                    if (o != null) o.close();
                }

            } else {
                Log.w("Demo video already exist.");
            }

        } catch (FileNotFoundException e) {
            Log.w(e.getMessage());
        } catch (IOException e) {
            Log.w(e.getMessage());
        }
    }

    @SuppressLint("InvalidWakeLockTag")
    public static void acquireWakeLock(Context mContext) {
        PowerManager powerManager = (PowerManager) (mContext).getSystemService(Activity
                .POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "VK_LOCK");
        Log.w("Acquire wake lock");
        wakeLock.acquire();
    }

    public static void releaseWakeLock() {
        if (wakeLock.isHeld()) {
            wakeLock.release();
            Log.w("Wake lock released");
        } else {
            Log.w("Wake lock is already released, doing nothing");
        }
    }


}
