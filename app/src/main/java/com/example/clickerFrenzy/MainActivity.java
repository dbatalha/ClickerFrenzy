package com.example.clickerFrenzy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
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

    private static final String TEST_CHANNEL = "test-channel";
    private static final int THRESHOLD = 1000;
    private static final String COLOR_GOLD = "#DAA520";
    private static final String TITLE = "Clicker Frenzy";
    private static final String CONGRATULATIONS_1000_CLICKS = "Congratulations you're reach %s";
    private long timeEpoch = 0;
    private static final String ERROR_API = "error calling quotes api";
    private int notificationStatus = 0;
    private MediaPlayer clickSoundEffect;
    private MediaPlayer longClickSoundEffect;
    private MediaPlayer warningSoundEffect;
    PlaySoundEffects playSoundEffects;
    List<Quote> quotes;

    public MainActivity() {

    }

    private void vibrate(int time) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (vibrator.hasVibrator()) {
            vibrator.vibrate(time);
        }

    }

    private void setRandomQuote() {
        int randomQuote = Helpers.getRandomNumber(0, quotes.size());

        Quote selectedQuote = quotes.get(randomQuote);

        TextView quoteText = findViewById(R.id.quoteText);
        TextView quoteAuthor = findViewById(R.id.quoteAuthor);

        quoteText.setText(selectedQuote.text);
        quoteAuthor.setText(selectedQuote.author);
    }

    private void quotesRequest() {
        String url = "https://type.fit/api/quotes";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                String text;
                String author;

                quotes = new ArrayList<>();

                for (int i = 0; i < response.length(); i++) {
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
        while (true) {

            if (this.timeEpoch == 0) {
                TimeUnit.SECONDS.sleep(1);
                continue;
            }

            long currentTime = System.currentTimeMillis();
            long total = currentTime - timeEpoch;
            if (total >= 30000) {
                playSoundEffects.playSoundEffect(warningSoundEffect);
                TextView editText = (TextView) findViewById(R.id.counterTextLabel);
                vibrate(50);
                int currentClicks = ClickActions.decreaseClicks(editText, 1);

                // When the clicks is 0 stop click and vibration.
                if (currentClicks == 0) {
                    this.timeEpoch = 0;
                }
            }

            TimeUnit.SECONDS.sleep(1);
            Log.d("THREAD", String.format(Long.toString(total)));
        }

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String description = "This is a test channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(TEST_CHANNEL, TEST_CHANNEL, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void addNotification(String value) {
        createNotificationChannel();

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, TEST_CHANNEL)
                        .setSmallIcon(R.drawable.cookie)
                        .setContentTitle(MainActivity.TITLE)
                        .setContentText(value)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent notificationIntent = new Intent(this, NotificationView.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        notificationIntent.putExtra(MainActivity.TITLE, value);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    private void rewards(TextView textView){
        int value = Integer.parseInt(textView.getText().toString());

        if ( value >= ( notificationStatus + THRESHOLD )){
            addNotification(
                    String.format(CONGRATULATIONS_1000_CLICKS, String.valueOf(value)));
            notificationStatus = value;
            textView.setTextColor(Color.parseColor(COLOR_GOLD));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playSoundEffects = new PlaySoundEffects();

        clickSoundEffect = MediaPlayer.create(this, R.raw.click_sound);
        longClickSoundEffect = MediaPlayer.create(this, R.raw.click_long);
        warningSoundEffect = MediaPlayer.create(this, R.raw.warning_sound);

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
                playSoundEffects.playSoundEffect(clickSoundEffect);
                setRandomQuote();
                rewards(editText);
            }
        });
        button.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                timeEpoch = System.currentTimeMillis();
                playSoundEffects.playSoundEffect(longClickSoundEffect);
                Log.d("BUTTONS", "Button long click teste pressed.");
                TextView editText = (TextView) findViewById(R.id.counterTextLabel);
                vibrate(500);
                ClickActions.increaseClicks(editText, 15);
                setRandomQuote();
                rewards(editText);
                return true;
            }
        });

    }

}