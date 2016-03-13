package com.example.android.pathfinding;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by Lew on 13/3/2016.
 */
public class CustomAdapter extends ArrayAdapter {
    public CustomAdapter(Context context, List<String> list) {
        super(context, R.layout.custom_view,list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=LayoutInflater.from(getContext());
        View customView=inflater.inflate(R.layout.custom_view,parent,false);

        return customView;
    }
}
