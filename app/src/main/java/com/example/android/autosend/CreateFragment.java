package com.example.android.autosend;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.android.autosend.adapter.CardsAdapter;
import com.example.android.autosend.data.CreateEntry;

import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.tooltip.Tooltip;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateFragment extends Fragment implements MainActivity.Updateable {


    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "CreateFragment";
    private RecyclerView recyclerView;
    private CardsAdapter cardsAdapter;
    private List<CreateEntry> createEntryList;
    private EditText title;
    String titleString;

    public CreateFragment() {
        // Required empty public constructor
    }

    public static CreateFragment newInstance(int sectionNumber) {
        CreateFragment fragment = new CreateFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        title = (EditText)view.findViewById(R.id.title_edit_text);
        addTextWatcher();
        createEntryList = new ArrayList<>();
        final RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this.getContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        CardsAdapter.ScrollRecyclerView scrollRecyclerViewListener = new CardsAdapter.ScrollRecyclerView() {
            @Override
            public void onScrollNeeded() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (recyclerView != null) {
                            recyclerView.scrollToPosition(0);
                        }
                    }
                });
                Log.d(TAG, "onScrollNeeded called");
            }
        };
        cardsAdapter = new CardsAdapter(getContext(), createEntryList, scrollRecyclerViewListener);

        //recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cardsAdapter);

        prepareList();
        return view;
    }

    public void prepareList(){
    int image[] = {R.drawable.contact,
                    R.drawable.write,


                    R.drawable.cal,
                    R.drawable.app};
        String headings[] = {
                "Contact",
                "Message",
                "Date & Time",
                "Save"
        };

        String action[] = {
                "SHOW SELECTED",
                "EDIT",
                "CHANGE",
                "AUTO-SEND"
        };

        for(int i=0;i<4;i++) {
            CreateEntry createEntry = new CreateEntry();
            createEntry.setHeading(headings[i]);
            createEntry.setImage(image[i]);
            createEntry.setActionButton(action[i]);
            createEntryList.add(createEntry);
        }
        cardsAdapter.notifyDataSetChanged();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("CreateFragment", "onActivityResult");
        cardsAdapter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        cardsAdapter.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    public void addTextWatcher() {
        title.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                titleString = title.getText().toString();
                cardsAdapter.setTitle(titleString);
            }
        });
    }

    @Override
    public void update() {
        title.setText("");
        Log.d(TAG, "title should be cleared now in update()");
    }
}

