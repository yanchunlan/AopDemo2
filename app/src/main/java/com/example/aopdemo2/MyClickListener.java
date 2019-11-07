package com.example.aopdemo2;

import android.util.Log;
import android.view.View;

/**
 * author:  ycl
 * date:  2019/11/06 17:16
 * desc:
 */
public class MyClickListener implements View.OnClickListener {
    private static final String TAG = "MyClickListener";

    @Override
    public void onClick(View view) {
        show();
    }


    private void show() {
        Log.d(TAG, "show: ");
    }
}
