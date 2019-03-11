package com.github.hiteshsondhi88.libffmpeg.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;

public class ProgressDialogHelper {

    public static ProgressDialogHelper instance;
    public ProgressDialog dialog;
    public static Context mContext;

    public static ProgressDialogHelper getInstance(Context mContext) {
        if (instance == null) {
            synchronized (ProgressDialogHelper.class) {
                if (instance == null) {
                    instance = new ProgressDialogHelper();
                    instance.mContext = mContext;
                    instance.create();
                }
            }
        }
        return instance;
    }


    public void create() {
        try {
            dialog = new ProgressDialog(mContext);
            dialog.setMax(100);
            dialog.setCancelable(false);
            dialog.setProgressStyle(android.app.ProgressDialog.STYLE_HORIZONTAL);
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FFmpeg.getInstance(mContext).cancel();
                            dismissDialog();
                        }
                    });
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "EXECUTE IN BACKGROUND",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismissDialog();
                        }
                    });
        } catch (Exception e) {
            Log.w("showDialog: " + e.toString());
        }
    }

    public void show(String msg, boolean isCancelable, boolean isBackgroundRunnable) {
        if (dialog == null) {
            create();
        }

        try {
            if (dialog != null) {
                dialog.setMessage(msg);
                dialog.setProgress(0);
                dialog.show();

                if (isCancelable) {
                    dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.VISIBLE);
                } else {
                    dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.GONE);
                }
                if (isBackgroundRunnable) {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.VISIBLE);
                } else {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            Log.w(e.toString());
        }


    }

    public void progressDialog(int per) {
        if (dialog != null && dialog.isShowing()) {
            dialog.setProgress(per);
        }
    }

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.setProgress(0);
            dialog.dismiss();
        }
    }
}
