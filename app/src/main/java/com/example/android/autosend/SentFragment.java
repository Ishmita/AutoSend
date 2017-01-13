package com.example.android.autosend;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.autosend.Services.DatabaseHandler;
import com.example.android.autosend.adapter.AlarmsAdapter;
import com.example.android.autosend.data.Alarm;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SentFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "SentFragment";
    ListView done, toDo;
    ArrayList<Alarm> allAlarms, doneAlarms, toDoAlarms;
    DatabaseHandler databaseHandler;
    AlarmsAdapter doneAdapter, toDoAdapter;
    TextView sent, toSend;

    public SentFragment() {
        // Required empty public constructor
    }

    public static SentFragment newInstance(int sectionNumber) {
        SentFragment sentFragment = new SentFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        sentFragment.setArguments(args);
        return sentFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_sent, container, false);
        done = (ListView)view.findViewById(R.id.alarms_done_list_view);
        //toDo = (ListView)view.findViewById(R.id.alarms_to_do_list_view);
        //sent = (TextView)view.findViewById(R.id.sent_msgs_tag);
        //toSend = (TextView)view.findViewById(R.id.to_send_tag);
        databaseHandler = new DatabaseHandler(getContext());
        ArrayList<Alarm> alarms = databaseHandler.getAllAlarms();
        for(Alarm alarm1: alarms) {
            Log.d(TAG, "alarmId: " + alarm1.getId()+ " title: " + alarm1.getAlarmTitle()+
                    " contactName: "+alarm1.getContactName()+
                    " date: " + alarm1.getDate());
        }
        doneAlarms = new ArrayList<Alarm>();
        //toDoAlarms = new ArrayList<>();
        //allAlarms = new ArrayList<>();
        doneAdapter = new AlarmsAdapter(getContext(), doneAlarms, R.layout.alarm_list_item);
        //toDoAdapter = new AlarmsAdapter(getContext(), toDoAlarms, R.layout.alarm_list_item);
        done.setAdapter(doneAdapter);
        //toDo.setAdapter(toDoAdapter);
        prepareLists();
        return view;
    }

    public void prepareLists() {
        allAlarms = databaseHandler.getAllAlarms();
        for(Alarm a: allAlarms) {
            Log.d(TAG, "allAlarmsList item: "+a.getContactName());
            if(a.getStatus()==1) {
                doneAlarms.add(a);
                Log.d(TAG, "doneAlarmsList item: "+a.getContactName()+" date: "+a.getDate());
            }else {
                //toDoAlarms.add(a);
                Log.d(TAG, "toDoAlarmsList item: "+a.getContactName()+" date: "+a.getDate());
            }
            //setListViewHeightBasedOnChildren(done);
            doneAdapter.notifyDataSetChanged();
            //toDoAdapter.notifyDataSetChanged();
        }
        //if(doneAlarms.size()>0) {
        //    sent.setVisibility(View.VISIBLE);
        //}
        //if(toDoAlarms.size()>0) {
        //    toSend.setVisibility(View.VISIBLE);
        //}

    }
/*
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        AlarmsAdapter adapter = (AlarmsAdapter) listView.getAdapter();
        if (adapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }*/

}
