package ru.ursmu.application.Abstraction;

public interface UniversalCallback {
    public void sendError(String notify);

    public void sendComplete(Object[] data);

    public void sendStart(long id);
}