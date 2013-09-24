package ru.example.ursmu.Abstraction;

import org.json.JSONException;

import java.util.ArrayList;

public abstract class IParserBehavior<T> {
    public T[] parse(String json) throws JSONException {
        return null;
    }

    public ArrayList<T> parseTwo(String json) {
        return null;
    }
}
