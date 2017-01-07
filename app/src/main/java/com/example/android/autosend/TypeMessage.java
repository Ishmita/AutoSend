package com.example.android.autosend;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

public class TypeMessage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_message);

        Toolbar toolbar = (Toolbar)findViewById(R.id.msg_toolbar);
        Button done = (Button) toolbar.findViewById(R.id.complete_msg);
        setSupportActionBar(toolbar);
        final Drawable upArrow = ContextCompat.getDrawable(getApplicationContext(), R.drawable.arrow);
        upArrow.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }
}
