package ru.ursmu.application.Abstraction;

import org.json.JSONException;

public abstract class IParserBehavior<T> {
    public T[] parse(String json) throws JSONException {
        return null;
    }
}
