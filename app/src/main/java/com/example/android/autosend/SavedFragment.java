package com.example.android.autosend;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.autosend.Services.AlarmService;
import com.example.android.autosend.Services.DatabaseHandler;
import com.example.android.autosend.adapter.AlarmsAdapter;
import com.example.android.autosend.data.Alarm;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SavedFragment extends Fragment implements MainActivity.Updateable{


    private static final String ARGS_SECTION_NUMBER = "section_number";
    private static final String TAG = "SavedFragment";
    ListView savedAlarmsListView;
    Alarm alarm;
    ArrayList<Alarm> savedAlarms;
    AlarmsAdapter adapter;
    int position;
    DatabaseHandler databaseHandler;

    public SavedFragment() {
        // Required empty public constructor
    }

    public static SavedFragment newInstance(int sectionNumber) {
        SavedFragment savedFragment = new SavedFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_SECTION_NUMBER, sectionNumber);
        savedFragment.setArguments(args);
        return savedFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_sent, container, false);
        savedAlarmsListView = (ListView)view.findViewById(R.id.alarms_done_list_view);
        //savedTextView = (TextView)view.findViewById(R.id.sent_msgs_tag);
        //savedTextView.setText("Saved");
        databaseHandler = new DatabaseHandler(getContext());
        savedAlarms = databaseHandler.getAllAlarms();

        adapter = new AlarmsAdapter(getContext(), savedAlarms, R.layout.alarm_list_item);
        savedAlarmsListView.setAdapter(adapter);
        savedAlarmsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                position = i;
                alarm = savedAlarms.get(position);
                //show alert dialog for surity of action.
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure you want to delete?\n");
                builder.setTitle("Alert");
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlarmService alarmService = new AlarmService();
                        alarmService.deleteAlarm(getContext(), alarm.getId());
                        databaseHandler.deleteAlarm(alarm);
                        savedAlarms.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.show();
                return false;
            }
        });
        return view;
    }

    @Override
    public void update() {
        Log.d(TAG, "update of SavedFragment called!");
        savedAlarms.clear();
        savedAlarms.addAll(databaseHandler.getAllAlarms());
        adapter.notifyDataSetChanged();
        //adapter = new AlarmsAdapter(getContext(), savedAlarms, R.layout.alarm_list_item);
        //savedAlarmsListView.setAdapter(adapter);

    }
}
