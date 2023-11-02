package com.example.templateapp;

import android.widget.TextView;

public class ClickActions {

    public static void increaseClicks(TextView editText, int count){

        int currentNumber = Integer.parseInt(editText.getText().toString());

        currentNumber = currentNumber + count;
        editText.setText(String.valueOf(currentNumber));

    }

    public static int decreaseClicks(TextView editText, int count){

        int currentNumber = Integer.parseInt(editText.getText().toString());

        if (currentNumber != 0) {
            currentNumber = currentNumber - count;
        }
        editText.setText(String.valueOf(currentNumber));

        return currentNumber;
    }

}
