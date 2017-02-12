package com.example.android.autosend.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.autosend.R;
import com.example.android.autosend.data.Contact;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Ishmita on 04-01-2017.
 */
public class ContactsListAdapter extends BaseAdapter implements Filterable{
    private Context mContext;
    private ArrayList<Contact> contactList = new ArrayList<>();
    private ArrayList<Contact> filteredList;
    private int resource;
    private ContactNameFilter contactNameFilter;
    private ArrayList<Contact> selectedContacts = new ArrayList<>();
    boolean newSelection;
    Contact contact;
    private final static String TAG = "contactListAdapter";

    public ContactsListAdapter(Context mContext, int resource ,ArrayList contactList){
        Log.d(TAG, "in contactListAdapter");
        this.mContext = mContext;
        this.contactList = contactList;
        this.resource = resource;
        this.filteredList = contactList;
        getFilter();
    }

    @Override
    public Filter getFilter() {
        if(contactNameFilter==null){
            return new ContactNameFilter();
        }
        return contactNameFilter;
    }

    public class MyHolder{
        ImageView contactPhoto;
        TextView contactName;
        LinearLayout contactLayout;
    }

    private class ContactNameFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults filterResults = new FilterResults();
            if(charSequence!=null && charSequence.length()>0){
                ArrayList<Contact> tempArray = new ArrayList<>();
                for(Contact c: contactList){
                    if(c.getContactName().toLowerCase().contains(charSequence.toString().toLowerCase())){
                        tempArray.add(c);
                    }
                }
                filterResults.count = tempArray.size();
                filterResults.values = tempArray;
            }else{
                filterResults.count = contactList.size();
                filterResults.values = contactList;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            filteredList = (ArrayList<Contact>)filterResults.values;
            notifyDataSetChanged();
        }
    }
    @Override
    public int getCount() {

        return filteredList.size();
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
        if(view!= null){
            myHolder = (MyHolder)view.getTag();
        }else {
            myHolder = new MyHolder();
            view = LayoutInflater.from(mContext).inflate(resource, viewGroup, false);
            myHolder.contactName = (TextView)view.findViewById(R.id.contact_name);
            myHolder.contactPhoto = (ImageView)view.findViewById(R.id.contact_photo);
            view.setTag(myHolder);
        }

        contact = filteredList.get(i);
        myHolder.contactName.setText(contact.getContactName());
        //load and set image for contacts
        Uri my_contact_Uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(getContactIDFromNumber(contactList.get(i).getNumber(), mContext)));
        filteredList.get(i).setContactPhoto(my_contact_Uri.toString());
        Log.d(TAG, "uri: "+my_contact_Uri + " saved uri in filteredList: "+filteredList.get(i).getContactPhoto());
        InputStream photo_stream = ContactsContract.Contacts.openContactPhotoInputStream(mContext.getContentResolver(), my_contact_Uri);
        Log.d(TAG, "" + photo_stream);
        BufferedInputStream buf = new BufferedInputStream(photo_stream);
        Bitmap my_btmp = BitmapFactory.decodeStream(buf);
        if(photo_stream!=null) {
            myHolder.contactPhoto.setImageBitmap(my_btmp);
        }else{
            myHolder.contactPhoto.setImageResource(R.drawable.contact);
        }

        return view;
    }

    public static long getContactIDFromNumber(String contactNumber, Context context) {
        String UriContactNumber = Uri.encode(contactNumber);
        long phoneContactID = new Random().nextInt();
        Cursor contactLookupCursor = context.getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, UriContactNumber),
                new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        while (contactLookupCursor.moveToNext()) {
            phoneContactID = contactLookupCursor.getLong(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
        }
        contactLookupCursor.close();

        return phoneContactID;
    }

    public ArrayList<Contact> getFilteredList() {
        return filteredList;
    }
}
