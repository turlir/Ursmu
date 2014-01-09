package ru.ursmu.application.Activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ru.ursmu.beta.application.R;

public class ExtendedGroupAdapter extends ArrayAdapter<String> {
    Context mContext;
    int mResId;
    String[] mData;

    private static final String PATTERN_1 = "(\\d+)";  //GB-12 GHB-12

    public ExtendedGroupAdapter(FindGroupActivity context, int layoutID, String[] data) {
        super(context, layoutID, data);
        mContext = context;
        mResId = layoutID;
        mData = data;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(mResId, null);
        }

        String original = getItem(position);
        TextView gr_name = (TextView) v.findViewById(R.id.groupItem);
/*      Pattern pattern = Pattern.compile(PATTERN_1);

        Matcher matcher = pattern.matcher(original);
        String temp = "";
        if (matcher.find()) {
            for (int i = 0; i <= matcher.groupCount(); i++) {
                temp = original.replace(matcher.group(i), new RomanNumeral(Integer.parseInt(matcher.group(i))).toString());
            }
        }*/

        gr_name.setText(original); //temp

        return v;
    }
}