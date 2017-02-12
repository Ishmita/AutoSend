package com.example.android.autosend.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.example.android.autosend.MainActivity;
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

import it.sephiroth.android.library.tooltip.Tooltip;
import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

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
    int count = 0, day=0, month=-1, year=0,repeatType = 0;
    Integer hour, minute;
    private final static String TAG = "cardsAdapter";
    private static final int REQUEST_CODE = 10;
    private static final int EDIT_REQUEST_CODE = 2;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 101;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 201;
    private String message = null;
    Calendar calendar;
    String title;
    Activity activity;
    private Tooltip.TooltipView mCurrentTooltip;
    View view1 = null, view2 = null, view3 = null, view4 = null;
    View buttonView1, buttonView2, buttonView3, buttonVuew4;
    ScrollRecyclerView scrollRecyclerViewListener;

    public CardsAdapter(Context mContext, List<CreateEntry> createEntryList, ScrollRecyclerView scrollRecyclerViewListener){
        this.mContext = mContext;
        this.createEntryList = createEntryList;
        activity = (Activity)mContext;
        this.scrollRecyclerViewListener = scrollRecyclerViewListener;
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
        final MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        CreateEntry createEntry = createEntryList.get(position);
        holder.heading.setText(createEntry.getHeading());

        if(position == 0) {
            view1 = holder.image;
            buttonView1 = holder.actionButton;
        }
        else if (position == 1) {
            view2 = holder.image;
            buttonView2 = holder.actionButton;
        }
        else if (position ==2 ) {
            view3 = holder.image;
            buttonView3 = holder.actionButton;
        }
        else if(position ==3) {
            view4 = holder.image;
            buttonVuew4 = holder.actionButton;
        }
        new MaterialShowcaseView.Builder((Activity) mContext)
                .setTarget(view1)
                .setDismissText("GOT IT")
                .setContentText("Use this to select contacts")
                .withCircleShape()
                .singleUse("1")
                .setDelay(200)
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                    }

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {

                            new MaterialShowcaseView.Builder((Activity) mContext)
                                    .setTarget(buttonView1)
                                    .setDismissText("GOT IT")
                                    .setContentText("Click me to view and edit the list of selected contacts")
                                    .withRectangleShape()
                                    .singleUse("2")
                                    .setListener(new IShowcaseListener() {
                                        @Override
                                        public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                                        }

                                        @Override
                                        public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                                            writeNewMsg();
                                        }
                                    })
                                    .show();
                            Log.d(TAG, "dismissed");
                    }
                })
                .show();

        holder.actionButton.setText(createEntry.getActionButton());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (position == 0) {
                    holder.actionButton.setVisibility(View.VISIBLE);
                    if (ContextCompat.checkSelfPermission(mContext,
                            Manifest.permission.READ_CONTACTS)
                            != PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "taking permission");
                        ActivityCompat.requestPermissions((Activity) mContext,
                                new String[]{Manifest.permission.READ_CONTACTS},
                                MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                    }else {
                            new FetchContactsTask().execute();
                    }
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
                if(position == 0) {
                    if (selectedContacts.size() > 0) {
                        showSelectedListDialog();
                    } else {
                        Toast.makeText(mContext, "No contact selected", Toast.LENGTH_SHORT).show();
                    }
                }else if(position == 1) {
                    editMessage();
                }else if(position == 2) {
                    selectDateTimeDialog();
                }else if(position == 3) {
                    Toast.makeText(mContext, "Just save it and forget it!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return createEntryList.size();
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "permission granted!");
                    new FetchContactsTask().execute();
                }
            break;
            }
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "permission granted to send sms!");
                    saveAlarm();
                    break;
                }
            }
        }
    }

    public void fetchContacts(){
        Log.d(TAG, "in fetchContacts()");
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
        Log.d(TAG, "in showContactDialog");
        final Dialog contactListDialog = new Dialog(mContext, R.style.MyDialogTheme);
        contactListDialog.setContentView(R.layout.contact_list_dialog);
        contactListDialog.setTitle("Select Contacts");
        contactListDialog.setCancelable(true);
        final ListView contactListView = (ListView)contactListDialog.findViewById(R.id.contacts_list_view);
        final ContactsListAdapter adapter = new ContactsListAdapter(mContext, R.layout.contact, alContacts);
        Button ok = (Button)contactListDialog.findViewById(R.id.ok_button);
        contactListView.setAdapter(adapter);
        contactListView.setTextFilterEnabled(true);
        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SparseBooleanArray clickedItemPositions = contactListView.getCheckedItemPositions();
                ArrayList<Contact> filteredList = adapter.getFilteredList();

                // Set the TextView text
                //mTextView.setText("Checked items - ");

                for(int index=0;index<clickedItemPositions.size();index++) {
                    // Get the checked status of the current item
                    boolean checked = clickedItemPositions.valueAt(index);

                    if (checked) {
                        // If the current item is checked
                        if (newSelection) {
                            selectedContacts.clear();
                        }
                        int delIndex = selectedContacts.indexOf(filteredList.get(clickedItemPositions.keyAt(index)));
                        if(delIndex==-1) {
                            selectedContacts.add(filteredList.get(clickedItemPositions.keyAt(index)));
                        }
                        Log.d(TAG, "position: " + (clickedItemPositions.keyAt(index)) + "name: " + filteredList.get(clickedItemPositions.keyAt(index)).getContactName() +
                                " uri: " + filteredList.get(clickedItemPositions.keyAt(index)).getContactPhoto());
                        newSelection = false;
                        // /int key = clickedItemPositions.keyAt(index);
                        //String item = (String) contactListDialog.getItemAtPosition(key);
                    }else {
                        int delIndex = selectedContacts.indexOf(filteredList.get(clickedItemPositions.keyAt(index)));
                        if(delIndex!=-1) {
                            selectedContacts.remove(delIndex);
                        }
                    }
                }

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

        Window window = contactListDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        Log.d(TAG, "just before showing contacts dialog");
        contactListDialog.show();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactListDialog.dismiss();
            }
        });
    }

    public void showSelectedListDialog() {
        Dialog dialog = new Dialog(mContext, R.style.MyDialogTheme);
        dialog.setTitle("Selected Contacts");
        dialog.setContentView(R.layout.selected_contact_list);
        ListView listView = (ListView)dialog.findViewById(R.id.selected_contacts_list_view);
        final ContactsListAdapter adapter = new ContactsListAdapter(mContext, R.layout.contact,selectedContacts);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int position = i;
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Are you sure you want to remove?\n");
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
                        selectedContacts.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.show();
                return false;
            }
        });
        dialog.show();
    }

    public void editMessage() {
        if(message!=null && message.length()>0) {
            Intent intent = new Intent(mContext, TypeMessage.class);
            intent.putExtra("previousMsg", message);
            ((Activity)mContext).startActivityForResult(intent, EDIT_REQUEST_CODE);
        }else{
            Toast.makeText(mContext, "Please write a message first", Toast.LENGTH_SHORT).show();
        }
    }

    public class FetchContactsTask extends AsyncTask<Void, Integer, Void> {

        private ProgressBar spinner;
        Dialog progressBarDialog;

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG, "in doInBackground");
            fetchContacts();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(TAG, "in on PostExecute");
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
        final Spinner spinner = (Spinner) dateTimeDialog.findViewById(R.id.spinner_repeat);
        List<String> type = new ArrayList<String>();
        type.add("None");
        type.add("Hourly");
        type.add("Daily");
        type.add("Monthly");
        type.add("Yearly");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, type);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                repeatType = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                repeatType =0;
            }
        });
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
                    count++;
                }else if(count == 1){
                    timePicker.clearFocus();
                    hour = timePicker.getCurrentHour();
                    minute = timePicker.getCurrentMinute();
                    Log.d(TAG, "hour: "+hour+"minute: "+minute);
                    //get selected time.
                    calendar = Calendar.getInstance();
                    calendar.set(year, month, day, hour, minute, 0);
                    dateTimeDialog.dismiss();
                    count = 0;
                }
            }
        });

    }

    public void saveAlarm() {
        DatabaseHandler databaseHandler = new DatabaseHandler(mContext);
        Log.d(TAG, "title: "+title);
        if(selectedContacts.size()!=0 && message!=null && message.length()>0 && year!=0
                && month!=-1 && day!=0 && title!=null && title.length()>0) {

            if (ContextCompat.checkSelfPermission(mContext,
                    Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "taking permission");
                ActivityCompat.requestPermissions((Activity) mContext,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);

            } else {

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
                    alarm.setContactPhotoURI(contact.getContactPhoto());
                    alarm.setRepeatType(repeatType);

                    //Saving alarm in database
                    id = (int) databaseHandler.saveAlarm(alarm);
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
                Toast.makeText(mContext, "Saved", Toast.LENGTH_SHORT).show();

                //tell the viewPager to refresh pages.
                MainActivity.mViewPager.getAdapter().notifyDataSetChanged();

            }
        }else{
            Toast.makeText(mContext, "Please specify all details", Toast.LENGTH_SHORT).show();
        }
    }

    public String getMonth(int month) {
        switch(month) {
            case 0:
                return "01";
            case 1:
                return "02";
            case 2:
                return "03";
            case 3:
                return "04";
            case 4:
                return "05";
            case 5:
                return "06";
            case 6:
                return "07";
            case 7:
                return "08";
            case 8:
                return "09";
            case 9:
                return "10";
            case 10:
                return "11";
            case 11:
                return "12";
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

    public void writeNewMsg() {
        new MaterialShowcaseView.Builder((Activity) mContext)
                .setTarget(view2)
                .setDismissText("GOT IT")
                .setContentText("Write new message to send")
                .withCircleShape()
                .singleUse("3")
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                    }

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                        editMsg();
                    }
                })
                .show();
    }

    public void editMsg() {
        new MaterialShowcaseView.Builder((Activity) mContext)
                .setTarget(buttonView2)
                .setDismissText("GOT IT")
                .setContentText("Click me to Edit your Message content!")
                .withRectangleShape()
                .singleUse("4")
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                    }

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                        //scrollRecyclerViewListener.onScrollNeeded();
                        //selectDateTime();
                    }
                })
                .show();
    }

    public void selectDateTime() {
        new MaterialShowcaseView.Builder((Activity) mContext)
                .setTarget(view3)
                .setDismissText("GOT IT")
                .setContentText("Select Date and Time when you want to send your message to selected contacts!")
                .withCircleShape()
                .singleUse("5")
                .setDelay(200)
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                    }

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                        editDateTime();
                    }
                })
                .show();
    }

    public void editDateTime() {
        new MaterialShowcaseView.Builder((Activity) mContext)
                .setTarget(buttonView3)
                .setDismissText("GOT IT")
                .setContentText("Click here to set new Date and Time for your Message")
                .withRectangleShape()
                .singleUse("6")
                .setDelay(200)
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                    }

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                        saveInfo();
                    }
                })
                .show();
    }

    public void saveInfo() {
        new MaterialShowcaseView.Builder((Activity) mContext)
                .setTarget(view4)
                .setDismissText("GOT IT")
                .setContentText("Click me to save your created entry and your Message will be Sent to " +
                        "the Selected Contacts at the specified Time and you don't have to do anything for it!!")
                .withCircleShape()
                .singleUse("7")
                .setDelay(200)
                .show();
    }

    public interface ScrollRecyclerView {
        public void onScrollNeeded();
    }
}
