package com.example.clickerFrenzy;

public class Helpers {

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
