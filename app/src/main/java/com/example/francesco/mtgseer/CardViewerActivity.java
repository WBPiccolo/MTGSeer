package com.example.francesco.mtgseer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class CardViewerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardviewer);
        Intent intent = getIntent();
        String name = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        Log.d("CardViewerActivity","CardViewerActivity created!");
        TextView cardname=(TextView)findViewById(R.id.textView);
        cardname.setText(name);
        //TODO: Print card's info
    }
}
