package com.example.francesco.mtgseer;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView mTextView = (TextView) findViewById(R.id.textViewResults);
        mTextView.setMovementMethod(new ScrollingMovementMethod());
        final Context context=getApplicationContext();
        String filename="cardname.txt";
        mTextView.setText("Caricamento...");
        ////
        //if Cardname.txt doesn't exit, create it and populate it.
        //Fetch the card list
        File file = context.getFileStreamPath(filename);
        if(file == null || !file.exists()) {
            //Create the file

        }else{
            //Read from file //Ci va l'else?
        }//else

        //SEARCH MODULE
        //https://scryfall.com/search?q=cardname
        //Search for a card, returns the name of the card, the most recent expansion and the price
        final Button button = (Button) findViewById(R.id.button);
        final EditText query=(EditText) findViewById(R.id.editTextSearch);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("URLResponse","click: "+query.getText().toString());
                if(!(query.getText().toString().isEmpty())){
                    //Search for the card
                    RequestQueue queue = Volley.newRequestQueue(context);//TODO: il context è giusto?
                    String url = "https://api.scryfall.com/cards/named?fuzzy="+query.getText().toString().replace(" ","+");
                    // Request a string response from the provided URL.
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    //mTextView.setText("Response is: "+ response.substring(0,500));
                                    //Log.d("URLResponse", response.substring(0,50));
                                    try {
                                        JSONObject reader = new JSONObject(response);
                                        String name = reader.getString("name");
                                        String price = reader.getString("eur");
                                        String set =reader.getString("set_name");
                                        /*s = s.substring(2);
                                        s = s.replace("\",\"", "\n");
                                        s = s.replace("\\\"", "\"");*/
                                        Log.d("URLResponseJson",name);
                                        mTextView.setText(name+", "+set+": "+price+"€");
                                    } catch (JSONException e) {
                                        Log.d("URLResponse", e.toString());
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mTextView.setText("That didn't work!");
                            Log.d("URLResponse", "That didn't work!");
                        }
                    });
                    // Add the request to the RequestQueue.
                    queue.add(stringRequest);
                }//if
            }//onClick
        });
    }//onCreate
}
