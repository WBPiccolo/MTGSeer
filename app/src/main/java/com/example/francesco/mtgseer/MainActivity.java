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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView mTextView = (TextView) findViewById(R.id.textViewResults);
        mTextView.setMovementMethod(new ScrollingMovementMethod());
        final Context context=getApplicationContext();
        final String filename="cardname.txt";
        mTextView.setText("Caricamento...");
        ////
        //if Cardname.txt doesn't exit, create it and populate it.
        //Fetch the card list
        File file = context.getFileStreamPath(filename);
        if(file == null || !file.exists()) {
            //Create the file
            Log.d("URLResponse", "Creating the file");
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://api.scryfall.com/catalog/card-names";
            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //mTextView.setText("Response is: "+ response.substring(0,500));
                            //Log.d("URLResponse", response.substring(0,50));
                            try {
                                JSONObject reader = new JSONObject(response);
                                String s = reader.getString("data");
                                s = s.substring(2);
                                s = s.replace("\",\"", "\n");
                                s = s.replace("\\\"", "\"");
                                s = s.replace("\\/", "/");
                                Log.d("URLResponseJson", s.substring(0, 50));
                                //mTextView.setText(s);
                                FileOutputStream outputStream;
                                try {
                                    outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                                    outputStream.write(s.getBytes());
                                    outputStream.close();
                                    Log.d("URLResponseFile", "File written!");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
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
            Log.d("URLResponseQueue",queue.toString());
        }//if
        Log.d("URLResponseFile","Reading the file!");
        StringBuilder text = new StringBuilder();
        ArrayList<String> cardsArrayList=new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                cardsArrayList.add(line+"\n");
                //text.append(line);
                //text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }
        //mTextView.setText(text.toString());
        mTextView.setText(cardsArrayList.toString().substring(1,cardsArrayList.toString().length()-1).replace(",",""));
        Log.d("URLResponsePrin","Printed");
        //SEARCH MODULE
        //TODO: search in the arraylist, then show all the results, for each expansion.
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
                                        String versionsURI = reader.getString("prints_search_uri");
                                        //TODO: Edit here
                                        RequestQueue queue = Volley.newRequestQueue(context);//TODO: il context è giusto?
                                        String url = versionsURI;
                                        Log.d("URLResponseJson",name+" , "+versionsURI);
                                        mTextView.setText(name+"\n");
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
