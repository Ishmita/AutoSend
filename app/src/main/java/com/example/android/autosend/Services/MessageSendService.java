package com.example.android.autosend.Services;

import android.app.IntentService;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

import com.example.android.autosend.data.Alarm;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Ishmita on 11-01-2017.
 */
public class MessageSendService extends IntentService {

    private static final String TAG = "MsgSendService";
    private Alarm alarm;
    int id;
    public MessageSendService(){
        super("MessageSendService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "message service started");
        DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
        id = intent.getIntExtra("id", -1);
        Log.d(TAG, "id: " + id);
        if (id != -1) {
            alarm = databaseHandler.getAlarm(id);
            if(alarm!=null) {
                Log.d(TAG, "Before update: "+"name: "+alarm.getContactName()+" message: "+alarm.getMessage()+
                        " number: "+alarm.getContactNumber()+" title: "+alarm.getAlarmTitle());
                //sendMessage();
                //for testing only, remove later
                updateStatus();
            }
        } else {
            ArrayList<Alarm> allAlarms = databaseHandler.getAllAlarms();
            for (Alarm a:allAlarms) {

                AlarmService alarmService = new AlarmService();
                String parts[] = a.getDate().split(" ");
                Integer date = Integer.parseInt(parts[0]);
                Integer year = Integer.parseInt(parts[2]);
                Integer month = Integer.parseInt(alarmService.getMonth(parts[1]))-1;
                String timeParts[] = parts[3].split(":");
                Integer min = Integer.parseInt(timeParts[1]);
                Integer hour = Integer.parseInt(alarmService.getHour(timeParts[0],parts[4]));

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, date, hour, min, 0);
                Calendar now = Calendar.getInstance();

                if(a.getRepeatType()!=0 && calendar.compareTo(now) <= 0) {
                    switch (a.getRepeatType()) {
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

                    String dateString = "" + calendar.get(Calendar.YEAR) + " " + (calendar.get(Calendar.MONTH)+1) + " "
                            + calendar.get(Calendar.DAY_OF_MONTH) + " " +
                            calendar.get(Calendar.HOUR) + " " + calendar.get(Calendar.MINUTE);

                    a.setDate(DatabaseHandler.formatDateTime(getApplicationContext(), dateString));
                    databaseHandler.editAlarm(a);
                    Log.d(TAG , "new repeat date: "+a.getDate());
                    alarmService.setAlarm(getApplicationContext(), a);
                }
            }
        }
    }

    private void sendMessage() {
        Log.d(TAG, "name: "+alarm.getContactName()+"message: "+alarm.getMessage()+
                "number: "+alarm.getContactNumber());
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(alarm.getContactNumber(), null, alarm.getMessage(), null, null);
        updateStatus();
    }

    private void updateStatus() {
        if(alarm.getRepeatType() == 0) {
            //status = 1 for triggered alarms
            alarm.setStatus(1);
        } else {
            alarm.setStatus(0);
        }
        DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
        databaseHandler.editAlarm(alarm);
        Alarm alarm1 = databaseHandler.getAlarm(id);
        Log.d(TAG, "After update: "+" title: "+alarm1.getAlarmTitle()+" name: "+alarm1.getContactName()+
                "message: "+alarm1.getMessage()+
                "number: "+alarm1.getContactNumber() +
                " date: "+alarm1.getDate());
        setRepeat();
    }

    private void setRepeat() {
        AlarmService alarmService = new AlarmService();
        Log.d(TAG, "repeatType:  "+alarm.getRepeatType());
        if (alarm.getRepeatType() != 0) {
            alarmService.setRepeatingAlarm(getApplicationContext(), alarm);
        }
    }

}
