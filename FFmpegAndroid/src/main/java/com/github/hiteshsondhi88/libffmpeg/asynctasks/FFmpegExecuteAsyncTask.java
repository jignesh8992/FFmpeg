package com.github.hiteshsondhi88.libffmpeg.asynctasks;

import android.os.AsyncTask;

import com.github.hiteshsondhi88.libffmpeg.CommandResult;
import com.github.hiteshsondhi88.libffmpeg.util.Log;
import com.github.hiteshsondhi88.libffmpeg.ShellCommand;
import com.github.hiteshsondhi88.libffmpeg.util.FFmpegUtils;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.interfaces.FFmpegExecuteResponseHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;

public class FFmpegExecuteAsyncTask extends AsyncTask<Void, String, CommandResult>  {

    private final String[] cmd;
    private final FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler;
    private final ShellCommand shellCommand;
    private final long timeout;
    private long startTime;
    private Process process;
    private String output = "";

    private float duration;
    private float outputFps;

    public FFmpegExecuteAsyncTask(String[] cmd, long timeout, FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler, float duration) {
        this.cmd = cmd;
        this.timeout = timeout;
        this.ffmpegExecuteResponseHandler = ffmpegExecuteResponseHandler;
        this.shellCommand = new ShellCommand();
        this.duration = duration;
    }

    @Override
    protected void onPreExecute() {
        startTime = System.currentTimeMillis();
        if (ffmpegExecuteResponseHandler != null) {
            ffmpegExecuteResponseHandler.onStart();
        }
    }

    @Override
    protected CommandResult doInBackground(Void... params) {
        try {
            process = shellCommand.run(cmd);
            if (process == null) {
                return CommandResult.getDummyFailureResponse();
            }



            Log.d("Running publishing updates method");
            checkAndUpdateProcess();

            if (isCancelled()){
                ffmpegExecuteResponseHandler.cancelled();
                return null;
            }



            return CommandResult.getOutputFromProcess(process);
        } catch (TimeoutException e) {
            Log.e("FFmpeg timed out", e);
            return new CommandResult(false, e.getMessage());
        } catch (Exception e) {
            Log.e("Error running FFmpeg", e);
        } finally {
            FFmpegUtils.destroyProcess(process);
        }
        return CommandResult.getDummyFailureResponse();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        if (values != null && values[0] != null && ffmpegExecuteResponseHandler != null) {
            ffmpegExecuteResponseHandler.onProgress(values[0]);
            if (values[0].contains("Stream #") && values[0].contains("Video")) {
                outputFps = FFmpegUtils.getFpsFromCommandMessage(values[0]);
            }
//            if (values[0].contains("Duration:")) {
//                duration = FFmpegUtils.getDurationFromCommandMessage(values[0]);
//            }
            if (values[0].contains("frame=")) {
                float frameNumber = FFmpegUtils.getFramePositionFromCommandMessage(values[0]);
                ffmpegExecuteResponseHandler.onProgressPercent((frameNumber / (outputFps * duration)) * 100);
            }
        }
    }

    @Override
    protected void onPostExecute(CommandResult commandResult) {
        if (ffmpegExecuteResponseHandler != null) {
            output += commandResult.output;
            if (commandResult.success) {
                try {
                    ffmpegExecuteResponseHandler.onSuccess(output);
                } catch (FFmpegCommandAlreadyRunningException e) {
                    e.printStackTrace();
                }
            } else {
                ffmpegExecuteResponseHandler.onFailure(output);
            }
            ffmpegExecuteResponseHandler.onFinish();
        }
    }

    private void checkAndUpdateProcess() throws TimeoutException, InterruptedException {
        while (!FFmpegUtils.isProcessCompleted(process)) {
            // checking if process is completed
            if (FFmpegUtils.isProcessCompleted(process)) {
                return;
            }

            // Handling timeout
            if (timeout != Long.MAX_VALUE && System.currentTimeMillis() > startTime + timeout) {
                throw new TimeoutException("FFmpeg timed out");
            }

            try {
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                while ((line = reader.readLine()) != null) {
                    if (isCancelled()) {
                        return;
                    }

                    output += line+"\n";
                    publishProgress(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isProcessCompleted() {
        return FFmpegUtils.isProcessCompleted(process);
    }


    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
