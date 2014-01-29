package ru.ursmu.application.Activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ru.ursmu.application.JsonObject.Faculty;
import ru.ursmu.beta.application.R;

public class FacultyAdapter extends ArrayAdapter<Faculty> {
    private final Typeface mTypefaceDesc;
    int mResId;

    public FacultyAdapter(Context context, int textViewResourceId, Faculty[] objects) {
        super(context, textViewResourceId, objects);
        mResId = textViewResourceId;

        mTypefaceDesc = Typeface.createFromAsset(context.getAssets(), "Roboto-Light.ttf");
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

            String name = item.getFullName();

            ImageView fc = (ImageView) v.findViewById(R.id.faculty_color);
            fc.setBackgroundColor(Color.parseColor(color));

            TextView fn = (TextView) v.findViewById(R.id.faculty_name);
            fn.setTypeface(mTypefaceDesc);
            fn.setText(name);
        }

        return v;
    }
}