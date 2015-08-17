package com.noy.loy.omnistick;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONObject;

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
    private HashMap<String,String> soundLog = new HashMap<>();
    private SharedPreferences prefs;
    private String playPauseBackground = "";
    private String backgroundSound = "";
    int backgroundCount = 0;

    Session(String backgroundSound){
        this.backgroundSound = backgroundSound;
    }

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
        if (startMilisec!=null)
            registerTime(soundPath, time);
    }

    /* register which sound was played and when */
    public void registerTime(Uri soundPath, Long sysMilisec){
        Long milisecFromStart = sysMilisec - startMilisec;
        soundLog.put(String.valueOf(milisecFromStart),String.valueOf(soundPath));
    }

    public void registerBackTime(Long sysTime) {
        backgroundCount++;
        playPauseBackground+=(sysTime-startMilisec)+",";
    }

    /* set time of stop recording, set duration */
    public void endSession(){
        Long endMilisec = System.currentTimeMillis();
        duration = endMilisec - startMilisec;
        if (backgroundCount%2!=0) playPauseBackground+=duration+"";
        else playPauseBackground=playPauseBackground.substring(0,playPauseBackground.length()-1);
        Log.d("SESSION_DEBUG", "Session End : " +soundLog.toString());
    }

    /* start recording, set time of start*/
    public void startSession(){
        startMilisec = System.currentTimeMillis();
    }

    public boolean saveSession(final Context context, String sessionName){
        // Init Preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int sessionNum = prefs.getInt(Setup.PROJECT_NUM,0);
        sessionNum++;
        SharedPreferences.Editor editor = prefs.edit();
        JSONObject jsonObject = new JSONObject(soundLog);
        String sessionStr = jsonObject.toString();

        editor.putString(Setup.PROJECT_CONTENT_KEY + sessionNum, sessionStr);
        editor.putString(Setup.PROJECT_NAME_KEY + sessionNum, sessionName);
        editor.putString(Setup.BACKGROUND_KEY + sessionNum, backgroundSound);
        editor.putString(Setup.BACKGROUND_KEY + sessionNum+"TIMES", playPauseBackground);
        editor.putLong(Setup.PROJECT_LENGTH_KEY + sessionNum, duration);
        editor.putInt(Setup.PROJECT_NUM, sessionNum);

        editor.commit();
        return false;
    }
}
