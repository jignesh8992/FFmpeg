package com.jignesh.ffmpegdemo;


import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.interfaces.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.util.FFmpegUtils;
import com.github.hiteshsondhi88.libffmpeg.util.Log;
import com.github.hiteshsondhi88.libffmpeg.util.ProgressDialogHelper;
import com.github.hiteshsondhi88.libffmpeg.util.ProgressNotification;


public class SimpleActivity extends AppCompatActivity {


    private String videoFolder = null;
    private String videoPath = null;
    private Context mContext;
    private TextView commandTextView;
    private TextView commandText;
    private CheckBox chkCancel;
    private CheckBox chkBackground;
    private Button btnExecute;
    private Button btnCancel;
    private FFmpeg ffmpeg;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        mContext = SimpleActivity.this;
        initViews();
        initAction();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        videoFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/videokit/";
        videoPath = videoFolder + "in.mp4";
        FFmpegUtils.checkForPermissionsMAndAbove(SimpleActivity.this, true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }


    public void initViews() {
        commandTextView = findViewById(R.id.CommandTextView);
        commandText = findViewById(R.id.CommandText);
        chkCancel = findViewById(R.id.chk_cancel);
        chkBackground = findViewById(R.id.chk_background);
        btnExecute = findViewById(R.id.btn_execute);
        btnCancel = findViewById(R.id.btn_cancel);
        chkBackground.setVisibility(View.GONE);
    }

    private void initAction() {
        btnExecute.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String commandStr = commandText.getText().toString();
                String[] command = FFmpegUtils.utilConvertToComplex(commandStr);
                long duration = FFmpegUtils.getVideoLength(mContext, videoPath);

                ffmpeg = FFmpeg.getInstance(mContext);

                FFmpegUtils.printCommand(command);
                Log.w("duration: " + duration);

                try {
                    ffmpeg.execute(command, duration,
                            new FFmpegExecuteResponseHandler() {
                                @Override
                                public void onSuccess(String message) throws FFmpegCommandAlreadyRunningException {
                                    Log.w("SUCCESS: " + message);
                                    FFmpegUtils.releaseWakeLock();
                                }

                                @Override
                                public void onProgress(String message) {

                                    // progressNotification.finishNotification(mContext);


                                }

                                @Override
                                public void onFailure(String message) {
                                    Log.w("FAILURE: " + message);
                                    FFmpegUtils.releaseWakeLock();
                                    ProgressDialogHelper.getInstance(mContext).dismissDialog();
                                    ProgressNotification.getInstance(mContext).cancelNotification();

                                }

                                @Override
                                public void cancelled() {
                                    Log.w("CANCELLED");
                                    FFmpegUtils.releaseWakeLock();
                                    ProgressDialogHelper.getInstance(mContext).dismissDialog();
                                    ProgressNotification.getInstance(mContext).cancelNotification();

                                }

                                @Override
                                public void onProgressPercent(float percentage) {
                                    Log.w("PERCENTAGE: " + percentage);
                                    ProgressDialogHelper.getInstance(mContext).progressDialog(Math.round(percentage));
                                    ProgressNotification.getInstance(mContext).setProgress(Math.round(percentage));

                                }

                                @Override
                                public void onStart() {
                                    Log.w("START");
                                    FFmpegUtils.acquireWakeLock(mContext);
                                    ProgressDialogHelper.getInstance(mContext).show("Processing " +
                                            "video", chkCancel.isChecked(), false);

                                    ProgressNotification.getInstance(mContext).createNotification(
                                            "Processing video");
                                }

                                @Override
                                public void onFinish() {
                                    FFmpegUtils.releaseWakeLock();
                                    ProgressDialogHelper.getInstance(mContext).dismissDialog();
                                    ProgressNotification.getInstance(mContext).finishNotification();


                                }
                            });


                } catch (FFmpegCommandAlreadyRunningException e) {
                    e.printStackTrace();
                }


            }
        });

        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                FFmpeg.getInstance(mContext).cancel();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        FFmpegUtils.checkForPermissionsMAndAbove(SimpleActivity.this, true);
    }
}
