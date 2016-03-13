package com.example.android.pathfinding;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Wei Hao on 1/7/2016.
 */
public class VolleySingleton {

    private static VolleySingleton sInstance=null;

    private RequestQueue mRequestQueue;

    private VolleySingleton(){
        mRequestQueue= Volley.newRequestQueue(MyApplication.getAppContext());
    }

    public  static VolleySingleton getsInstance(){
        if (sInstance==null){
            sInstance=new VolleySingleton();
        }
        return sInstance;
    }

    public RequestQueue getmRequestQueue(){
        return mRequestQueue;
    }

}
