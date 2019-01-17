package com.example.francesco.mtgseer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView cardNameTextView = (TextView) findViewById(R.id.cardNameTextView);
        final TextView setDataTextView=(TextView) findViewById(R.id.setDataTextView);
        setDataTextView.setMovementMethod(new ScrollingMovementMethod());
        final Context context=getApplicationContext();
        /// AUTOCOMPLETE TEXTVIEW https://www.tutorialspoint.com/android/android_auto_complete.htm
        final AutoCompleteTextView actv;
        actv = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
        String[] languages={"Android ","java","IOS","SQL","JDBC","Web services"};
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,languages);
        actv.setAdapter(adapter);
        actv.setThreshold(1);
        ////
        //if Cardname.txt doesn't exit, create it and populate it.
        //Fetch the card list
        /*File file = context.getFileStreamPath(filename);
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
                    mTextView.setText("File writing didn't work!");
                    Log.d("URLResponse", "File writing didn't work!");
                }
            });
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
            Log.d("URLResponseQueue",queue.toString());
        }//if
        Log.d("URLResponseFile","Reading the file!");
        ArrayList<String> cardsArrayList=new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                cardsArrayList.add(line+"\n");
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }
        //mTextView.setText(text.toString());
        mTextView.setText(cardsArrayList.toString().substring(1,cardsArrayList.toString().length()-1).replace(",",""));
        Log.d("URLResponsePrin","Printed");*/
        //SEARCH MODULE
        //TODO: search in the arraylist, then show all the results, for each expansion.
        //https://scryfall.com/search?q=cardname
        //Search for a card, returns the name of the card, the most recent expansion and the price
        final Button button = (Button) findViewById(R.id.button);
        final EditText query=(EditText) findViewById(R.id.editTextSearch);
        //TODO: Visualizzare la lista di carte che rispettano i requisiti, se ce n'è solo una, caricarla direttamente
        //https://api.scryfall.com/cards/search?q=
        button.setOnClickListener(new View.OnClickListener() {
                                      public void onClick(View v) {
                                          Log.d("URLResponse", "Search for \"" + query.getText().toString() + "\"");
                                          if (!(query.getText().toString().isEmpty())) {
                                              String cardName = query.getText().toString();
                                              Log.d("URLResponseCardInput", cardName);
                                              //Search for the card
                                              RequestQueue queue = Volley.newRequestQueue(context);
                                              String url = "https://api.scryfall.com/cards/search?q="+query.getText().toString().replace(" ","+");
                                              // Request a string response from the provided URL.
                                              StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                                      new Response.Listener<String>() {
                                                          @Override
                                                          public void onResponse(String response) {
                                                              try {
                                                                  JSONObject reader = new JSONObject(response);
                                                                  int total_cards = Integer.parseInt(reader.getString("total_cards"));
                                                                  Log.d("URLResponse","Carte trovate:"+ total_cards);
                                                                  JSONArray data = (JSONArray) reader.get("data");
                                                                  //TODO: SISTEMARE QUI, BISOGNA FARE UNA CARDVIEW PER OGNI RISULTATO
                                                                  //TODO: IF total_cards==1, vai direttamente all'activity
                                                                  if(total_cards==1){
                                                                      //START NEW ACTIVITY
                                                                      Intent intent=new Intent(getApplicationContext(),CardViewerActivity.class);
                                                                      String cardID =data.getJSONObject(0).getString("id");
                                                                      Log.d("URLResponseID",cardID);
                                                                      intent.putExtra(EXTRA_MESSAGE, cardID);
                                                                      startActivity(intent);
                                                                  }//TODO: METTERE UN ELSE
                                                                  else {
                                                                      String cards = "";
                                                                      for (int i = 0; i < total_cards; i++) {
                                                                          //data.getJSONObject(i);
                                                                          try {
                                                                              cards += data.getJSONObject(i).getString("name") + "\n";
                                                                              Log.d("URLResponseCardName", data.getJSONObject(i).getString("name"));
                                                                          } catch (JSONException e) {
                                                                              cards += data.getJSONObject(i).getString("set_name") + " : " + "Prezzo non disponibile" + "\n";
                                                                              Log.d("URLResponseError", e.toString());
                                                                          }//catch
                                                                      }//for
                                                                      setDataTextView.setText(cards);
                                                                  }//else
                                                              } catch (JSONException e) {
                                                                  Log.d("URLResponse", e.toString());
                                                              }
                                                          }
                                                      }, new Response.ErrorListener() {
                                                  @Override
                                                  public void onErrorResponse(VolleyError error) {
                                                      Log.d("URLResponse", "That didn't work!");
                                                  }
                                              });
                                              // Add the request to the RequestQueue.
                                              queue.add(stringRequest);
                                          }//if
                                      }//onClick
                                  });
        /*button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("URLResponse","Search for \""+query.getText().toString()+"\"");
                if(!(query.getText().toString().isEmpty())){
                    /*String cardName=query.getText().toString(); //TODO: Completare il nome della carta!
                    Log.d("URLResponseCardInput",cardName);
                    String cardURI=cardNameToUri(context,cardName);
                    Log.d("URLResponseURIFunction",cardURI);
                   // ArrayList<String> rows=uriToPrintings(context,cardURI);
                    mTextView.setText(cardName+"; "+cardURI);*/
                   /*
                   //Search for the card
                    RequestQueue queue = Volley.newRequestQueue(context);
                    String url = "https://api.scryfall.com/cards/named?fuzzy="+query.getText().toString().replace(" ","+");
                    // Request a string response from the provided URL.
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject reader = new JSONObject(response);
                                        String name = reader.getString("name");
                                        String versionsURI = reader.getString("prints_search_uri");
                                        //TODO: Edit here
                                        RequestQueue queue = Volley.newRequestQueue(context);
                                        if(!(versionsURI.isEmpty())){
                                            RequestQueue queueURI = Volley.newRequestQueue(context);
                                            String url = versionsURI;
                                            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                                    new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            try {
                                                                JSONObject reader = new JSONObject(response);
                                                                int cardsNumber = Integer.parseInt(reader.getString("total_cards"));
                                                                Log.d("URLResponse","Edizioni trovate:"+ cardsNumber);
                                                                JSONArray data = (JSONArray) reader.get("data");
                                                                String setData="";
                                                                for(int i=0;i<cardsNumber;i++){
                                                                    //data.getJSONObject(i);
                                                                    try {
                                                                        setData += data.getJSONObject(i).getString("set_name") + " : " + data.getJSONObject(i).getString("eur") + "€" + "\n";
                                                                    }catch(JSONException e){
                                                                        setData += data.getJSONObject(i).getString("set_name") + " : " + "Prezzo non disponibile" + "\n";
                                                                        Log.d("URLResponseError",e.toString());
                                                                    }
                                                                }
                                                                Log.d("URLResponseJSONObject",setData);
                                                                setDataTextView.setText(setData);
                                                                RequestQueue queue = Volley.newRequestQueue(context);
                                                            } catch (JSONException e) {
                                                                Log.d("URLResponse", e.toString());
                                                            }
                                                        }
                                                    }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    Log.d("URLResponse", "cardsNumber didn't work!");
                                                }
                                            });
                                            // Add the request to the RequestQueue.
                                            queueURI.add(stringRequest);
                                        }//if
                                        Log.d("URLResponseJson",name+" , "+versionsURI);
                                        cardNameTextView.setText(name);
                                    } catch (JSONException e) {
                                        Log.d("URLResponse", e.toString());
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("URLResponse", "That didn't work!");
                        }
                    });
                    // Add the request to the RequestQueue.
                    queue.add(stringRequest);
                }//if
            }//onClick
        });*/
    }//onCreate

    /**
     * Method that returns an scryfall uri "prints_search_uri" to find the other printings
     * @param context the context
     * @param cardName the name of the card
     * @return a string containing the uri
     */
    private String cardNameToUri(final Context context, String cardName){
        final String[] cardUri = {""};
        RequestQueue queue = Volley.newRequestQueue(context);//TODO: il context è giusto?
        String url = "https://api.scryfall.com/cards/named?fuzzy="+cardName.replace(" ","+");
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
                            RequestQueue queue = Volley.newRequestQueue(context);
                            //TODO: Il problema è qua, non ritorna l'URI
                            cardUri[0] = versionsURI;
                            Log.d("URLResponseJson",name+" , "+versionsURI+"; "+cardUri[0]);
                        } catch (JSONException e) {
                            Log.d("URLResponse", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("URLResponse", "cardNameToUri didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        Log.d("URLResponseJsonURI2",cardUri[0]);
        return cardUri[0];
    }//cardNameToUri

    /**
     * Method that returns an array containing a string wich is the concatenation of: card set and card price in euros.
     * @param context the context
     * @param cardUri the uri of the card
     * @return an array of strings containing information about the other printings
     */
    private ArrayList<String> uriToPrintings(final Context context, final String cardUri){
        ArrayList<String> rows=new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(context);//TODO: il context è giusto?
        String url = cardUri;
        // Request a string response from the provided URL.
        Log.d("URLResponseURI","Sto provando ");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject reader = new JSONObject(response);
                            String totalCards = reader.getString("total_cards");
                            Log.d("URLResponseURItoPrint","Trovate "+totalCards+" carte");
                            String versionsURI = reader.getString("prints_search_uri");
                            //TODO: Edit here
                            RequestQueue queue = Volley.newRequestQueue(context);//TODO: il context è giusto?

                            //Log.d("URLResponseJson",name+" , "+versionsURI);
                        } catch (JSONException e) {
                            Log.d("URLResponse", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("URLResponse", "uriToPrintings didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        return rows;
    }//uriToPrintings
}//MainActivity
