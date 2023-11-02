package com.example.templateapp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class Quotes {

    private final URL url = new URL("https://type.fit/api/quotes");

    public List<Quote> getAllQuotes() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) this.url.openConnection();
        connection.setRequestMethod("GET");

        //InputStream responseStream = connection.getInputStream();
        //String response = new BufferedReader(
        //        new InputStreamReader(responseStream, StandardCharsets.UTF_8))
        //        .lines()
        //        .collect(Collectors.joining("\n"));

        //Log.d("QUOTES", response);

        Quote quote = new Quote();

    return null;
    }

    public Quotes() throws MalformedURLException {



    }
}
