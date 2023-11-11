package com.example.clickerFrenzy;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationView extends AppCompatActivity {
    private static final String MESSAGE = "message";
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_view);
        textView = findViewById(R.id.textView);

        //getting the notification message
        String message = getIntent().getStringExtra(MESSAGE);
        textView.setText(message);
    }
}
