package com.jignesh.ffmpegdemo;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.github.hiteshsondhi88.libffmpeg.util.FFmpegUtils;
import com.github.hiteshsondhi88.libffmpeg.util.Log;

public class FFmpegApplication extends Application {
    private Context mContext;
    private FFmpeg ffmpeg;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = FFmpegApplication.this;
        initFFmpeg();
    }

    private void initFFmpeg() {
        try {
            ffmpeg = FFmpeg.getInstance(this);
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    Log.w("onFailure: " + "Error in Loading FFmpge");
                }

                @Override
                public void onSuccess() {


                    Log.w("onSuccess: " + "FFmpeg loaded successfully");
                }
            });
        } catch (FFmpegNotSupportedException e) {
            Log.w("FFmpegNotSupportedException: " + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            Log.w("Exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
