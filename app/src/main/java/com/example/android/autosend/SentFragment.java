package com.example.android.autosend;


import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.autosend.Services.AlarmService;
import com.example.android.autosend.Services.DatabaseHandler;
import com.example.android.autosend.adapter.AlarmsAdapter;
import com.example.android.autosend.data.Alarm;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SentFragment extends Fragment implements MainActivity.Updateable {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "SentFragment";
    ListView done, toDo;
    ArrayList<Alarm> allAlarms, doneAlarms, toDoAlarms;
    DatabaseHandler databaseHandler;
    AlarmsAdapter doneAdapter, toDoAdapter;
    TextView sent, toSend;
    int position;
    Alarm alarm;

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
        done.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                position = i;
                alarm = doneAlarms.get(position);
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
                        databaseHandler.deleteAlarm(alarm);
                        doneAlarms.remove(position);
                        doneAdapter.notifyDataSetChanged();
                    }
                });
                builder.show();
                return true;
            }
        });

        done.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Alarm alarm = doneAlarms.get(i);
                Uri myPhotoUri = null;
                InputStream photo_stream = null;
                Bitmap my_btmp = null;
                Dialog messageDetailsDialog = new Dialog(getContext());
                messageDetailsDialog.setTitle(alarm.getAlarmTitle());
                messageDetailsDialog.setContentView(R.layout.message_details);
                messageDetailsDialog.setCancelable(true);
                CircleImageView imageView = (CircleImageView) messageDetailsDialog.findViewById(R.id.message_contact_photo);
                TextView name = (TextView) messageDetailsDialog.findViewById(R.id.messaged_contact_name);
                TextView date = (TextView)messageDetailsDialog.findViewById(R.id.date);
                TextView message = (TextView) messageDetailsDialog.findViewById(R.id.message_text_view);
                if(doneAlarms.get(i).getContactPhotoURI()!=null) {
                    myPhotoUri = Uri.parse(doneAlarms.get(i).getContactPhotoURI());
                    photo_stream = ContactsContract.Contacts.openContactPhotoInputStream(getContext().getContentResolver(), myPhotoUri);
                    Log.d(TAG, "" + photo_stream);
                    BufferedInputStream buf = new BufferedInputStream(photo_stream);
                    my_btmp = BitmapFactory.decodeStream(buf);
                }

                if(photo_stream!=null) {
                    imageView.setImageBitmap(my_btmp);
                }else{
                    imageView.setImageResource(R.drawable.contact);
                }
                name.setText(doneAlarms.get(i).getContactName());
                date.setText(doneAlarms.get(i).getDate().substring(0,11)+
                        "  "+doneAlarms.get(i).getDate().substring(11,19));
                message.setText("\n"+doneAlarms.get(i).getMessage());
                Window window = messageDetailsDialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                messageDetailsDialog.show();
            }
        });
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

    @Override
    public void update() {
        prepareLists();
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
