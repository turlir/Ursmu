package ru.ursmu.application.Realization;

import android.content.Context;
import android.graphics.drawable.Drawable;
import ru.ursmu.application.Abstraction.DrawerItem;

public class EntryDrawer implements DrawerItem {
    private String mText;
    private int mResId;

    public EntryDrawer(String s, int id) {
        mText = s;
        mResId = id;
    }

    @Override
    public boolean isSection() {
        return false;
    }

    public String getText() {
        return mText;
    }

    public Drawable getDrawable(Context c) {
        return c.getResources().getDrawable(mResId);
    }
}
