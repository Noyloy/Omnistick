package com.noy.loy.omnistick;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
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
import java.util.Map;
import java.util.TreeMap;


public class Projects extends Activity {
    ListView mListView;
    MediaPlayer backgroundSound;
    Map<Integer,Integer> clickMap = new HashMap<>();
    ArrayList<Long> playPauseBackground = new ArrayList<>();
    MyPlayer player;
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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Integer clickTimes = clickMap.get(position);
                if (clickTimes == null) clickTimes = 0;
                if (clickTimes % 2 == 0) {
                    ImageView thumbnail = (ImageView) view.findViewById(R.id.list_image);
                    playProjectNum(position + 1, thumbnail);
                } else {
                    stopProjectNum(position);
                    ImageView thumbnail = (ImageView) view.findViewById(R.id.list_image);
                    thumbnail.setImageResource(R.drawable.play);
                }
                clickTimes++;
                clickMap.put(position, clickTimes);
            }
        });
    }
    private void stopProjectNum(int projNum){
        if (backgroundSound.isPlaying())
            backgroundSound.stop();
        player.cancel(false);
    }

    private void playProjectNum(final int projNum,final ImageView thumb){
        player = new MyPlayer();
        player.execute(projNum);
        thumb.setImageResource(R.drawable.stop);
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
        int numOfProj = prefs.getInt(Setup.PROJECT_NUM, 0);
        for (int i=1;i<=numOfProj;i++){
            String tmpName = prefs.getString(Setup.PROJECT_NAME_KEY+i,"NO-NAME");
            names.add(i-1,tmpName);
        }
        return names;
    }

    private ArrayList<String> getLengths(){
        ArrayList<String> lengths = new ArrayList<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Projects.this);
        int numOfProj = prefs.getInt(Setup.PROJECT_NUM, 0);
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
        Log.d("projectMap",project_index+"");
        HashMap<Long,Uri> outputMap = new HashMap<Long,Uri>();
        SharedPreferences pSharedPref = PreferenceManager.getDefaultSharedPreferences(Projects.this);

        try {
            if (pSharedPref != null) {
                // load background sound
                final String sound_str = pSharedPref.getString(Setup.BACKGROUND_KEY + project_index,"android.resource://com.noy.loy.omnistick/raw/kick_02");
                backgroundSound = MediaPlayer.create(Projects.this, Uri.parse(sound_str));
                backgroundSound.setLooping(false);
                final String playPause = pSharedPref.getString(Setup.BACKGROUND_KEY + project_index+"TIMES","");
                String[] playPauseString = playPause.split(",");
                for(int i=0;i<playPauseString.length;i++){
                    playPauseBackground.add(Long.parseLong(playPauseString[i]));
                }

                String jsonString = pSharedPref.getString(Setup.PROJECT_CONTENT_KEY + project_index, (new JSONObject()).toString());
                Log.d("JSONPROJ",jsonString);
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while (keysItr.hasNext()) {
                    String key = keysItr.next();
                    Long timeKey = Long.valueOf(key);
                    Uri value = Uri.parse((String) jsonObject.get(key));
                    outputMap.put(timeKey, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputMap;
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
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

    public class MyPlayer extends AsyncTask<Integer, Long, View> {

        @Override
        protected View doInBackground(Integer... params) {


            HashMap<Long,Uri> project_content = loadProjectMap(params[0]);
            Map<Long,Uri> treeMap = new TreeMap<Long,Uri>(project_content);
            Session playback = new Session("");
            long lastTimePlayed = 0;
            Iterator it = treeMap.entrySet().iterator();


            // background music
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Iterator<Long> iterator = playPauseBackground.iterator();
                    while(iterator.hasNext())
                    {
                        // sleep till my time comes
                        try {
                            Thread.sleep(iterator.next());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (backgroundSound.isPlaying()){
                            backgroundSound.pause();
                        }
                        else{
                            backgroundSound.start();
                        }
                    }
                }
            }).start();

            while (it.hasNext() && !isCancelled()) {
                Map.Entry pair = (Map.Entry)it.next();
                // wait until my time comes
                try {
                    Thread.sleep(Long.parseLong(pair.getKey().toString())-lastTimePlayed);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Thread t = playback.playSound(Projects.this,Uri.parse(pair.getValue().toString()));

                lastTimePlayed = Long.parseLong(pair.getKey().toString());
                it.remove(); // avoids a ConcurrentModificationException
            }
            return getViewByPosition(params[0]-1,mListView);
        }

        @Override
        protected void onPostExecute(final View v) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ImageView thumbnail = (ImageView) v.findViewById(R.id.list_image);
                    thumbnail.setImageResource(R.drawable.play);
                }
            });
        }
    }
}
