package com.example.clickerFrenzy.core;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.clickerFrenzy.R;

public class NotificationView extends AppCompatActivity {
    private static final String MESSAGE = "message";
    protected TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_view);
        textView = findViewById(R.id.textView);

        // Getting the notification message
        String message = getIntent().getStringExtra(MESSAGE);
        textView.setText(message);
    }
}
