package com.noy.loy.omnistick;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class SessionActivity extends Activity implements SensorEventListener {

    private enum Direction {UP, DOWN}

    private SharedPreferences prefs;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private Button aBtn, bBtn, cBtn, dBtn, backgroundBtn, recBtn;
    private Uri soundToPlay = Uri.parse("android.resource://com.noy.loy.omnistick/raw/snar_03");
    private Session session;
    private List<Integer> combination = new ArrayList<Integer>();

    private float[] history = new float[3];
    private Direction lastMovement = Direction.UP;
    private long lastUpdate;
    private boolean isLefty = false;
    private int sensitivity = 10;
    private int recTimesClicked = 0;
    MediaPlayer backgroundSound;
    String sound_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get Preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Hide the status bar.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // Lefty support
        isLefty = prefs.getBoolean(Setup.LEFTY_KEY, false);
        if (isLefty) {
            setContentView(R.layout.activity_session_lefty);
        } else {
            setContentView(R.layout.activity_session);
        }

        // get seneitivity value
        sensitivity = prefs.getInt(Setup.SENSITIVITY_KEY,Setup.SENSITIVITY_VALUE);

        // load background sound
        sound_str = prefs.getString(Setup.BACKGROUND_KEY,"android.resource://com.noy.loy.omnistick/raw/default_background");
        backgroundSound = MediaPlayer.create(SessionActivity.this, Uri.parse(sound_str));
        backgroundSound.setLooping(false);

        // init session
        session = new Session(sound_str);



        // Get buttons
        aBtn = (Button) findViewById(R.id.a_button);
        bBtn = (Button) findViewById(R.id.b_button);
        cBtn = (Button) findViewById(R.id.c_button);
        dBtn = (Button) findViewById(R.id.d_button);
        backgroundBtn = (Button) findViewById(R.id.background_button);
        recBtn = (Button) findViewById(R.id.rec_button);

        // Set Listeners
        aBtn.setOnTouchListener(new CombineButtonTouchListener());
        bBtn.setOnTouchListener(new CombineButtonTouchListener());
        cBtn.setOnTouchListener(new CombineButtonTouchListener());
        dBtn.setOnTouchListener(new CombineButtonTouchListener());

        backgroundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // init media object

                try {
                    backgroundSound.prepare();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (backgroundSound.isPlaying()) {
                    backgroundBtn.setText(R.string.playBackground);
                    backgroundSound.pause();
                }
                else {
                    backgroundBtn.setText(R.string.pauseBackground);
                    backgroundSound.start();
                }

                if (recTimesClicked%2==1) session.registerBackTime(System.currentTimeMillis());
            }
        });

        recBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recTimesClicked++;
                if (recTimesClicked%2==1){
                    session.startSession();
                    int imgResource = R.drawable.save;
                    recBtn.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                    recBtn.setText(R.string.recording);
                }
                else if (recTimesClicked%2==0){
                    int imgResource = R.drawable.record;
                    recBtn.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                    recBtn.setText(R.string.record);
                    session.endSession();
                    if (backgroundSound.isPlaying())
                        backgroundSound.pause();
                    // get prompts.xml view
                    LayoutInflater layoutInflater = LayoutInflater.from(SessionActivity.this);
                    View promptView = layoutInflater.inflate(R.layout.prompt_save, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SessionActivity.this);

                    // set prompts.xml to be the layout file of the alertdialog builder
                    alertDialogBuilder.setView(promptView);
                    final EditText input = (EditText) promptView.findViewById(R.id.userInput);

                    // setup a dialog window
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // get user input and set it to result
                                    String inText = input.getText().toString();
                                    session.saveSession(SessionActivity.this, inText);
                                    session = new Session(sound_str);
                                }
                            })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,	int id) {
                                            dialog.cancel();
                                        }
                                    });

                    // create an alert dialog
                    AlertDialog alertD = alertDialogBuilder.create();

                    alertD.show();

                }
            }
        });

        // Create and register Accelerometer
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);

        // set last update for time diff
        lastUpdate = System.currentTimeMillis();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        backgroundSound.pause();
        combination.clear();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        // Current Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];
        // calc change
        float xChange = history[0] - x;
        float yChange = history[1] - y;
        float zChange = history[2] - z;

        //Log.d("DEBUG", "x = " + x + "\txHistory = " + history[0] + "\txChange = " + xChange + "\tlast movement = " + lastMovement);
        // assign to history values
        history[0] = x;
        history[1] = y;
        history[2] = z;


        // get update time
        long actualTime = System.currentTimeMillis();

        // calc direction
        // Lefty support
        // Now Up
        if ((xChange < -sensitivity && !isLefty) || (xChange > sensitivity && isLefty)) {
            //if last time Down
            if (lastMovement == Direction.DOWN && actualTime - lastUpdate > 50) {
                //Log.d("DEBUG", "playing, time diff = " + (actualTime - lastUpdate));
                session.playSound(getApplicationContext(), soundToPlay, actualTime);
                lastUpdate = actualTime;
            }
            // Update movement
            lastMovement = Direction.UP;
        }
        // Now Down
        else if ((xChange > 2 && !isLefty) || (xChange < -2 && isLefty)) {
            lastMovement = Direction.DOWN;
        }
    }

    /*** Supports one button per sound */
    class SoundButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.a_button:
                    soundToPlay = Uri.parse(prefs.getString(Setup.A_KEY, "android.resource://com.noy.loy.omnistick/raw/kick_02"));
                    break;
                case R.id.b_button:
                    soundToPlay = Uri.parse(prefs.getString(Setup.B_KEY, "android.resource://com.noy.loy.omnistick/" + R.raw.kick_03));
                    break;
                case R.id.c_button:
                    soundToPlay = Uri.parse(prefs.getString(Setup.C_KEY, "android.resource://com.noy.loy.omnistick/" + R.raw.kick_04));
                    break;
                case R.id.d_button:
                    soundToPlay = Uri.parse(prefs.getString(Setup.D_KEY, "android.resource://com.noy.loy.omnistick/" + R.raw.kick_05));
                    break;
            }

        }
    }

    /*** Supports multiple buttons per sound */
    class CombineButtonTouchListener implements View.OnTouchListener{
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            synchronized (combination) {
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_POINTER_DOWN) {
                    // add button to combination
                    if (combination.indexOf(v.getId()) == -1)
                        combination.add(v.getId());
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_POINTER_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    // remove button from combination;
                    if (combination.indexOf(v.getId()) != -1)
                        combination.remove(combination.indexOf(v.getId()));
                }
                soundToPlay = getSoundToPlay();
                return false;
            }
        }
        private String getPrefsComboKey(){

            // all 4 buttons pressed
            if (combination.contains(R.id.a_button) && combination.contains(R.id.b_button)
                    && combination.contains(R.id.c_button) && combination.contains(R.id.d_button))
                return Setup.ABCD_KEY;

            // 3 buttons pressed
            else if (combination.contains(R.id.a_button) && combination.contains(R.id.b_button)
                    && combination.contains(R.id.c_button))
                return Setup.ABC_KEY;
            else if (combination.contains(R.id.a_button) && combination.contains(R.id.b_button)
                    && combination.contains(R.id.d_button))
                return Setup.ABD_KEY;
            else if (combination.contains(R.id.a_button) && combination.contains(R.id.c_button)
                    && combination.contains(R.id.d_button))
                return Setup.ACD_KEY;
            else if (combination.contains(R.id.b_button) && combination.contains(R.id.c_button)
                    && combination.contains(R.id.d_button))
                return Setup.BCD_KEY;

            // 2 buttons pressed
            else if (combination.contains(R.id.a_button) && combination.contains(R.id.b_button))
                return Setup.AB_KEY;
            else if (combination.contains(R.id.a_button) && combination.contains(R.id.c_button))
                return Setup.AC_KEY;
            else if (combination.contains(R.id.a_button) && combination.contains(R.id.d_button))
                return Setup.AD_KEY;
            else if (combination.contains(R.id.b_button) && combination.contains(R.id.c_button))
                return Setup.BC_KEY;
            else if (combination.contains(R.id.b_button) && combination.contains(R.id.d_button))
                return Setup.BD_KEY;
            else if (combination.contains(R.id.c_button) && combination.contains(R.id.d_button))
                return Setup.CD_KEY;

            // 1 button presed
            else if (combination.contains(R.id.a_button))
                return Setup.A_KEY;
            else if (combination.contains(R.id.b_button))
                return Setup.B_KEY;
            else if (combination.contains(R.id.c_button))
                return Setup.C_KEY;
            else if (combination.contains(R.id.d_button))
                return Setup.D_KEY;

            // default sound
            return Setup.NONE_KEY;
        }
        private String getDefaultSound(String key){
            switch (key){
                case Setup.A_KEY:
                    return "android.resource://com.noy.loy.omnistick/raw/kick_02";
                case Setup.B_KEY:
                    return "android.resource://com.noy.loy.omnistick/raw/hi_h_01";
                case Setup.C_KEY:
                    return "android.resource://com.noy.loy.omnistick/raw/hi_h_02";
                case Setup.D_KEY:
                    return "android.resource://com.noy.loy.omnistick/raw/hi_h_03";
                case Setup.AB_KEY:
                    return "android.resource://com.noy.loy.omnistick/raw/tom_02";
                case Setup.AC_KEY:
                    return "android.resource://com.noy.loy.omnistick/raw/prec_02";
                case Setup.AD_KEY:
                    return "android.resource://com.noy.loy.omnistick/raw/snar_01";
                case Setup.BC_KEY:
                    return "android.resource://com.noy.loy.omnistick/raw/snar_04";
                case Setup.BD_KEY:
                    return "android.resource://com.noy.loy.omnistick/raw/snar_05";
                case Setup.CD_KEY:
                    return "android.resource://com.noy.loy.omnistick/raw/snar_07";
                case Setup.ABC_KEY:
                    return "android.resource://com.noy.loy.omnistick/raw/tom_01";
                case Setup.ABD_KEY:
                    return "android.resource://com.noy.loy.omnistick/raw/prec_01";
                case Setup.ACD_KEY:
                    return "android.resource://com.noy.loy.omnistick/raw/congo_01";
                case Setup.BCD_KEY:
                    return "android.resource://com.noy.loy.omnistick/raw/congo_02";
                case Setup.ABCD_KEY:
                    return "android.resource://com.noy.loy.omnistick/raw/cymb_04";

                case Setup.NONE_KEY:
                    return "android.resource://com.noy.loy.omnistick/raw/snar_03";
                default:
                    return "android.resource://com.noy.loy.omnistick/raw/snar_03";
            }
        }

        private Uri getSoundToPlay(){
            String key = getPrefsComboKey();
            Log.d("DEBUG", "and the key is :"+key);
            return Uri.parse(prefs.getString(key, getDefaultSound(key)));
        }
    }
}
