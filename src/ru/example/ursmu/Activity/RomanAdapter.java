package ru.example.ursmu.Activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ru.example.ursmu.JsonObject.RomanNumeral;
import ru.example.ursmu.R;

public class RomanAdapter extends ArrayAdapter<String> {
    Context mContext;
    int mResId;
    String[] mData;

    public RomanAdapter(FindKursActivity context, int adapter_id_layout, String[] data) {
        super(context, adapter_id_layout, data);

        //mContext = context;
        mResId = adapter_id_layout;
        //mData = data;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(mResId, null);
        }

        String item = new RomanNumeral(Integer.parseInt(getItem(position))).toString();

        if (!item.isEmpty()) {
            TextView textRoman = (TextView) v.findViewById(R.id.kursItem);
            textRoman.setText(item);
        }


        return v;
    }


}