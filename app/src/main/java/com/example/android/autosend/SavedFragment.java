package com.example.android.autosend;


import android.os.Bundle;
import android.support.v4.app.Fragment;
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
public class SavedFragment extends Fragment {


    private static final String ARGS_SECTION_NUMBER = "section_number";
    ListView savedAlarmsListView;
    //TextView savedTextView;
    ArrayList<Alarm> savedAlarms;
    AlarmsAdapter adapter;
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
        DatabaseHandler databaseHandler = new DatabaseHandler(getContext());
        savedAlarms = databaseHandler.getAllAlarms();

        adapter = new AlarmsAdapter(getContext(), savedAlarms, R.layout.alarm_list_item);
        savedAlarmsListView.setAdapter(adapter);
        return view;
    }

}
