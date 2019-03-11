package com.github.hiteshsondhi88.libffmpeg.interfaces;

import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

public interface FFmpegExecuteResponseHandler extends ResponseHandler {

    /**
     * on Success
     * @param message complete output of the FFmpeg command
     */
    public void onSuccess(String message) throws FFmpegCommandAlreadyRunningException;

    /**
     * on Progress
     * @param message current output of FFmpeg command
     */
    public void onProgress(String message);

    /**
     * on Failure
     * @param message complete output of the FFmpeg command
     */
    public void onFailure(String message);

    public  void cancelled();

    void onProgressPercent(float percentage);
}
