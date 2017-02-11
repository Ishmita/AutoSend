package com.example.android.autosend.Services;


import android.app.IntentService;
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
        Log.d(TAG, "in boot service");
        //Re-set all alarms.
        resetAlarms();

    }

    public void resetAlarms() {
        DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
        AlarmService alarmService = new AlarmService();
        ArrayList<Alarm> alarms = databaseHandler.getAllAlarms();
        for (Alarm alarm:alarms) {
            Calendar now = Calendar.getInstance();
            Log.d(TAG, "alarm title: "+alarm.getAlarmTitle()+
                    " status: "+alarm.getStatus()+
                    " compareTo(now): "+ getCalendar(alarm).compareTo(now));

            if(alarm.getRepeatType() == 0 && getCalendar(alarm).compareTo(now)<=0) {
                //single alarms that have already passed. Cannot do anything about them.
            } else if(alarm.getStatus() == 0 && getCalendar(alarm).compareTo(now)>0) {
                //alarms that will be triggered in future. Need to re-set them.
                alarmService.setRepeatingAlarm(getApplicationContext(), alarm);
            } else if(alarm.getRepeatType() != 0 && getCalendar(alarm).compareTo(now)<=0) {
                Log.d(TAG, "missed repeating alarm");
                //repeating missed alarm. Calculate next date and set repeating again.
                Calendar calendar = getCalendar(alarm);

                Integer min = calendar.get(Calendar.MINUTE);
                Integer hour = calendar.get(Calendar.HOUR_OF_DAY);
                Integer day = calendar.get(Calendar.DAY_OF_MONTH);
                Integer month = calendar.get(Calendar.MONTH);

                if (calendar.compareTo(now) <= 0) {
                    switch (alarm.getRepeatType()) {
                        case 1:

                            if(min.compareTo(now.get(Calendar.MINUTE))<=0) {
                                Calendar nextHourCalendar = alarmService.getNextHour(now);
                                calendar.set(Calendar.HOUR_OF_DAY, nextHourCalendar.get(Calendar.HOUR_OF_DAY));
                            }else {
                                calendar.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY));
                            }
                            break;
                        case 2:
                            if(hour.compareTo(now.get(Calendar.HOUR_OF_DAY))<0 ||
                                    (hour.compareTo(now.get(Calendar.HOUR_OF_DAY)) == 0 && min.compareTo(now.get(Calendar.MINUTE))<=0 )) {
                                        Calendar nextDayCalendar = alarmService.getNextDay(now);
                                        calendar.set(Calendar.DAY_OF_MONTH, nextDayCalendar.get(Calendar.DAY_OF_MONTH));
                                    } else {
                                        calendar.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
                                    }
                            break;
                        case 3:
                            if (day.compareTo(now.get(Calendar.DAY_OF_MONTH))<0 ||
                                    (day.compareTo(now.get(Calendar.DAY_OF_MONTH))==0 && hour.compareTo(now.get(Calendar.HOUR_OF_DAY))<0) ||
                                    (day.compareTo(now.get(Calendar.DAY_OF_MONTH))==0 && hour.compareTo(now.get(Calendar.HOUR_OF_DAY)) == 0 && min.compareTo(now.get(Calendar.MINUTE))<=0)) {
                                Calendar nextMonthCalendar = alarmService.getNextMonth(now);
                                calendar.set(Calendar.MONTH, nextMonthCalendar.get(Calendar.MONTH));
                            }else{
                                calendar.set(Calendar.MONTH, now.get(Calendar.MONTH));
                            }
                            break;
                        case 4:
                            if(month.compareTo(now.get(Calendar.MONTH))<0 ||
                                    (month.compareTo(now.get(Calendar.MONTH))==0 && day.compareTo(now.get(Calendar.DAY_OF_MONTH))<0) ||
                                    (month.compareTo(now.get(Calendar.MONTH))==0 && day.compareTo(now.get(Calendar.DAY_OF_MONTH))==0 && hour.compareTo(now.get(Calendar.HOUR_OF_DAY))<0) ||
                                    (month.compareTo(now.get(Calendar.MONTH))==0 && day.compareTo(now.get(Calendar.DAY_OF_MONTH))==0 && hour.compareTo(now.get(Calendar.HOUR_OF_DAY))==0 && min.compareTo(now.get(Calendar.MINUTE))<=0)) {
                                Calendar nextYearCalendar = alarmService.getNextYear(now);
                                calendar.set(Calendar.YEAR, nextYearCalendar.get(Calendar.YEAR));
                            }else {
                                calendar.set(Calendar.YEAR, now.get(Calendar.YEAR));
                            }
                            break;
                    }

                    String dateString = "" + calendar.get(Calendar.YEAR) + " " + (calendar.get(Calendar.MONTH)+1)
                            + " " + calendar.get(Calendar.DAY_OF_MONTH) + " " +
                            calendar.get(Calendar.HOUR_OF_DAY) + " " + calendar.get(Calendar.MINUTE);

                    Log.d(TAG, "dateString: "+ dateString);
                    alarm.setDate(DatabaseHandler.formatDateTime(getApplicationContext(), dateString));
                    databaseHandler.editAlarm(alarm);
                    Log.d(TAG , "new date: "+alarm.getDate());

                    alarmService.setRepeatingAlarm(getApplicationContext(), alarm);
                }
            }
        }
    }

    private Calendar getCalendar(Alarm alarm) {
        AlarmService alarmService = new AlarmService();
        String parts[] = alarm.getDate().split(" ");
        Integer date = Integer.parseInt(parts[0]);
        Integer year = null;
        if (parts[2].endsWith(",")) {
            year = Integer.parseInt(parts[2].substring(0, parts[2].length() - 1));
        } else {
            year = Integer.parseInt(parts[2]);
        }
        Integer month = Integer.parseInt(alarmService.getMonth(parts[1])) - 1;
        String timeParts[] = parts[3].split(":");
        Integer min = Integer.parseInt(timeParts[1]);
        Integer hour = null;

        if (parts.length == 5) {
            hour = Integer.parseInt(alarmService.getHour(timeParts[0], parts[4]));
            Log.d(TAG, "saved hour: " + timeParts[0] + " amPm: " + parts[4]);
        } else {
            hour = Integer.parseInt(alarmService.getHour(timeParts[0], null));
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date, hour, min, 0);
        return calendar;
    }
}
