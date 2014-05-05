package ru.ursmu.application.Activity;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ru.ursmu.application.Abstraction.DrawerItem;
import ru.ursmu.application.Realization.EntryDrawer;
import ru.ursmu.application.Realization.SectionDrawer;
import ru.ursmu.beta.application.R;

import java.util.ArrayList;


public class SlideMenuAdapter extends ArrayAdapter<DrawerItem> {
    private final static int RES_ID = R.layout.drawer_list_item;
    private LayoutInflater maLayoutService;
    private final Typeface mRegular, mLight;

    public SlideMenuAdapter(Context context, ArrayList<DrawerItem> mData) {
        super(context, RES_ID, mData);
        maLayoutService = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLight = Typeface.createFromAsset(context.getAssets(), "Roboto-Light.ttf");
        mRegular = Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");
    }

    public View getView(int position, View v, ViewGroup parent) {
        DrawerItem item = getItem(position);

        if (item.isSection()) {
            SectionDrawer s = (SectionDrawer) item;
            v = maLayoutService.inflate(R.layout.section_drawer, null);
            v.setOnClickListener(null);
            v.setOnLongClickListener(null);
            v.setLongClickable(false);
            TextView section = (TextView) v.findViewById(R.id.section_drawer_textView);
            section.setTypeface(mRegular);
            section.setText(s.getText());
        } else {
            EntryDrawer d = (EntryDrawer) item;
            v = maLayoutService.inflate(R.layout.drawer_list_item, null);
            TextView item_text = (TextView) v.findViewById(R.id.slide_item_text);
            item_text.setTypeface(mLight);
            item_text.setText(d.getText());
            ((ImageView) v.findViewById(R.id.slide_item_image)).setImageDrawable(d.getDrawable(getContext()));
        }

        return v;
    }
}

