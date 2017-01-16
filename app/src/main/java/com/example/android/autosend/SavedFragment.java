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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
        savedAlarmsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Alarm alarm = savedAlarms.get(i);
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
                if(savedAlarms.get(i).getContactPhotoURI()!=null) {
                    myPhotoUri = Uri.parse(savedAlarms.get(i).getContactPhotoURI());
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
                name.setText(savedAlarms.get(i).getContactName());
                date.setText(savedAlarms.get(i).getDate().substring(0,11)+
                        "  "+savedAlarms.get(i).getDate().substring(11,19));
                message.setText(savedAlarms.get(i).getMessage());
                Window window = messageDetailsDialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                messageDetailsDialog.show();
            }
        });

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
