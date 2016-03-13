package com.example.android.pathfinding;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Directory extends ActionBarActivity {
    List<String> listx,listy;
    Button b1,b2,b3,b4,b5;
    ListView lv;
    ListAdapter adapter;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        setContentView(R.layout.activity_directory);
        lv=(ListView)findViewById(R.id.listview);
      //  b1=(Button)findViewById(R.id.route1_button);
      //  b2=(Button)findViewById(R.id.route2_button);
       // b3=(Button)findViewById(R.id.route3_button);
       // b4=(Button)findViewById(R.id.route4_button);
        /*b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args=new Bundle();
                args.putString("X",listx.get(0));
                args.putString("Y",listy.get(0));
                Intent intentDirectory = new Intent(Directory.this, ViewPlots.class);
                intentDirectory.putExtras(args);
                startActivity(intentDirectory);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("X", listx.get(1));
                args.putString("Y", listy.get(1));
                Intent intentDirectory = new Intent(Directory.this, ViewPlots.class);
                intentDirectory.putExtras(args);
                startActivity(intentDirectory);
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("X", listx.get(4));
                args.putString("Y", listy.get(4));
                Intent intentDirectory = new Intent(Directory.this, ViewPlots.class);
                intentDirectory.putExtras(args);
                startActivity(intentDirectory);
            }

        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args=new Bundle();
                args.putString("X",listx.get(6));
                args.putString("Y",listy.get(6));
                Intent intentDirectory = new Intent(Directory.this, ViewPlots.class);
                intentDirectory.putExtras(args);
                startActivity(intentDirectory);
            }
        });*/
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle args=new Bundle();
                args.putString("X",listx.get(position));
                args.putString("Y",listy.get(position));
                Intent intentDirectory = new Intent(Directory.this, ViewPlots.class);
                intentDirectory.putExtras(args);
                startActivity(intentDirectory);
            }
        });
        listx=new ArrayList<String>();
        listy=new ArrayList<String>();
        String URL="http://lewspage.hostei.com/scripts/jobretrieve.php";
        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("HELLOOOO");
                        JSONArray objects = null;
                        try {
                            JSONObject parentObject = new JSONObject(response);
                            objects=parentObject.getJSONArray("MapsX");
                            for (int i = 0; i < objects.length(); i++) {
                                JSONObject session = objects.getJSONObject(i);
                                String s1= String.valueOf(session.get("xes"));
                                listx.add(s1);
                                String s2= String.valueOf(session.get("yes"));
                                listy.add(s2);
                                System.out.println(s1+" || "+s2);
                            }
                            adapter=new CustomAdapter(context,listx);
                            lv.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }}}, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
    }

    public void profile_page(View v) {
        if (v.getId() == R.id.profile_button) {
            Intent intentProfile = new Intent(Directory.this, Profile.class);
            startActivity(intentProfile);
        }
    }
   /* public void map1_page(View v){
        if (v.getId()== R.id.route1_button)
        {
            Intent intentGMap = new Intent(Directory.this, Map1.class);
            startActivity(intentGMap);
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_directory, menu);
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
