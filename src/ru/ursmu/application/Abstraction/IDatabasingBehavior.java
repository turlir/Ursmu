package ru.ursmu.application.Abstraction;

import android.database.Cursor;
import ru.ursmu.application.JsonObject.EducationItem;
import ru.ursmu.application.Realization.EducationWeek;

import java.util.ArrayList;

public abstract class IDatabasingBehavior {

    public void add(ArrayList<?> week) throws Exception {
        if (week==null)
            throw new Exception();
    }


    public abstract void close();

    public abstract EducationWeek getSchedule();

    public Cursor get() {
        return null;
    }

    public boolean check() {
        return true;
    }

    public void clearTable() throws Exception {
    }

    public void update(ArrayList<Object> q) throws Exception {
        if (q==null)
            throw new Exception();
    }
}
