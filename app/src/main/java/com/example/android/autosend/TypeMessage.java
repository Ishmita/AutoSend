package com.example.android.autosend;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.autosend.adapter.CardsAdapter;

public class TypeMessage extends AppCompatActivity {

    Button done;
    EditText msgEditText;
    String message;
    @TargetApi(21)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_message);

        Toolbar toolbar = (Toolbar)findViewById(R.id.msg_toolbar);
        done = (Button) toolbar.findViewById(R.id.complete_msg);
        msgEditText = (EditText)findViewById(R.id.message);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.arrow);
        upArrow.setColorFilter(ContextCompat.getColor(this, android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        Window window = getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        //in-case the user selects to edit the message.
        Intent intent = getIntent();
        String msg = intent.getStringExtra("previousMsg");
        if(msg!=null && msg.length()>0) {
            msgEditText.setText(msg);
        }
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        message = msgEditText.getText().toString();
                        //Save message in database.
                        Intent intent = new Intent(TypeMessage.this, CardsAdapter.class);
                        intent.putExtra("msg", message);
                        setResult(102, intent);
                InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow((null == getCurrentFocus())
                        ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                finish();
            }
        });
    }
}
