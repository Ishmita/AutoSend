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

    public Calendar getCalendarDate(Alarm alarm) {
        String dateParts[] = alarm.getDate().split(" ");
        int date[] = {
                Integer.parseInt(dateParts[0]),
                Integer.parseInt(dateParts[1])-1,
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
        return calendar;
    }
    @TargetApi(19)
    public void setAlarm(Context context, Alarm alarm) {

        Calendar calendar = getCalendarDate(alarm);

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

    //delete an alarm
    public void deleteAlarm(Context context, int id) {
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(getPendingIntent(context, id));
    }

    public void setRepeatingAlarm(Context context , Alarm alarm) {
        //Calendar newDate = (Calendar) getDateForRepeat(alarm).clone();
        int repeatType;
        String parts[] = alarm.getDate().split(" ");
        Integer date = Integer.parseInt(parts[0]);
        Integer year = null;
        if(parts[2].endsWith(",")){
            year = Integer.parseInt(parts[2].substring(0, parts[2].length()-1));
        } else {
            year = Integer.parseInt(parts[2]);
        }
        Integer month = Integer.parseInt(getMonth(parts[1]))-1;
        String timeParts[] = parts[3].split(":");
        Integer min = Integer.parseInt(timeParts[1]);
        Integer hour = null;
        if (parts.length == 5) {
            hour = Integer.parseInt(getHour(timeParts[0], parts[4]));
        } else {
            hour = Integer.parseInt(getHour(timeParts[0], null));
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date, hour, min, 0);

        Calendar now = Calendar.getInstance();
        if (calendar.compareTo(now)<=0) {
            switch (alarm.getRepeatType()) {
                case 1:
                    repeatType = Calendar.HOUR_OF_DAY;
                    calendar.set(year, month, date, hour + 1, min, 0);
                    hour++;
                    break;
                case 2:
                    repeatType = Calendar.DAY_OF_YEAR;
                    calendar.set(year, month, date + 1, hour, min, 0);
                    date++;
                    break;
                case 3:
                    repeatType = Calendar.MONTH;
                    calendar.set(year, month + 1, date, hour, min, 0);
                    month++;
                    break;
                case 4:
                    repeatType = Calendar.YEAR;
                    calendar.set(year + 1, month, date, hour, min, 0);
                    year++;
                    break;
                default:
                    repeatType = Calendar.DAY_OF_YEAR;
                    calendar.set(year, month, date, hour, min, 0);
            }
        }

        //newDate.add(repeatType, 0);
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                getPendingIntent(context, alarm.getId()));

        String dateString = "" + year + " " + (month+1) + " " + date + " " +
                hour + " " + min;

        DatabaseHandler databaseHandler = new DatabaseHandler(context);
        alarm.setDate(DatabaseHandler.formatDateTime(context, dateString));
        databaseHandler.editAlarm(alarm);
        Log.d(TAG , "new date: "+alarm.getDate());

    }

    //public Calendar getDateForRepeat(Alarm alarm) {

    //    return calendar;
    //}

    public String getMonth(String month) {
            if(month.equals("Jan"))
                return "01";
            else if(month.equals("Feb"))
                return "02";
            else if(month.equals("Mar"))
                return "03";
            else if(month.equals("Apr"))
                return "04";
            else if(month.equals("May"))
                return "05";
            else if(month.equals("Jun"))
                return "06";
            else if(month.equals("Jul"))
                return "07";
            else if(month.equals("Aug"))
                return "08";
            else if(month.equals("Sep"))
                return "09";
            else if(month.equals("Oct"))
                return "10";
            else if(month.equals("Nov"))
                return "11";
            else if(month.equals("Dec"))
                return "12";
        return null;
    }

    public String getHour(String hour, String amPm) {
        if (amPm == null || amPm.equals("am")) {
            return hour;
        }else if(amPm.equals("pm")) {
            if(hour.equals("1"))
                return "13";
            else if(hour.equals("2"))
                return "14";
            else if(hour.equals("3"))
                return "15";
            else if (hour.equals("4"))
                return "16";
            else if(hour.equals("5"))
                return "17";
            else if(hour.equals("6"))
                return "18";
            else if(hour.equals("7"))
                return "19";
            else if(hour.equals("8"))
                return "20";
            else if(hour.equals("9"))
                return "21";
            else if(hour.equals("10"))
                return "22";
            else if(hour.equals("11"))
                return "23";
            else if(hour.equals("12"))
                return "24";
        }
        return null;
    }
}
