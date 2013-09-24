package ru.example.ursmu.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ru.example.ursmu.R;

public class SimpleCustomArrayAdapter<T> extends ArrayAdapter<String> {
    private int mResID;
    android.content.Context mContext;

    public SimpleCustomArrayAdapter(GroupScheduleActivity Context, int resId, String[] data) {
        super(Context, resId, data);
        mResID = resId;
        mContext = Context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(mResID, null);
        }

        TextView text = (TextView) v.findViewById(R.id.title_action_menu);
        text.setText(getItem(position));


        //TextView text2 = (TextView) v.findViewById(R.id.subtitle_action_menu);
        //text2.setText(position == 0 ? "Выбор группы из общего расписания" : "Ваша группа");

        return v;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.simple_action_menu_drop, null);
        }

        TextView title = (TextView) v.findViewById(R.id.title_drop_action_menu);
        String text = getItem(position);
        title.setText(text);

        ImageView image = (ImageView) v.findViewById(R.id.image_drop_menu_group_schedule);
        image.setImageDrawable(mContext.getResources().getDrawable(text.equals("Поиск") ? R.drawable.collections_view_as_list : R.drawable.rating_important));

        // TextView text2 = (TextView) v.findViewById(R.id.subtitle_drop_action_menu);
        //text2.setText(position == 0 ? "Выбор группы из общего расписания" : "Ваша группа");

        return v;
    }
}