package com.example.android.autosend.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.android.autosend.Services.AlarmService;
import com.example.android.autosend.Services.BootService;
import com.example.android.autosend.Services.MessageSendService;

/**
 * Created by Ishmita on 18-01-2017.
 */
public class OnBootCompletedReceiver extends BroadcastReceiver {

    private static final String TAG = "OnBootCompleteReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        //WakefulIntentService.acquireStaticLock(context);
        Log.d("OnBootCompleteReceiver", "Boot completed!");
        Intent i = new Intent(context, BootService.class);
        ComponentName componentName = context.startService(i);

        if(componentName == null) {
            Log.e(TAG, "cannot start service after reboot");
        }else {
            Log.d(TAG ,"Boot Service started! ");
        }
    }
}
