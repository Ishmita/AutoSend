package com.example.android.autosend.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import com.example.android.autosend.Services.MessageSendService;

/**
 * Created by Ishmita on 11-01-2017.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = "AlarmReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "alarm received");
        int id = intent.getIntExtra("id", -1);
        Log.d(TAG, "id: "+id);
        Intent intent1 = new Intent(context, MessageSendService.class);
        intent1.putExtra("id", id);
        context.startService(intent1);

    }
}
