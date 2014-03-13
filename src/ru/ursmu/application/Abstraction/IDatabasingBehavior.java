package ru.ursmu.application.Abstraction;

import android.database.Cursor;

import java.io.Serializable;

public abstract class IDatabasingBehavior {

    public void add(Object[] week) throws Exception {
        if (week==null)
            throw new Exception();
    }


    public abstract void close();

    public abstract Serializable getSchedule();

    public Cursor get() {
        return null;
    }

    public boolean check() {
        return true;
    }

    public void clearTable() {
    }

    public void update(Object[] q) throws Exception {
        if (q==null)
            throw new Exception();
    }
}
