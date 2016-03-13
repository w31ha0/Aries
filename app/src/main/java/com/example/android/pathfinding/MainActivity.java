package com.example.android.pathfinding;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;


public class MainActivity extends ActionBarActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void directory_page(View v){
        if (v.getId()== R.id.directory_button)
        {
            Intent intentDirectory = new Intent(MainActivity.this, Directory.class);
            startActivity(intentDirectory);
        }
    }

    public void profile_page(View v){
        if (v.getId()== R.id.profile_button)
        {
            Intent intentProfile = new Intent(MainActivity.this, Profile.class);
            startActivity(intentProfile);
        }

    }
    public void map1_page(View v){
        if (v.getId()== R.id.mapplot_button)
        {
            Intent intentGMap = new Intent(MainActivity.this, SubActivity.class);
            startActivity(intentGMap);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
