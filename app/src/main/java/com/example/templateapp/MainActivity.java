package com.example.templateapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private long timeEpoch = 0;

    private static final String ERROR_API = "error calling quotes api";

    private String url = "https://type.fit/api/quotes";

    List<Quote> quotes;

    public MainActivity() {

    }

    public void playSoundClick() {
        Log.d("SOUND", "This is the function");
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.click_sound);
        mediaPlayer.start();
    }

    public void playSoundLongClick() {
        Log.d("SOUND", "This is the long click function");
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.click_long);
        mediaPlayer.start();
    }

    public void playWarningEffect() {
        Log.d("SOUND", "Play warning effect");
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.warning_sound);
        mediaPlayer.start();
    }

    private void vibrate(int time){
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (vibrator.hasVibrator()){
            vibrator.vibrate(time);
        }

    }

    private void setRandomQuote(){
        int randomQuote = Helpers.getRandomNumber(0, quotes.size());

        Quote selectedQuote = quotes.get(randomQuote);

        TextView quoteText = (TextView) findViewById(R.id.quoteText);
        TextView quoteAuthor = (TextView) findViewById(R.id.quoteAuthor);

        quoteText.setText(selectedQuote.text);
        quoteAuthor.setText(selectedQuote.author);
    }

    private void quotesRequest(){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
                public void onResponse(JSONArray response) {

                String text;
                String author;

                quotes = new ArrayList<>();

                for (int i=0 ; i < response.length() ; i++){
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        text = obj.getString("text");
                        author = obj.getString("author");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    Quote quote = new Quote();
                    quote.author = author;
                    quote.text = text;

                    quotes.add(quote);
                }

                setRandomQuote();

            }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("REST", ERROR_API);
                }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);

    }

    private void timeOperation() throws InterruptedException {
        while (true){

            if (this.timeEpoch == 0){
                TimeUnit.SECONDS.sleep(1);
                continue;
            }

            long currentTime = System.currentTimeMillis();
            long total = currentTime - timeEpoch;
            if (total >= 30000){
                playWarningEffect();
                TextView editText = (TextView) findViewById(R.id.counterTextLabel);
                vibrate(50);
                int currentClicks = ClickActions.decreaseClicks(editText, 1);

                // When the clicks is 0 stop click and vibration.
                if (currentClicks == 0){
                    this.timeEpoch = 0;
                }
            }

            TimeUnit.SECONDS.sleep(1);
            Log.d("THREAD", "Time calculation thread %s".format(Long.toString(total)));
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView editText = (TextView) findViewById(R.id.counterTextLabel);
        editText.setText("0");

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        this.quotesRequest();

        Thread timeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    timeOperation();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        timeThread.start();

        Button button = (Button) findViewById(R.id.teste);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                timeEpoch = System.currentTimeMillis();
                Log.d("BUTTONS", "Button teste pressed.");
                TextView editText = (TextView) findViewById(R.id.counterTextLabel);
                ClickActions.increaseClicks(editText, 1);
                vibrate(100);
                playSoundClick();
                setRandomQuote();
            }
        });
        button.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                timeEpoch = System.currentTimeMillis();
                playSoundLongClick();
                Log.d("BUTTONS", "Button long click teste pressed.");
                TextView editText = (TextView) findViewById(R.id.counterTextLabel);
                vibrate(500);
                ClickActions.increaseClicks(editText, 15);
                setRandomQuote();
                return true;
            }
        });

    }

}