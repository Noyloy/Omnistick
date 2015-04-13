package com.noy.loy.omnistick;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;
import java.util.List;

/**
 * Created by Noyloy on 13-Apr-15.
 */
public class Session {
    private String name;
    private Long duration;
    private List<Uri> soundLog;

    public void Session(){ }
    /* play sound without logging it*/
    public void playSound(final Context context, final Uri soundPath){
        // work on thread
        new Thread(new Runnable(){
            @Override
            public void run() {
                // init media object
                MediaPlayer sound = MediaPlayer.create(context, soundPath);
                sound.setLooping(false);
                try {
                    sound.prepare();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // play sound
                sound.start();
            }
        }).start();
    }
    /* play sound and log it*/
    public void playSound(final Context context, final Uri soundPath, Long time){
        // work on thread
        new Thread(new Runnable(){
            @Override
            public void run() {
                // init media object
                MediaPlayer sound = MediaPlayer.create(context, soundPath);
                sound.setLooping(false);
                try {
                    sound.prepare();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // play sound
                sound.start();
            }
        }).start();
        // register when the sound was played
        registerTime(soundPath, time);
    }
    public void registerTime(Uri soundPath, Long milisecFromStart){ }
    public void endSession(){ }
    public void startSession(){ }
    public boolean saveSession(String sessionName){ return false; }
}
