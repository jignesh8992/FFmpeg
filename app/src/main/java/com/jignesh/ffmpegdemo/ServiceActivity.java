package com.jignesh.ffmpegdemo;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.service.ExecuteCommandService;
import com.github.hiteshsondhi88.libffmpeg.util.FFmpegUtils;
import com.github.hiteshsondhi88.libffmpeg.util.Log;
import com.github.hiteshsondhi88.libffmpeg.util.ProgressDialogHelper;


public class ServiceActivity extends AppCompatActivity {


    private String videoFolder = null;
    private String videoPath = null;
    private Context mContext;
    private TextView commandTextView;
    private TextView commandText;
    private CheckBox chkCancel;
    private CheckBox chkBackground;
    private Button btnExecute;
    private Button btnCancel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        mContext = ServiceActivity.this;
        initViews();
        initAction();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        videoFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/videokit/";
        videoPath = videoFolder + "in.mp4";
        FFmpegUtils.checkForPermissionsMAndAbove(ServiceActivity.this, true);

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
    }

    private void initAction() {
        btnExecute.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
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

        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                FFmpeg.getInstance(mContext).cancel();
            }
        });
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
                    ProgressDialogHelper.getInstance(mContext).show("Processing video",
                            chkCancel.isChecked(), chkBackground.isChecked());
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
        try {
            if (progressReceiver != null)
                unregisterReceiver(progressReceiver);
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FFmpegUtils.checkForPermissionsMAndAbove(ServiceActivity.this, true);
    }
}
