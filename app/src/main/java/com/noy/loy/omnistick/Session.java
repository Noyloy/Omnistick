package com.noy.loy.omnistick;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Noyloy on 13-Apr-15.
 */
public class Session {
    private String name;
    private Long duration;
    private Long startMilisec;
    private HashMap<Long,Uri> soundLog;

    public void Session(){ }
    /* play sound without logging it*/
    public void playSound(final Context context, final Uri soundPath){
        // work on thread
        new Thread(new Runnable(){
            @Override
            public void run() {
                // init media object
                MediaPlayer sound = MediaPlayer.create(context, soundPath);
                sound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                        mp = null;
                    }
                });
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
                sound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                        mp = null;
                    }
                });
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
    /* register which sound was played and when */
    public void registerTime(Uri soundPath, Long sysMilisec){
        Long milisecFromStart = sysMilisec - startMilisec;
        soundLog.put(milisecFromStart,soundPath);
    }
    /* set time of stop recording, set duration */
    public void endSession(){
        Long endMilisec = System.currentTimeMillis();
        duration = endMilisec - startMilisec;
    }
    /* start recording, set time of start*/
    public void startSession(){
        startMilisec = System.currentTimeMillis();
    }
    public boolean saveSession(String sessionName){

        return false;
    }
}
