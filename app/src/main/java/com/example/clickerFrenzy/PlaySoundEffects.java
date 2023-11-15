package com.example.clickerFrenzy;

import android.media.MediaPlayer;
import android.util.Log;

public class PlaySoundEffects {
    public void playSoundEffect(MediaPlayer soundEffect) {
        Log.d("SOUND", "This is the function");
        soundEffect.start();
    }
}
