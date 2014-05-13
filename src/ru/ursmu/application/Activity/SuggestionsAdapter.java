package ru.ursmu.application.Activity;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import ru.ursmu.application.R;

public class SuggestionsAdapter extends CursorAdapter {

    private int textIndex;
    private String mField;
    private int mImageRes;
    private Context mContext;

    public SuggestionsAdapter(Context context, Cursor c, String fieldName, int imageRes) {
        super(context, c, 0);
        mField = fieldName;
        mContext = context;
        mImageRes = imageRes;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.suggestion_adapter, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (cursor.getCount() > 0) {
            TextView text = (TextView) view.findViewById(R.id.suggestion_text);
            ImageView image = (ImageView) view.findViewById(R.id.suggestion_image);
            if (textIndex == 0) {
                textIndex = cursor.getColumnIndex(mField);
            }
            text.setText(cursor.getString(textIndex));
            image.setImageDrawable(mContext.getResources().getDrawable(mImageRes));
            ////Log.d("URSMULOG", image.getDrawable().getIntrinsicWidth() + "x" + image.getDrawable().getIntrinsicWidth());
        }
    }

    public String getString(int position) {
        getCursor().moveToPosition(position);
        return getCursor().getString(textIndex);
    }
}