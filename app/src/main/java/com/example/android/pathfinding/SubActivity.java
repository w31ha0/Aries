package com.example.android.pathfinding;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class SubActivity extends Activity implements SensorEventListener,View.OnTouchListener {
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    BroadcastReceiver mReceiver;
    InputStream is;
    /**
     * Called when the activity is first created.
     */
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private long lastUpdate, current2, last2 = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 2;
    boolean first, firstwalk, lock;
    int width, height, fx, fy, sx, sy;
    List<Integer> listx,listy;
    boolean walkingx, walkingy = false;
    float walkingspeedx, walkingspeedy;
    ArrayList<Line> lines;
    SurfaceHolder holder;
    Canvas canvas;
    View view;
    Paint paint;
    byte received;
    ConnectThread t;
    Context context;
    Bitmap bmp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bmp=BitmapFactory.decodeResource(this.getResources(),R.drawable.floor3);
        view = new MyView(this);
        setContentView(view);
        view.setOnTouchListener(this);
        first=true;
        listx=new ArrayList<Integer>();
        listy=new ArrayList<Integer>();
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener((SensorEventListener) this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        if (mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
        ParcelUuid[] uuids = null;
        Method getUuidsMethod = null;
        try {
            getUuidsMethod = BluetoothAdapter.class.getDeclaredMethod("getUuids", null);
            uuids = (ParcelUuid[]) getUuidsMethod.invoke(mBluetoothAdapter, null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


        //    for (UUID uuid: uuids) {
        //  System.out.println("UUID: " + uuid.getUuid().toString());
        //  this.uuid=uuid.getUuid();
        //  }
        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Add the name and address to an array adapter to show in a ListView
                    System.out.println("Found Device: " + device.getName());
                    if (device.getName().equals("raspberrypi")) {
                        Toast.makeText(context, "Raspberry pi deteced!",
                                Toast.LENGTH_LONG).show();
                        mBluetoothAdapter.cancelDiscovery();
                        t = new ConnectThread();
                        t.start();
                        t.connect(device, UUID.fromString("832e4374-95a3-41ac-ad85-f2312cca9c7a"),context);
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        mBluetoothAdapter.startDiscovery();
    }

    private void submit(final Context context){
        String URL="http://lewspage.hostei.com/scripts/insert.php?x=";
        Iterator<Integer> it =listx.iterator();
        while (it.hasNext())
            URL=URL+it.next().toString()+",";
        URL=URL+"&y=";
        it =listy.iterator();
        while (it.hasNext())
            URL=URL+it.next().toString()+",";
        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, "Successful!",
                                Toast.LENGTH_LONG).show();
                }}, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            System.out.println("OK");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        mBluetoothAdapter.cancelDiscovery();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction()==MotionEvent.ACTION_DOWN)
            submit(getApplicationContext());
        return false;
    }

    public class ConnectThread extends Thread {
        private BluetoothSocket bTSocket;

        public boolean connect(BluetoothDevice bTDevice, UUID mUUID,Context context) {
            System.out.println("Connecting");
            BluetoothSocket temp = null;
            try {
                temp = bTDevice.createRfcommSocketToServiceRecord(mUUID);
            } catch (IOException e) {
                Log.d("CONNECTTHREAD", "Could not create RFCOMM socket:" + e.toString());
                return false;
            }
            try {
                temp.connect();
            } catch (IOException e) {
                Log.d("CONNECTTHREAD", "Could not connect: " + e.toString());
                try {
                    temp = (BluetoothSocket) bTDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(bTDevice, 1);
                    temp.connect();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                } catch (NoSuchMethodException e1) {
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    try {
                        temp.close();
                    } catch (IOException close) {
                        Log.d("CONNECTTHREAD", "Could not close connection:" + e.toString());
                        return false;
                    }
                    e1.printStackTrace();
                }
            }
            Toast.makeText(context, "Connection established!",
                    Toast.LENGTH_LONG).show();
            try {
                is = temp.getInputStream();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            beginListenForData();
            return true;
        }
    }

    class Line {
        float startX, startY, stopX, stopY;
        public Line(float startX, float startY, float stopX, float stopY) {
            this.startX = startX;
            this.startY = startY;
            this.stopX = stopX;
            this.stopY = stopY;
        }

        public Line(float startX, float startY) { // for convenience
            this(startX, startY, startX, startY);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public class MyView extends SurfaceView implements Runnable {

        public MyView(Context context) {
            super(context);
            holder = getHolder();
            lines=new ArrayList<Line>();
            Thread t = new Thread(this);
            t.start();
            // TODO Auto-generated constructor stub
        }

        @Override
        public void run() {
            while (true) {
                if (!holder.getSurface().isValid())
                    continue;
                canvas = holder.lockCanvas();
                int x = getWidth();
                int y = getHeight();
                if (first){
                    fx=x/2;
                    fy=y/2;
                    first=false;
                }
                paint = new Paint();
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.WHITE);
                canvas.drawPaint(paint);
                paint.setColor(Color.parseColor("#CD5C5C"));
                paint.setStyle(Paint.Style.STROKE);
                paint.setTextSize(20);
                canvas.drawText(String.valueOf(received), 100, 100, paint);
                Rect rs = new Rect();
                Rect rd = new Rect();
                rs.left = rs.top = rd.left = rd.top = 0;
                rs.right = bmp.getWidth();
                rs.bottom = bmp.getHeight();
                rd.right = canvas.getWidth();
                rd.bottom = canvas.getHeight();
                canvas.drawBitmap(bmp, rs, rd, null);
                for (Line l : lines)
                    canvas.drawLine(l.startX,l.startY,l.stopX,l.stopY,paint);
                // Use Color.parseColor to define HTML colors
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float ax = sensorEvent.values[0];
            float ay = sensorEvent.values[1];
            float az = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();


            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;
                current2 = System.currentTimeMillis();
                float dx = ax - last_x;
                float dy = ay - last_y;
                float dz = az - last_z;


                float speedx = Math.abs(ax - last_x);
                float speedy = Math.abs(ay - last_y);
                float speedz = Math.abs(az - last_z);

                if (Math.abs(ay) > SHAKE_THRESHOLD) {
                    walkingy = true;
                    walkingspeedy = ay;
                    sy = fy;
                    sx = fx;
                    listx.add(sx);
                    listy.add(sy);
                }

                if (Math.abs(ax) > SHAKE_THRESHOLD) {
                    walkingx = true;
                    walkingspeedx = ax;
                    sy = fy;
                    sx = fx;
                    listx.add(sx);
                    listy.add(sy);
                }

                if (walkingy) {
                    if (firstwalk) {
                        current2 = 0;
                        last2 = System.currentTimeMillis();
                        firstwalk = false;
                    }
                    fy += walkingspeedy * 5;
                    String s = String.valueOf(ay);
                    //      tv2.setText(s);
                    //      tv3.setText("");
                }
                if (walkingx) {
                    if (firstwalk) {
                        current2 = 0;
                        last2 = System.currentTimeMillis();
                        firstwalk = false;
                    }
                    fx -= walkingspeedx * 5;
                    String s = String.valueOf(ax);
                    //      tv2.setText(s);
                    //      tv3.setText("");
                }
                if (current2 - last2 > 1500 && firstwalk == false) {
                    //      tv3.setText("Stopped");
                    System.out.print("Stopped");
                    walkingy = false;
                    walkingx = false;
                    listx.add(fx);
                    listy.add(fy);
                   if (sx != 0 && sy != 0 && lines!=null)
                        lines.add(new Line(sx, sy, fx, fy));
                    firstwalk=true;
                }
                last_x = ax;
                last_y = ay;
                last_z = az;
            }
        }
    }
    void beginListenForData()
    {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        Thread workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                boolean stopWorker = false;
                int readBufferPosition = 0;
                byte[] readBuffer = new byte[1024];
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = 1000;
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            is.read(packetBytes);
                            System.out.println("Received: ");
                            received=packetBytes[0];
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                byte[] encodedBytes = new byte[readBufferPosition];
                                System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                final String data = new String(encodedBytes, "US-ASCII");
                                readBufferPosition = 0;
                                System.out.println(b);

                                handler.post(new Runnable()
                                {
                                    public void run()
                                    {

                                    }
                                });
                                /*else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }*/
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });
        workerThread.start();
    }
}