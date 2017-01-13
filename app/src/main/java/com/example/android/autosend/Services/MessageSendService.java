package com.example.android.autosend.Services;

import android.app.IntentService;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

import com.example.android.autosend.data.Alarm;

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
        id = intent.getIntExtra("id", -1);
        Log.d(TAG, "id: " + id);
        if (id != -1) {
            DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
            alarm = databaseHandler.getAlarm(id);
            if(alarm!=null) {
                Log.d(TAG, "Before update: "+"name: "+alarm.getContactName()+" message: "+alarm.getMessage()+
                        " number: "+alarm.getContactNumber()+" title: "+alarm.getAlarmTitle());
                //sendMessage();
                //for testing only, remove later
                updateStatus();
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
        //status = 1 for triggered alarms
        alarm.setStatus(1);
        DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
        databaseHandler.editAlarm(alarm);
        Alarm alarm1 = databaseHandler.getAlarm(id);
        Log.d(TAG, "After update: "+" title: "+alarm1.getAlarmTitle()+" name: "+alarm1.getContactName()+
                "message: "+alarm1.getMessage()+
                "number: "+alarm1.getContactNumber() +
                " date: "+alarm1.getDate());
    }

}
