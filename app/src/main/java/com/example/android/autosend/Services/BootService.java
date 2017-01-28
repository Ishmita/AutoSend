package com.example.android.autosend.Services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.android.autosend.data.Alarm;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Ishmita on 27-01-2017.
 */
public class BootService extends IntentService {

    private static final String TAG = "BootService";

    public BootService() {
        super("BootService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Re-set all alarms.
        resetAlarms();
    }

    public void resetAlarms() {
        DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
        AlarmService alarmService = new AlarmService();
        ArrayList<Alarm> alarms = databaseHandler.getAllAlarms();
        for (Alarm alarm:alarms) {
            Calendar now = Calendar.getInstance();
            if(alarm.getStatus() == 0 && getCalendar(alarm).compareTo(now)<=0) {
                //single alarms that have already passed. Cannot do anything about them.
            } else if(alarm.getStatus() == 0 && getCalendar(alarm).compareTo(now)>0) {
                //single alarms that will be triggered in future. Need to re-set them.
                alarmService.setRepeatingAlarm(getApplicationContext(), alarm);
            } else if(alarm.getRepeatType() != 0 && getCalendar(alarm).compareTo(now)<=0) {
                //repeating missed alarm. Calculate next date and set repeating again.
                Calendar calendar = getCalendar(alarm);
                Integer year = calendar.get(Calendar.YEAR);
                Integer month = calendar.get(Calendar.MONTH);
                Integer date = calendar.get(Calendar.DAY_OF_MONTH);
                Integer hour = calendar.get(Calendar.HOUR);
                Integer min = calendar.get(Calendar.MINUTE);

                switch (alarm.getRepeatType()) {
                    case 1:
                        while (calendar.compareTo(now) <= 0) {
                            calendar.set(year, month, date, hour + 1, min, 0);
                        }
                        break;
                    case 2:
                        while (calendar.compareTo(now) <= 0) {
                            calendar.set(year, month, date + 1, hour, min, 0);
                        }
                        break;
                    case 3:
                        while (calendar.compareTo(now) <= 0) {
                            calendar.set(year, month + 1, date, hour, min, 0);
                        }
                        break;
                    case 4:
                        while (calendar.compareTo(now) <= 0) {
                            calendar.set(year + 1, month, date, hour, min, 0);
                        }
                        break;
                    default:
                        calendar.set(year, month, date, hour, min, 0);
                }
                alarmService.setRepeatingAlarm(getApplicationContext(), alarm);
            }
        }
    }

    private Calendar getCalendar(Alarm alarm) {
        AlarmService alarmService = new AlarmService();
        String parts[] = alarm.getDate().split(" ");
        Integer date = Integer.parseInt(parts[0]);
        Integer year = Integer.parseInt(parts[2]);
        Integer month = Integer.parseInt(alarmService.getMonth(parts[1]))-1;
        String timeParts[] = parts[3].split(":");
        Integer min = Integer.parseInt(timeParts[1]);
        Integer hour = Integer.parseInt(alarmService.getHour(timeParts[0],parts[4]));

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date, hour, min, 0);
        return calendar;
    }
}
