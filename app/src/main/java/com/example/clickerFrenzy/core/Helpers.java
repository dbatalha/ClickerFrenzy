package com.example.clickerFrenzy.core;

public class Helpers {

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
