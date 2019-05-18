package com.bagus.purchasingapp_mtn.service;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.bagus.purchasingapp_mtn.utils.Constants;

public class AppBroadcastReceiver extends BroadcastReceiver {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w("LOG_SERVICE_BROADCAST", "onReceive");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent serviceIntent = new Intent(context, AppService.class);
            serviceIntent.setAction(Constants.STARTFOREGROUND_ACTION);
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(new Intent(context, AppService.class));
        }
    }
}

