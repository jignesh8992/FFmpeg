package com.github.hiteshsondhi88.libffmpeg;

import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.interfaces.FFmpegExecuteResponseHandler;

 public class ExecuteBinaryResponseHandler implements FFmpegExecuteResponseHandler {

    @Override
    public void onSuccess(String message) throws FFmpegCommandAlreadyRunningException {

    }

    @Override
    public void onProgress(String message) {

    }

    @Override
    public void onFailure(String message) {

    }

    @Override
    public void cancelled() {

    }

    @Override
    public void onProgressPercent(float percentage) {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onFinish() {

    }
}
