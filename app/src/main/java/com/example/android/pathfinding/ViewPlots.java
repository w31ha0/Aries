package com.example.android.pathfinding;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Lew on 13/3/2016.
 */
public class ViewPlots extends Activity {
    List<String> listx,listy;
    List<Integer> lx,ly;
    String x,y;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args=getIntent().getExtras();
        String x=args.getString("X");
        String y=args.getString("Y");
        System.out.println("X:"+x);
        System.out.println("Y:"+y);
        lx=new ArrayList<>();
        ly=new ArrayList<>();
        listx = Arrays.asList(x.split(","));
        listy = Arrays.asList(y.split(","));
        Iterator<String> it=listx.iterator();
        while(it.hasNext())
            lx.add(Integer.parseInt(it.next()));
        it=listy.iterator();
        while(it.hasNext())
            ly.add(Integer.parseInt(it.next()));
        System.out.println("LISTX:"+lx);
        System.out.println("LISTY:"+ly);
        setContentView(new MyView(this));
    }

    public class MyView extends View {

        public MyView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Paint paint=new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            canvas.drawPaint(paint);
            paint.setColor(Color.parseColor("#CD5C5C"));
            paint.setStyle(Paint.Style.STROKE);
            paint.setTextSize(20);
            Iterator<Integer> it=lx.iterator();
            Iterator<Integer> it2=ly.iterator();
            int sx,sy,fx,fy;
            sx=canvas.getWidth()/2;
            sy=canvas.getHeight()/2;
            while (it.hasNext() && it2.hasNext()){
                fx=it.next();fy=it2.next();
                canvas.drawLine(sx,sy,fx,fy,paint);
                sx=fx;sy=fy;
            }
        }
    }

}
