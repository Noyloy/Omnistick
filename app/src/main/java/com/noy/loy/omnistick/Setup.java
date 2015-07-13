package com.noy.loy.omnistick;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.CheckedInputStream;


public class Setup extends Activity {

    public static final String NONE_KEY = "NONE";
    public static final int NONE_CODE = 0;

    public static final String A_KEY = "A";
    public static final int A_CODE = 1;
    public static final String B_KEY = "B";
    public static final int B_CODE = 2;
    public static final String C_KEY = "C";
    public static final int C_CODE = 3;
    public static final String D_KEY = "D";
    public static final int D_CODE = 4;

    public static final String AB_KEY = "AB";
    public static final int AB_CODE = 5;
    public static final String AC_KEY = "AC";
    public static final int AC_CODE = 6;
    public static final String AD_KEY = "AD";
    public static final int AD_CODE = 7;
    public static final String BC_KEY = "BC";
    public static final int BC_CODE = 8;
    public static final String BD_KEY = "BD";
    public static final int BD_CODE = 9;
    public static final String CD_KEY = "CD";
    public static final int CD_CODE = 10;

    public static final String ABC_KEY = "ABC";
    public static final int ABC_CODE = 11;
    public static final String ABD_KEY = "ABD";
    public static final int ABD_CODE = 12;
    public static final String ACD_KEY = "ACD";
    public static final int ACD_CODE = 13;
    public static final String BCD_KEY = "BCD";
    public static final int BCD_CODE = 14;

    public static final String ABCD_KEY = "ABCD";
    public static final int ABCD_CODE = 15;

    public static final String BACKGROUND_KEY = "BACKGROUND";
    public static final int BACKGROUND_CODE = 16;

    public static final String LEFTY_KEY = "LEFTY";
    public static final String PROJECT_CONTENT_KEY = "PROJECT";
    public static final String PROJECT_NAME_KEY = "PROJECTNAME";
    public static final String PROJECT_LENGTH_KEY = "PROJECTLEN";
    public static final String PROJECT_NUM = "PROJECT_N";

    public static final String SENSITIVITY_KEY = "SENSITIVITY";
    public static int SENSITIVITY_VALUE = 10;


    private SharedPreferences prefs;

    private List<Integer> combination = new ArrayList<Integer>();

    private ToggleButton aBtn, bBtn, cBtn, dBtn, leftyBtn;
    private Button setBtn, clearBtn, backgroundBtn;

    private boolean isLefty = false;
    private int sens = 10;

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

        // Get Toggle buttons
        aBtn = (ToggleButton)findViewById(R.id.a_button_set);
        bBtn = (ToggleButton)findViewById(R.id.b_button_set);
        cBtn = (ToggleButton)findViewById(R.id.c_button_set);
        dBtn = (ToggleButton)findViewById(R.id.d_button_set);
        leftyBtn = (ToggleButton)findViewById(R.id.lefty_button);

        // Get buttons
        setBtn = (Button) findViewById(R.id.setup_button);
        clearBtn = (Button) findViewById(R.id.clear_button);
        backgroundBtn = (Button) findViewById(R.id.background_button_set);


