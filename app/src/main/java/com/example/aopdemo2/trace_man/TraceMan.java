package com.example.aopdemo2.trace_man;

import android.os.Build;
import android.os.Looper;
import android.os.Trace;
import android.util.Log;

/**
 * 插桩类
 */
public class TraceMan {
    private static final String TAG = "TraceMan";

    public static void start(String name) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Trace.beginSection(name);
        }
        Log.d(TAG, "start ->  methodName: "+name+" currentTimeMillis: "+System.currentTimeMillis()+" isInMainThread: "+isInMainThread());
    }

    public static void end(String name) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Trace.endSection();
        }
        Log.d(TAG, "end ->  methodName: "+name+" currentTimeMillis: "+System.currentTimeMillis()+" isInMainThread: "+isInMainThread());
    }



    private static boolean isInMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}