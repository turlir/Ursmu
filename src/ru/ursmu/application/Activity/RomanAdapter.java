package ru.ursmu.application.Activity;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ru.ursmu.application.JsonObject.RomanNumeral;
import ru.ursmu.application.R;

public class RomanAdapter extends ArrayAdapter<String> {
    private final Typeface mTypefaceDesc;
    int mResId;

    public RomanAdapter(Context context, int adapter_id_layout, String[] data) {
        super(context, adapter_id_layout, data);

        mResId = adapter_id_layout;
        mTypefaceDesc = Typeface.createFromAsset(context.getAssets(), "Roboto-Light.ttf");
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(mResId, null);
        }

        String item;
        try {
            item = new RomanNumeral(Integer.parseInt(getItem(position))).toString();
        } catch (NumberFormatException e) {
            item = getItem(position);
        }

        TextView textRoman = (TextView) v.findViewById(R.id.kursItem);
        textRoman.setTypeface(mTypefaceDesc);
        textRoman.setText(item);


        return v;
    }


}