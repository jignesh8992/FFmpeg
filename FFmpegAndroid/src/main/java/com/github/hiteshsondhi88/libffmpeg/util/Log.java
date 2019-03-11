package com.github.hiteshsondhi88.libffmpeg.util;

@SuppressWarnings("unused")
public class Log {

    public static String TAG = "JNP_FFMpeg";
    //  private static String TAG = FFmpeg.class.getSimpleName();
    private static boolean DEBUG = false;

    public static void setDEBUG(boolean DEBUG) {
        Log.DEBUG = DEBUG;
    }

    public static void setTAG(String tag) {
        Log.TAG = tag;
    }

    public static void d(Object obj) {
        if (DEBUG) {
            android.util.Log.d(TAG, obj != null ? obj.toString() : null + "");
        }
    }

    public static void e(Object obj) {
        if (DEBUG) {
            android.util.Log.e(TAG, obj != null ? obj.toString() : null + "");
        }
    }

    public static void w(Object obj) {
        if (DEBUG) {
            android.util.Log.w(TAG, obj != null ? obj.toString() : null + "");
        }
    }

    public static void i(Object obj) {
        if (DEBUG) {
            android.util.Log.i(TAG, obj != null ? obj.toString() : null + "");
        }
    }

    public static void v(Object obj) {
        if (DEBUG) {
            android.util.Log.v(TAG, obj != null ? obj.toString() : null + "");
        }
    }

    public static void e(Object obj, Throwable throwable) {
        if (DEBUG) {
            android.util.Log.e(TAG, obj != null ? obj.toString() : null + "", throwable);
        }
    }

    public static void e(Throwable throwable) {
        if (DEBUG) {
            android.util.Log.e(TAG, "", throwable);
        }
    }

}
