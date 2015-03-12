package com.noy.loy.omnistick;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start Button Listener Go To Session Activity
        final Button start = (Button)findViewById(R.id.startBtn);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Session.class);
                startActivity(intent);
            }
        });

        // Projects Button Listener Go To Projects Activity
        Button projects = (Button)findViewById(R.id.projectsBtn);
        projects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Projects.class);
                startActivity(intent);
            }
        });

        // Setup Button Listener Go To Setup Activity
        Button setup = (Button)findViewById(R.id.setupBtn);
        setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Setup.class);
                startActivity(intent);
            }
        });

        // How To Button Listener Go To How To Activity
        Button how_to = (Button)findViewById(R.id.howToBtn);
        how_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),HowTo.class);
                startActivity(intent);
            }
        });
    }

}
