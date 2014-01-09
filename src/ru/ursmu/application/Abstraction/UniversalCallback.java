package ru.ursmu.application.Abstraction;

import java.io.Serializable;

public interface UniversalCallback {
    public void sendError(String notify);

    public void sendComplete(Serializable data);

    public void sendStart(long id);
}