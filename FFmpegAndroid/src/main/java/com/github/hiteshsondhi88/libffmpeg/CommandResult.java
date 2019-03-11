package com.github.hiteshsondhi88.libffmpeg;

import com.github.hiteshsondhi88.libffmpeg.util.FFmpegUtils;

public class CommandResult {
    public final String output;
    public final boolean success;

    public CommandResult(boolean success, String output) {
        this.success = success;
        this.output = output;
    }

    public static CommandResult getDummyFailureResponse() {
        return new CommandResult(false, "");
    }

    public static CommandResult getOutputFromProcess(Process process) {
        String output;
        if (success(process.exitValue())) {
            output = FFmpegUtils.convertInputStreamToString(process.getInputStream());
        } else {
            output = FFmpegUtils.convertInputStreamToString(process.getErrorStream());
        }
        return new CommandResult(success(process.exitValue()), output);
    }

    public static boolean success(Integer exitValue) {
        return exitValue != null && exitValue == 0;
    }

}