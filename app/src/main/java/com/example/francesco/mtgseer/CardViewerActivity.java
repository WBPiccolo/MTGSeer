package com.example.francesco.mtgseer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class CardViewerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context=getApplicationContext();
        setContentView(R.layout.activity_cardviewer);
        final TextView cardNameTW=(TextView)findViewById(R.id.CardNameTW);
        final TextView cardManaTW=(TextView)findViewById(R.id.ManaTW);
        final TextView cardTypeTW=(TextView)findViewById(R.id.CardTypeTW);
        final TextView cardEffectsTW=(TextView)findViewById(R.id.CardEffetcsTW);
        final TextView cardPrintingsTW=(TextView)findViewById(R.id.CardPrintingsTW);
        final ImageView cardImageView =(ImageView)findViewById(R.id.ImageView);
        Intent intent = getIntent();
        String cardID = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        //Given the cardID, the card's data are at "https://api.scryfall.com/cards/" + "cardID"
        String url="https://api.scryfall.com/cards/"+cardID;
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject reader = new JSONObject(response);
                            String cardName=reader.getString("name");
                            Log.d("URLcardName", cardName);
                            //TODO: Stampare le altre info della carta
                            //TODO: Controllare per i planeswalker
                            cardNameTW.setText(reader.getString("name"));
                            cardManaTW.setText(reader.getString("mana_cost"));
                            cardTypeTW.setText(reader.getString("type_line"));
                            //Set the image
                            String imageUri=reader.getJSONObject("image_uris").getString("art_crop");
                            Log.d("URLResponse",cardName);
                            Log.d("URLResponseImageUris",reader.getString("image_uris"));
                            Log.d("URLResponseImageNormal",reader.getJSONObject("image_uris").getString("art_crop"));
                            cardImageView.setImageURI(Uri.parse(imageUri));//TODO: SISTEMARLO

                            //TODO: Divide oracle text from flavour text, artist and P/T
                            String oracleText=reader.getString("oracle_text");
                            String flavorText=reader.getString("flavor_text");
                            String artist=reader.getString("artist");
                            String pt=reader.getString("power")+"/"+reader.getString("toughness");//TODO: Gestire l'eccezione se non è una creatura
                            cardEffectsTW.setText(oracleText+"\n\n"+flavorText+"\n"+artist+"  "+pt);

                            //PRINTINGS AND PRICING
                            final String printingsURI=reader.getString("prints_search_uri");
                            Log.d("URLResponsePrintings",printingsURI);
                            printPrintings(context,printingsURI);
                        }catch(JSONException e) {
                            Log.d("URLCardViewer", e.toString());
                        }//catch
                    }//onResponse
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("URLCardID",error.toString());
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
        //cardname.setText(name);
        //TODO: Print card's info
    }

    /**Method that prints the printings with pricing of the card
     *
     * @param printingsURI printings of the card
     */
    private void printPrintings(final Context context, String printingsURI) {
        final TextView cardPrintingsTW=(TextView)findViewById(R.id.CardPrintingsTW);
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url =printingsURI;
        Log.d("URLprintPrinintgs","PrinitngsUri: "+url);

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject reader = new JSONObject(response);
                            //String data = reader.getString("data");
                            // Display the first 500 characters of the response string.
                            //mTextView.setText("Response is: "+ response.substring(0,500));
                            int prints = Integer.parseInt(reader.getString("total_cards"));
                            JSONArray data = (JSONArray) reader.get("data");
                            String printingsData="";
                            for(int i=0;i<prints;i++){
                                try {
                                    printingsData += data.getJSONObject(i).getString("set_name") + " : " + data.getJSONObject(i).getString("eur") + "€" + "\n";
                                }catch(JSONException e){
                                    printingsData += data.getJSONObject(i).getString("set_name") + " : " + "Prezzo non disponibile" + "\n";
                                    Log.d("URLResponseError",e.toString());
                                }
                            }//for
                            Log.d("URLPrintprintings", printingsData);
                            cardPrintingsTW.setText(printingsData);
                        }catch(JSONException e){
                            Log.d("URLPrintpritingsError",e.toString());
                        }//catch
                    }//onResponse
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("URLPrintprintings",error.toString());
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }//printPrintings
}
