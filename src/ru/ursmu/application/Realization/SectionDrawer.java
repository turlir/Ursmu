package ru.ursmu.application.Realization;

import ru.ursmu.application.Abstraction.DrawerItem;

public class SectionDrawer implements DrawerItem {
    private String mText;

    public SectionDrawer(String s) {
        mText = s;
    }

    @Override
    public boolean isSection() {
        return true;
    }

    public String getText() {
        return mText;
    }
}