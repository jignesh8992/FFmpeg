package com.jignesh.ffmpegdemo;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.service.ExecuteCommandService;
import com.github.hiteshsondhi88.libffmpeg.util.FFmpegUtils;
import com.github.hiteshsondhi88.libffmpeg.util.Log;
import com.github.hiteshsondhi88.libffmpeg.util.ProgressDialogHelper;


public class SampleActivity extends AppCompatActivity {


    private String videoFolder = null;
    private String videoPath = null;
    private Context mContext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        mContext = SampleActivity.this;

        videoFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/videokit/";

        videoPath = videoFolder + "in.mp4";

        FFmpegUtils.checkForPermissionsMAndAbove(SampleActivity.this, true);


        Button invoke = findViewById(R.id.invokeButton);
        invoke.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                TextView commandText = findViewById(R.id.CommandText);
                String commandStr = commandText.getText().toString();
                String[] command = FFmpegUtils.utilConvertToComplex(commandStr);
                long duration = FFmpegUtils.getVideoLength(mContext, videoPath);

                Intent serviceIntent = new Intent(mContext, ExecuteCommandService.class);
                serviceIntent.putExtra("command", command);
                serviceIntent.putExtra("duration", duration);
                startService(serviceIntent);
                initProgressReceiver();
            }
        });


    }

    public void cancel(View view) {
        FFmpeg.getInstance(mContext).cancel();
    }

    public void initProgressReceiver() {
        LocalBroadcastManager.getInstance(this).registerReceiver(progressReceiver,
                new IntentFilter(ExecuteCommandService.ACTION_TASK)
        );
    }

    private BroadcastReceiver progressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int type = intent.getIntExtra("type", 0);
            switch (type) {
                case FFmpegUtils.START:
                    Log.w("START");
                    FFmpegUtils.acquireWakeLock(mContext);
                    ProgressDialogHelper.getInstance(mContext).show("Processing video");
                    break;

                case FFmpegUtils.SUCCESS:
                    String success = intent.getStringExtra(ExecuteCommandService.EXTRA_MESSAGE);
                    Log.w("SUCCESS: " + success);
                    FFmpegUtils.releaseWakeLock();
                    break;

                case FFmpegUtils.PROGRESS:
                    String progress = intent.getStringExtra(ExecuteCommandService.EXTRA_MESSAGE);
                    //  Log.w("PROGRESS: " + progress);
                    break;

                case FFmpegUtils.FAILURE:
                    String failure = intent.getStringExtra(ExecuteCommandService.EXTRA_MESSAGE);
                    Log.w("FAILURE: " + failure);
                    FFmpegUtils.releaseWakeLock();
                    ProgressDialogHelper.getInstance(mContext).dismissDialog();
                    break;

                case FFmpegUtils.CANCELLED:
                    Log.w("CANCELLED");
                    FFmpegUtils.releaseWakeLock();
                    ProgressDialogHelper.getInstance(mContext).dismissDialog();
                    break;

                case FFmpegUtils.PERCENTAGE:
                    int percentage = intent.getIntExtra(ExecuteCommandService.EXTRA_PERCENTAGE, 0);
                    Log.w("PERCENTAGE: " + percentage);
                    ProgressDialogHelper.getInstance(mContext).progressDialog(percentage);
                    break;

                case FFmpegUtils.FINISH:
                    Log.w("FINISH");
                    FFmpegUtils.releaseWakeLock();
                    ProgressDialogHelper.getInstance(mContext).dismissDialog();
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressReceiver != null)
            unregisterReceiver(progressReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FFmpegUtils.checkForPermissionsMAndAbove(SampleActivity.this, true);
    }
}
