package com.github.hiteshsondhi88.libffmpeg;

import com.github.hiteshsondhi88.libffmpeg.util.Log;
import com.github.hiteshsondhi88.libffmpeg.util.FFmpegUtils;

import java.io.IOException;

public class ShellCommand {

    public Process run(String[] commandString) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(commandString);
        } catch (IOException e) {
            Log.e("Exception while trying to run: " + commandString, e);
        }
        return process;
    }

    public   CommandResult runWaitFor(String[] s) {
        Process process = run(s);

        Integer exitValue = null;
        String output = null;
        try {
            if (process != null) {
                exitValue = process.waitFor();

                if (CommandResult.success(exitValue)) {
                    output = FFmpegUtils.convertInputStreamToString(process.getInputStream());
                } else {
                    output = FFmpegUtils.convertInputStreamToString(process.getErrorStream());
                }
            }
        } catch (InterruptedException e) {
            Log.e("Interrupt exception", e);
        } finally {
            FFmpegUtils.destroyProcess(process);
        }

        return new CommandResult(CommandResult.success(exitValue), output);
    }

}