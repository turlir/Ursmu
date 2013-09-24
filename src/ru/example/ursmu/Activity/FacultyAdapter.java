package ru.example.ursmu.Activity;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ru.example.ursmu.R;
import ru.example.ursmu.JsonObject.Faculty;

public class FacultyAdapter extends ArrayAdapter<Faculty> {
    Context mContext;
    int mResId;
    Faculty[] mData;

    public FacultyAdapter(Context context, int textViewResourceId, Faculty[] objects) {
        super(context, textViewResourceId, objects);
        mContext = context;
        mResId = textViewResourceId;
        mData = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(mResId, null);
        }

        Faculty item = getItem(position);

        if (item != null) {
            String color = item.getColor();

            String temp = item.getFullName();
            String name = (temp.isEmpty() ? item.getOriginalName() : temp);
            temp = null;

            ImageView fc = (ImageView) v.findViewById(R.id.faculty_color);
            fc.setBackgroundColor(Color.parseColor(color));

            TextView fn = (TextView) v.findViewById(R.id.faculty_name);
            fn.setText(name);
        }

        return v;
    }


//    @Override
//    public Faculty getItem(int position) {
//        return mData[position];
//    }


}