package com.noy.loy.omnistick;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
        // Construct the data source
        ArrayList<ProjectElement> arrayOfUsers = new ArrayList<ProjectElement>();
        // Create the adapter to convert the array to views
        ProjectAdapter adapter = new ProjectAdapter(this, arrayOfUsers);
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.list);
        insertProjects(arrayOfUsers);
        listView.setAdapter(adapter);



        //ArrayList<String> names = getNames();
        //ArrayList<String> lengths = getLengths();
        //ArrayList<ProjectElement> projElement = getProjects();
        //ArrayAdapter<String> name_adapt = new ArrayAdapter<String>(Projects.this,R.layout.project_line,R.id.title,names);
        //mListView.setAdapter(name_adapt);
        //mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        //    @Override
        //    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //        Log.d("PROJECTS_D","Selection of item "+position+" THE VIEW:"+ view+" id:"+ id );
        //    }
        //});
    }

    private void insertProjects(ArrayList<ProjectElement> arrayOfUsers) {
        ArrayList<String> names = getNames();
        ArrayList<String> lengths = getLengths();
        for (int i = 0; i<names.size(); i++){
            arrayOfUsers.add(new ProjectElement(names.get(i),lengths.get(i)));
        }
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
            long secondes = milisec/1000 - minutes*60;
            String min = (minutes<10)?"0"+minutes : ""+minutes;
            String sec = (secondes<10)?"0"+secondes : ""+secondes;
            lengths.add(i-1,min+":"+sec);
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


    public class ProjectAdapter extends ArrayAdapter<ProjectElement> {
        public ProjectAdapter(Context context, ArrayList<ProjectElement> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            ProjectElement user = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.project_line, parent, false);
            }
            // Lookup view for data population
            TextView tvName = (TextView) convertView.findViewById(R.id.title);
            TextView tvLength = (TextView) convertView.findViewById(R.id.duration);
            ImageView thumbnail = (ImageView) convertView.findViewById(R.id.list_image);

            // Populate the data into the template view using the data object
            tvName.setText(user.name);
            tvLength.setText(user.length);
            thumbnail.setImageResource(R.drawable.play);
            // Return the completed view to render on screen
            return convertView;
        }
    }
}
