package com.example.android.autosend.adapter;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.autosend.R;
import com.example.android.autosend.TypeMessage;
import com.example.android.autosend.data.Contact;
import com.example.android.autosend.data.CreateEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Ishmita on 28-12-2016.
 */
public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.MyViewHolder> {

    private Context mContext;
    List<CreateEntry> createEntryList;
    ArrayList<Contact> alContacts;
    ArrayList<Contact> selectedContacts = new ArrayList<>();
    boolean newSelection;
    private final static String TAG = "cardsAdapter";

    public CardsAdapter(Context mContext, List<CreateEntry> createEntryList){
        this.mContext = mContext;
        this.createEntryList = createEntryList;
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
                    mContext.startActivity(msgIntent);
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

}
