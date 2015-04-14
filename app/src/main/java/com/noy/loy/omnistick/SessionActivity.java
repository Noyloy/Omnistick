package com.noy.loy.omnistick;

import android.app.Activity;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOException;


public class SessionActivity extends Activity implements SensorEventListener {
    public final String A_KEY = "A";
    public final String B_KEY = "B";
    public final String C_KEY = "C";
    public final String D_KEY = "D";
    private SharedPreferences prefs;



    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private Button aBtn, bBtn, cBtn, dBtn, recBtn;
    private Uri soundToPlay;
    private Session session;

    private long lastUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        // Hide the status bar.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Get Preferences
        prefs = getPreferences(MODE_PRIVATE);
        session = new Session();
        // Get buttons
        aBtn = (Button)findViewById(R.id.a_button);
        bBtn = (Button)findViewById(R.id.b_button);
        cBtn = (Button)findViewById(R.id.c_button);
        dBtn = (Button)findViewById(R.id.d_button);
        recBtn = (Button)findViewById(R.id.rec_button);
        // Set Listeners
        aBtn.setOnClickListener(new SoundButtonClickListener());
        bBtn.setOnClickListener(new SoundButtonClickListener());
        cBtn.setOnClickListener(new SoundButtonClickListener());
        dBtn.setOnClickListener(new SoundButtonClickListener());

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

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
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];
        float accelerationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = System.currentTimeMillis();
        if (accelerationSquareRoot >= 1.7)
        {

            session.playSound(getApplicationContext(),soundToPlay);
        }
    }


    class SoundButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.a_button:
                    soundToPlay = Uri.parse(prefs.getString(A_KEY,"android.resource://com.noy.loy.omnistick/" + R.raw.kick_02));
                    break;
                case R.id.b_button:
                    soundToPlay = Uri.parse(prefs.getString(B_KEY,"android.resource://com.noy.loy.omnistick/" + R.raw.kick_03));
                    break;
                case R.id.c_button:
                    soundToPlay = Uri.parse(prefs.getString(C_KEY,"android.resource://com.noy.loy.omnistick/" + R.raw.kick_04));
                    break;
                case R.id.d_button:
                    soundToPlay = Uri.parse(prefs.getString(D_KEY,"android.resource://com.noy.loy.omnistick/" + R.raw.kick_05));
                    break;
            }

        }
    }
}
