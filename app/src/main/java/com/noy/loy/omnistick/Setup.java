package com.noy.loy.omnistick;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class Setup extends Activity {
    public static final String A_KEY = "A";
    public static final String B_KEY = "B";
    public static final String C_KEY = "C";
    public static final String D_KEY = "D";

    private SharedPreferences prefs;

    private Button aBtn, bBtn, cBtn, dBtn, aBtnClear, bBtnClear, cBtnClear, dBtnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        // Init Preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // Get buttons
        aBtn = (Button)findViewById(R.id.a_button_set);
        bBtn = (Button)findViewById(R.id.b_button_set);
        cBtn = (Button)findViewById(R.id.c_button_set);
        dBtn = (Button)findViewById(R.id.d_button_set);
        aBtnClear = (Button)findViewById(R.id.a_button_clear);
        bBtnClear = (Button)findViewById(R.id.b_button_clear);
        cBtnClear = (Button)findViewById(R.id.c_button_clear);
        dBtnClear = (Button)findViewById(R.id.d_button_clear);


        // Set Listeners
        aBtn.setOnClickListener(new SetSoundButtonClickListener());
        bBtn.setOnClickListener(new SetSoundButtonClickListener());
        cBtn.setOnClickListener(new SetSoundButtonClickListener());
        dBtn.setOnClickListener(new SetSoundButtonClickListener());
        aBtnClear.setOnClickListener(new ClearSoundButtonClickListener());
        bBtnClear.setOnClickListener(new ClearSoundButtonClickListener());
        cBtnClear.setOnClickListener(new ClearSoundButtonClickListener());
        dBtnClear.setOnClickListener(new ClearSoundButtonClickListener());
    }

    class SetSoundButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent select = new Intent();
            select.setAction(android.content.Intent.ACTION_PICK);
            select.setData(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            switch(v.getId()){
                case R.id.a_button_set:
                    startActivityForResult(select, R.id.a_button_set);
                    break;
                case R.id.b_button_set:
                    startActivityForResult(select, R.id.b_button_set);
                    break;
                case R.id.c_button_set:
                    startActivityForResult(select, R.id.c_button_set);
                    break;
                case R.id.d_button_set:
                    startActivityForResult(select, R.id.d_button_set);
                    break;
            }

        }
    }
    class ClearSoundButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // Prepare to remove from prefs
            SharedPreferences.Editor editor = prefs.edit();
            switch(v.getId()){
                case R.id.a_button_clear:
                    editor.remove(A_KEY);
                    break;
                case R.id.b_button_clear:
                    editor.remove(B_KEY);
                    break;
                case R.id.c_button_clear:
                    editor.remove(C_KEY);
                    break;
                case R.id.d_button_clear:
                    editor.remove(D_KEY);
                    break;
            }
            editor.commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        // Get new sound selected
        Uri newSound = data.getData();
        // Prepare to save to prefs
        SharedPreferences.Editor editor = prefs.edit();
        switch (requestCode){
            case R.id.a_button_set:
                editor.putString(A_KEY,newSound.toString());
                break;
            case R.id.b_button_set:
                editor.putString(B_KEY,newSound.toString());
                break;
            case R.id.c_button_set:
                editor.putString(C_KEY,newSound.toString());
                break;
            case R.id.d_button_set:
                editor.putString(D_KEY,newSound.toString());
                break;
        }
        editor.commit();
    }

}