        // Get spinner
        Spinner spinner = (Spinner)findViewById(R.id.sens_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sensitivity_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        // sensitivity from prefs
        sens = prefs.getInt(SENSITIVITY_KEY,SENSITIVITY_VALUE);
        spinner.setSelection((sens == 8) ? 0 : (sens == 10) ? 1 : 2);

        // Set Listeners
        aBtn.setOnClickListener(new CombinationChangeListener());
        bBtn.setOnClickListener(new CombinationChangeListener());
        cBtn.setOnClickListener(new CombinationChangeListener());
        dBtn.setOnClickListener(new CombinationChangeListener());

        setBtn.setOnClickListener(new SetSoundButtonClickListener());
        backgroundBtn.setOnClickListener(new SetSoundButtonClickListener());
        clearBtn.setOnClickListener(new ClearSoundButtonClickListener());

        leftyBtn.setChecked(isLefty);
        leftyBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Prepare to save to prefs
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(LEFTY_KEY, isChecked);
                editor.commit();
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int newSens = SENSITIVITY_VALUE;
                //position 0 -> high
                if (position==0)
                    newSens = 5;
                //position 1 -> med
                else if (position==1)
                    newSens = 10;
                //position 2 -> low
                else if (position==2)
                    newSens = 15;
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(SENSITIVITY_KEY,newSens);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private String getPrefsComboKey(){

        // all 4 buttons pressed
        if (combination.contains(R.id.a_button_set) && combination.contains(R.id.b_button_set)
                && combination.contains(R.id.c_button_set) && combination.contains(R.id.d_button_set))
            return Setup.ABCD_KEY;

            // 3 buttons pressed
        else if (combination.contains(R.id.a_button_set) && combination.contains(R.id.b_button_set)
                && combination.contains(R.id.c_button_set))
            return Setup.ABC_KEY;
        else if (combination.contains(R.id.a_button_set) && combination.contains(R.id.b_button_set)
                && combination.contains(R.id.d_button_set))
            return Setup.ABD_KEY;
        else if (combination.contains(R.id.a_button_set) && combination.contains(R.id.c_button_set)
                && combination.contains(R.id.d_button_set))
            return Setup.ACD_KEY;
        else if (combination.contains(R.id.b_button_set) && combination.contains(R.id.c_button_set)
                && combination.contains(R.id.d_button_set))
            return Setup.BCD_KEY;

            // 2 buttons pressed
        else if (combination.contains(R.id.a_button_set) && combination.contains(R.id.b_button_set))
            return Setup.AB_KEY;
        else if (combination.contains(R.id.a_button_set) && combination.contains(R.id.c_button_set))
            return Setup.AC_KEY;
        else if (combination.contains(R.id.a_button_set) && combination.contains(R.id.d_button_set))
            return Setup.AD_KEY;
        else if (combination.contains(R.id.b_button_set) && combination.contains(R.id.c_button_set))
            return Setup.BC_KEY;
        else if (combination.contains(R.id.b_button_set) && combination.contains(R.id.d_button_set))
            return Setup.BD_KEY;
        else if (combination.contains(R.id.c_button_set) && combination.contains(R.id.d_button_set))
            return Setup.CD_KEY;

            // 1 button presed
        else if (combination.contains(R.id.a_button_set))
            return Setup.A_KEY;
        else if (combination.contains(R.id.b_button_set))
            return Setup.B_KEY;
        else if (combination.contains(R.id.c_button_set))
            return Setup.C_KEY;
        else if (combination.contains(R.id.d_button_set))
            return Setup.D_KEY;

        // default sound
        return Setup.NONE_KEY;
    }
    private int getPrefsComboKeyCode(){

        // all 4 buttons pressed
        if (combination.contains(R.id.a_button_set) && combination.contains(R.id.b_button_set)
                && combination.contains(R.id.c_button_set) && combination.contains(R.id.d_button_set))
            return Setup.ABCD_CODE;

            // 3 buttons pressed
        else if (combination.contains(R.id.a_button_set) && combination.contains(R.id.b_button_set)
                && combination.contains(R.id.c_button_set))
            return Setup.ABC_CODE;
        else if (combination.contains(R.id.a_button_set) && combination.contains(R.id.b_button_set)
                && combination.contains(R.id.d_button_set))
            return Setup.ABD_CODE;
        else if (combination.contains(R.id.a_button_set) && combination.contains(R.id.c_button_set)
                && combination.contains(R.id.d_button_set))
            return Setup.ACD_CODE;
        else if (combination.contains(R.id.b_button_set) && combination.contains(R.id.c_button_set)
                && combination.contains(R.id.d_button_set))
            return Setup.BCD_CODE;

            // 2 buttons pressed
        else if (combination.contains(R.id.a_button_set) && combination.contains(R.id.b_button_set))
            return Setup.AB_CODE;
        else if (combination.contains(R.id.a_button_set) && combination.contains(R.id.c_button_set))
            return Setup.AC_CODE;
        else if (combination.contains(R.id.a_button_set) && combination.contains(R.id.d_button_set))
            return Setup.AD_CODE;
        else if (combination.contains(R.id.b_button_set) && combination.contains(R.id.c_button_set))
            return Setup.BC_CODE;
        else if (combination.contains(R.id.b_button_set) && combination.contains(R.id.d_button_set))
            return Setup.BD_CODE;
        else if (combination.contains(R.id.c_button_set) && combination.contains(R.id.d_button_set))
            return Setup.CD_CODE;

            // 1 button presed
        else if (combination.contains(R.id.a_button_set))
            return Setup.A_CODE;
        else if (combination.contains(R.id.b_button_set))
            return Setup.B_CODE;
        else if (combination.contains(R.id.c_button_set))
            return Setup.C_CODE;
        else if (combination.contains(R.id.d_button_set))
            return Setup.D_CODE;

        // default sound
        return Setup.NONE_CODE;
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
            case NONE_CODE:
                editor.putString(NONE_KEY,newSound.toString());
                break;
            case A_CODE:
                editor.putString(A_KEY,newSound.toString());
                break;
            case B_CODE:
                editor.putString(B_KEY,newSound.toString());
                break;
            case C_CODE:
                editor.putString(C_KEY,newSound.toString());
                break;
            case D_CODE:
                editor.putString(D_KEY,newSound.toString());
                break;
            case AB_CODE:
                editor.putString(AB_KEY,newSound.toString());
                break;
            case AC_CODE:
                editor.putString(AC_KEY,newSound.toString());
                break;
            case AD_CODE:
                editor.putString(AD_KEY,newSound.toString());
                break;
            case BC_CODE:
                editor.putString(BC_KEY,newSound.toString());
                break;
            case BD_CODE:
                editor.putString(BD_KEY,newSound.toString());
                break;
            case CD_CODE:
                editor.putString(CD_KEY,newSound.toString());
                break;
            case ABC_CODE:
                editor.putString(ABC_KEY,newSound.toString());
                break;
            case ABD_CODE:
                editor.putString(ABD_KEY,newSound.toString());
                break;
            case ACD_CODE:
                editor.putString(ACD_KEY,newSound.toString());
                break;
            case BCD_CODE:
                editor.putString(BCD_KEY,newSound.toString());
                break;
            case ABCD_CODE:
                editor.putString(ABCD_KEY,newSound.toString());
                break;
            case BACKGROUND_CODE:
                editor.putString(BACKGROUND_KEY,newSound.toString());
                break;
        }
        editor.commit();
    }

    class CombinationChangeListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            ToggleButton tb = (ToggleButton)v;
            if (tb.isChecked()) combination.add(tb.getId());
            else combination.remove(combination.indexOf(tb.getId()));
        }
    }
    class SetSoundButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent select = new Intent();
            select.setAction(android.content.Intent.ACTION_PICK);
            select.setData(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            if (v.getId() == backgroundBtn.getId()){
                startActivityForResult(select, BACKGROUND_CODE);
            }
            else{
                int code = getPrefsComboKeyCode();
                startActivityForResult(select, code);
            }
        }
    }
    class ClearSoundButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // Prepare to remove from prefs
            SharedPreferences.Editor editor = prefs.edit();
            String code = getPrefsComboKey();
            editor.remove(code);
            editor.commit();
        }
    }
}
