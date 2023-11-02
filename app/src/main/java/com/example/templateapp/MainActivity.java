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

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private long timeEpoch = 0;
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

        try {
            Quotes quotes = new Quotes();
            quotes.getAllQuotes();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


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
                return true;
            }
        });

    }

}