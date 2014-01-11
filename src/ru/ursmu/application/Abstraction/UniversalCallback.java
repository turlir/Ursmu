package ru.ursmu.application.Abstraction;

import java.io.Serializable;

public abstract class UniversalCallback {
    public abstract void sendError(String notify);

    public abstract void sendComplete(Serializable data);

    public abstract void sendStart(long id);

    public void sendMiddle(String v) {

    }
}