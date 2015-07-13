package com.noy.loy.omnistick;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;



public class Projects extends Activity {
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);
        mListView = (ListView) findViewById(R.id.list);

        populateList();
    }

    private void populateList() {
        ArrayList<String> names = getNames();
        ArrayList<String> lengths = getLengths();
        ArrayAdapter<String> name_adapt = new ArrayAdapter<String>(Projects.this,R.layout.project_line,R.id.title,names);
        mListView.setAdapter(name_adapt);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("PROJECTS_D","Selection of item "+position+" THE VIEW:"+ view+" id:"+ id );
            }
        });
    }

    private ArrayList<String> getNames(){
        ArrayList<String> names = new ArrayList<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Projects.this);
        int numOfProj = prefs.getInt(Setup.PROJECT_NUM,0);
        for (int i=1;i<=numOfProj;i++){
            String tmpName = prefs.getString(Setup.PROJECT_NAME_KEY+i,"NO-NAME");
            names.add(i-1,tmpName);
        }
        return names;
    }

    private ArrayList<String> getLengths(){
        ArrayList<String> lengths = new ArrayList<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Projects.this);
        int numOfProj = prefs.getInt(Setup.PROJECT_NUM,0);
        for (int i=1;i<=numOfProj;i++){
            long milisec = prefs.getLong(Setup.PROJECT_LENGTH_KEY+i,0);
            // minutes calc
            long minutes = milisec/60000;
            // seconds left calc
            long secondes = milisec/1000 - minutes;
            lengths.add(i-1,minutes+":"+secondes);
        }
        return lengths;
    }

    private HashMap<Long,Uri> loadProjectMap(int project_index) {
        HashMap<Long,Uri> outputMap = new HashMap<Long,Uri>();
        SharedPreferences pSharedPref = PreferenceManager.getDefaultSharedPreferences(Projects.this);

        try {
            if (pSharedPref != null) {
                String jsonString = pSharedPref.getString(Setup.PROJECT_CONTENT_KEY + project_index, (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while (keysItr.hasNext()) {
                    String key = keysItr.next();
                    Long timeKey = Long.valueOf(key).longValue();
                    Uri value = (Uri) jsonObject.get(key);
                    outputMap.put(timeKey, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputMap;
    }

}
