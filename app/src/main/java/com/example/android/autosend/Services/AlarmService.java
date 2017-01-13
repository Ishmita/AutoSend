package com.example.android.autosend.Services;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

import com.example.android.autosend.receiver.AlarmReceiver;
import com.example.android.autosend.data.Alarm;

/**
 * Created by Ishmita on 11-01-2017.
 */
public class AlarmService {

    private static final String TAG = "AlarmService";
    @TargetApi(19)
    public void setAlarm(Context context, Alarm alarm) {
        String dateParts[] = alarm.getDate().split(" ");
        int date[] = {
                Integer.parseInt(dateParts[0]),
                Integer.parseInt(dateParts[1]),
                Integer.parseInt(dateParts[2]),
                Integer.parseInt(dateParts[3]),
                Integer.parseInt(dateParts[4])
        };
        Calendar calendar = Calendar.getInstance();
        calendar.set(date[0], date[1], date[2], date[3], date[4],0);
        Log.d(TAG, "year: " + calendar.get(Calendar.YEAR) +" month: "+calendar.get(Calendar.MONTH)+
                " day: "+calendar.get(Calendar.DAY_OF_MONTH)+
                " hour: "+calendar.get(Calendar.HOUR)+
                " min: "+calendar.get(Calendar.MINUTE)+
                " sec: "+calendar.get(Calendar.SECOND));

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), getPendingIntent(context, alarm.getId()));
    }

    public PendingIntent getPendingIntent(Context context, int id) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction("MY_ALARM_TRIGGERED");
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra("id",id);
        Log.d(TAG, "id: "+id);
        return PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
