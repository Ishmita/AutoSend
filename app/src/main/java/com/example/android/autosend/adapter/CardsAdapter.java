package com.example.android.autosend.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.example.android.autosend.CreateFragment;
import com.example.android.autosend.Services.AlarmService;
import com.example.android.autosend.Services.DatabaseHandler;
import com.example.android.autosend.R;
import com.example.android.autosend.TypeMessage;
import com.example.android.autosend.data.Alarm;
import com.example.android.autosend.data.Contact;
import com.example.android.autosend.data.CreateEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Ishmita on 28-12-2016.
 */

//CHECKS NEED TO BE ADDED FOR VALUES LEFT NULL BY USER WHILE SAVING.
public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.MyViewHolder>  {

    private Context mContext;
    List<CreateEntry> createEntryList;
    ArrayList<Contact> alContacts;
    ArrayList<Contact> selectedContacts = new ArrayList<>();
    boolean newSelection;
    int count = 0, day=0, month=-1, year=0;
    Integer hour, minute;
    private final static String TAG = "cardsAdapter";
    private static final int REQUEST_CODE = 10;
    private String message = null;
    Calendar calendar;
    String title;

    public CardsAdapter(Context mContext, List<CreateEntry> createEntryList){
        this.mContext = mContext;
        this.createEntryList = createEntryList;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView heading;
        ImageView image;
        CardView cardView;
        Button actionButton;
        public MyViewHolder(View view){
            super(view);
            heading = (TextView) view.findViewById(R.id.heading);
            image = (ImageView) view.findViewById(R.id.card_image);
            cardView = (CardView) view.findViewById(R.id.card_view);
            actionButton = (Button)view.findViewById(R.id.action_button);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        CreateEntry createEntry = createEntryList.get(position);
        holder.heading.setText(createEntry.getHeading());

        holder.actionButton.setText(createEntry.getActionButton());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (position == 0) {
                    holder.actionButton.setVisibility(View.VISIBLE);
                    new FetchContactsTask().execute();
                }else if(position == 1){
                    Intent msgIntent = new Intent(mContext,TypeMessage.class);
                    //mContext.startActivity(msgIntent);
                    ((Activity)mContext).startActivityForResult(msgIntent, REQUEST_CODE);
                }else if(position == 2) {
                     selectDateTimeDialog();
                }else {
                    saveAlarm();
                }
            }
        });
        //set image.
        Glide.with(mContext).load(createEntry.getImage()).into(holder.image);
        holder.actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position==0) {
                    if (selectedContacts.size() > 0) {
                        showSelectedListDialog();
                    } else {
                        Toast.makeText(mContext, "No contact selected", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return createEntryList.size();
    }

    public void fetchContacts(){
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        ContentResolver contentResolver = mContext.getContentResolver();
        //Cursor cursor = contentResolver.query(uri, null, null, null, null, null);
        Cursor cursor = contentResolver.query(uri, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor.moveToFirst()) {
            alContacts = new ArrayList<Contact>();
            do {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                String name = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));
                Contact contact = new Contact();
                contact.setContactName(name);
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
                    while (pCur.moveToNext()) {
                        String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contact.setNumber(contactNumber);
                        alContacts.add(contact);
                        break;
                    }
                    pCur.close();
                }

            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    public void showContactDialog(){
        newSelection = true;
        final Dialog contactListDialog = new Dialog(mContext);
        contactListDialog.setContentView(R.layout.contact_list_dialog);
        contactListDialog.setCancelable(true);
        ListView contactListView = (ListView)contactListDialog.findViewById(R.id.contacts_list_view);
        final ContactsListAdapter adapter = new ContactsListAdapter(mContext, R.layout.contact, alContacts);
        Button ok = (Button)contactListDialog.findViewById(R.id.ok_button);
        contactListView.setAdapter(adapter);
        contactListView.setTextFilterEnabled(true);
        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayList<Contact> filteredList = adapter.getFilteredList();
                if(newSelection){
                    selectedContacts.clear();
                }
                selectedContacts.add(filteredList.get(i));
                Log.d(TAG, "position: " + (i+1) + "name: " + filteredList.get(i).getContactName());
                newSelection = false;
            }
        });

        SearchView searchView = (SearchView)contactListDialog.findViewById(R.id.searchView);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
        contactListDialog.setTitle("Select Contacts");
        Window window = contactListDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        contactListDialog.show();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactListDialog.dismiss();
            }
        });
    }

    public void showSelectedListDialog() {
        Dialog dialog = new Dialog(mContext);
        dialog.setTitle("Selected Contacts");
        dialog.setContentView(R.layout.selected_contact_list);
        ListView listView = (ListView)dialog.findViewById(R.id.selected_contacts_list_view);
        final ContactsListAdapter adapter = new ContactsListAdapter(mContext, R.layout.contact,selectedContacts);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedContacts.remove(i);
                adapter.notifyDataSetChanged();
                return false;
            }
        });
        dialog.show();
    }


    public class FetchContactsTask extends AsyncTask<Void, Integer, Void> {

        private ProgressBar spinner;
        Dialog progressBarDialog;

        @Override
        protected Void doInBackground(Void... params) {
            fetchContacts();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            showContactDialog();
            spinner.setVisibility(View.GONE);
            progressBarDialog.dismiss();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarDialog = new Dialog(mContext);
            progressBarDialog.setContentView(R.layout.progress_bar_dialog);
            progressBarDialog.setTitle("Loading contacts..");
            spinner = (ProgressBar)progressBarDialog.findViewById(R.id.progress_bar);
            progressBarDialog.setCancelable(false);
            Window window = progressBarDialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            progressBarDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            spinner.setProgress(values[0]);
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        if(data!=null) {
            message = data.getExtras().getString("msg");
        }
        Log.d(TAG, "received msg: " + message);
    }
    public void selectDateTimeDialog() {
        final Dialog dateTimeDialog = new Dialog(mContext);
        dateTimeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dateTimeDialog.setContentView(R.layout.date_time_picker_dialog);
        final ViewSwitcher viewSwitcher = (ViewSwitcher) dateTimeDialog.findViewById(R.id.view_switcher);
        final DatePicker datePicker = (DatePicker) dateTimeDialog.findViewById(R.id.date_picker);
        final TimePicker timePicker = (TimePicker) dateTimeDialog.findViewById(R.id.time_picker);
        Button okButton = (Button) dateTimeDialog.findViewById(R.id.ok_date_time_button);
        Window window = dateTimeDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dateTimeDialog.show();
        //dateTimeDialog.setTitle("Select Date");
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count == 0) {
                    //get selected date.
                    day = datePicker.getDayOfMonth();
                    //months start from 0 i.e. jan-0, feb-1..
                    month = datePicker.getMonth();
                    year = datePicker.getYear();
                    Log.d(TAG, "day: " + day + "month: " + month + "year: " + year);
                    viewSwitcher.showNext();
                    //dateTimeDialog.setTitle("Select Time");
                    count++;
                }else{
                    count = 0;
                    timePicker.clearFocus();
                    hour = timePicker.getCurrentHour();
                    minute = timePicker.getCurrentMinute();
                    Log.d(TAG, "hour: "+hour+"minute: "+minute);
                    //get selected time.
                    dateTimeDialog.dismiss();
                    calendar = Calendar.getInstance();
                    calendar.set(year, month, day, hour, minute, 0);
                }

            }
        });

    }

    public void saveAlarm() {
        DatabaseHandler databaseHandler = new DatabaseHandler(mContext);
        Log.d(TAG, "title: "+title);
        //Log.d(TAG, "rows deleted: "+databaseHandler.deleteAllAlarms());
        if(selectedContacts.size()!=0 && message!=null && year!=0 && month!=-1 && day!=0 && title!=null) {
            for (Contact contact : selectedContacts) {
                Alarm alarm = new Alarm();
                int id;
                alarm.setAlarmTitle(title);
                alarm.setContactName(contact.getContactName());
                alarm.setContactNumber(contact.getNumber());
                alarm.setMessage(message);
                alarm.setDate("" + year + " " + getMonth(month) + " " + getDay(day) + " " +
                        hour + " " + minute);
                alarm.setStatus(0);
                id = (int)databaseHandler.saveAlarm(alarm);
                alarm.setId(id);
                Calendar now = Calendar.getInstance();
                if (calendar.compareTo(now) <= 0) {
                    Toast.makeText(mContext, "Invalid date", Toast.LENGTH_LONG).show();
                } else {
                    //Setting the alarm for each alarm that is saved to DB
                    AlarmService alarmService = new AlarmService();
                    alarmService.setAlarm(mContext, alarm);
                }
            }
        }else {
            Toast.makeText(mContext, "Please specify all details", Toast.LENGTH_SHORT).show();
        }
        List<Alarm> alarms = databaseHandler.getAllAlarms();
        for(Alarm alarm1: alarms) {
            Log.d(TAG, "alarmId: " + alarm1.getId()+ " title: " + alarm1.getAlarmTitle()+
                    " contactName: "+alarm1.getContactName()+
                    " date: " + alarm1.getDate()+" msg: "+alarm1.getMessage()+
                    " number: "+alarm1.getContactNumber());
        }
    }

    public String getMonth(int month) {
        switch(month) {
            case 0:
                return "00";
            case 1:
                return "01";
            case 2:
                return "02";
            case 3:
                return "03";
            case 4:
                return "04";
            case 5:
                return "05";
            case 6:
                return "06";
            case 7:
                return "07";
            case 8:
                return "08";
            case 9:
                return "09";
            case 10:
                return "10";
            case 11:
                return "11";
        }
        return null;
    }


    public String getDay(int day) {
        switch (day){
            case 1:
               return "01";
            case 2:
                return "02";
            case 3:
                return "03";
            case 4:
                return "04";
            case 5:
                return "05";
            case 6:
                return "06";
            case 7:
                return "07";
            case 8:
                return "08";
            case 9:
                return "09";
            default:
                return ""+day;
        }
    }
}
