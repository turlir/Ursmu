package ru.ursmu.application.Abstraction;

import android.database.Cursor;

import java.util.ArrayList;

public abstract class IDatabasingBehavior {

    public void add(ArrayList<?> week) {
    }


    public abstract void close();

    public abstract Object[] get(int limit);

    public Cursor get() {
        return null;
    }

    public boolean check() {
        return true;
    }

    public void clearTable() {
    }

    public abstract void update(ArrayList<Object> q);
}
