package com.example.android.autosend.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.android.autosend.R;
import com.example.android.autosend.data.Alarm;

import java.util.ArrayList;

/**
 * Created by Ishmita on 12-01-2017.
 */
public class AlarmsAdapter extends BaseAdapter {

    private static final String TAG = "AlarmsAdapter";
    private Context context;
    private ArrayList<Alarm> alarmArrayList = new ArrayList<>();
    private int resourceId;
    Alarm alarm;
    public AlarmsAdapter(Context context, ArrayList<Alarm> alarmArrayList, int resourceId) {
        this.context = context;
        this.alarmArrayList = alarmArrayList;
        this.resourceId = resourceId;
    }

    public class MyHolder {
        TextView title;
        TextView date;
    }
    @Override
    public int getCount() {
        return alarmArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        MyHolder myHolder;
        if(view!=null){
            myHolder = (MyHolder)view.getTag();
        }else{
            myHolder = new MyHolder();
            view = LayoutInflater.from(context).inflate(resourceId, viewGroup, false);
            myHolder.title = (TextView)view.findViewById(R.id.alarms_title);
            myHolder.date = (TextView)view.findViewById(R.id.alarm_date);
            view.setTag(myHolder);
        }

        alarm = alarmArrayList.get(i);
        Log.d(TAG, "list size: "+getCount());

        Log.d(TAG, "alarmId: " + alarm.getId()+" alarm date: " + alarm.getDate());

        myHolder.title.setText(alarm.getAlarmTitle());
        String dateString = alarm.getDate().substring(0,11);

        myHolder.date.setText(dateString);
        return view;
    }
}
