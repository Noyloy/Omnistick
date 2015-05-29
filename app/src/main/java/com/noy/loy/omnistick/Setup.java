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
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.zip.CheckedInputStream;


public class Setup extends Activity {

    public static final String A_KEY = "A";
    public static final String B_KEY = "B";
    public static final String C_KEY = "C";
    public static final String D_KEY = "D";

    public static final String AB_KEY = "AB";
    public static final String AC_KEY = "AC";
    public static final String AD_KEY = "AD";
    public static final String BC_KEY = "BC";
    public static final String BD_KEY = "BD";
    public static final String CD_KEY = "CD";

    public static final String ABC_KEY = "ABC";
    public static final String ABD_KEY = "ABD";
    public static final String ACD_KEY = "ACD";
    public static final String BCD_KEY = "BCD";

    public static final String ABCD_KEY = "ABCD";


    public static final String LEFTY_KEY = "LEFTY";

    private SharedPreferences prefs;

    private Button aBtn, bBtn, cBtn, dBtn, aBtnClear, bBtnClear, cBtnClear, dBtnClear;
    private CheckBox leftyCb;
    private boolean isLefty = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init Preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // Lefty support
        isLefty = prefs.getBoolean(LEFTY_KEY,false);
        if (isLefty) {
            setContentView(R.layout.activity_setup_lefty);
        }
        else{
            setContentView(R.layout.activity_setup);
        }

        // Get buttons
        aBtn = (Button)findViewById(R.id.a_button_set);
        bBtn = (Button)findViewById(R.id.b_button_set);
        cBtn = (Button)findViewById(R.id.c_button_set);
        dBtn = (Button)findViewById(R.id.d_button_set);
        aBtnClear = (Button)findViewById(R.id.a_button_clear);
        bBtnClear = (Button)findViewById(R.id.b_button_clear);
        cBtnClear = (Button)findViewById(R.id.c_button_clear);
        dBtnClear = (Button)findViewById(R.id.d_button_clear);
        // Get CheckBox
        leftyCb = (CheckBox)findViewById(R.id.lefty_check_box);

        // Set Listeners
        aBtn.setOnClickListener(new SetSoundButtonClickListener());
        bBtn.setOnClickListener(new SetSoundButtonClickListener());
        cBtn.setOnClickListener(new SetSoundButtonClickListener());
        dBtn.setOnClickListener(new SetSoundButtonClickListener());
        aBtnClear.setOnClickListener(new ClearSoundButtonClickListener());
        bBtnClear.setOnClickListener(new ClearSoundButtonClickListener());
        cBtnClear.setOnClickListener(new ClearSoundButtonClickListener());
        dBtnClear.setOnClickListener(new ClearSoundButtonClickListener());

        leftyCb.setChecked(isLefty);
        leftyCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Prepare to save to prefs
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(LEFTY_KEY,isChecked);
                editor.commit();
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
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
