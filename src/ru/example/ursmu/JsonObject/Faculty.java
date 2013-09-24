package ru.example.ursmu.JsonObject;

public class Faculty {
    private String mName;
    private String mShortName;
    private String mColor;


    public Faculty(String name, String originalName, String color) {
        mName = name;
        mShortName = originalName;
        mColor = color;
    }

    public String getFullName() {
        return mName;
    }

    public String getOriginalName() {
        return mShortName;
    }

    public String getColor() {
        return mColor;   //hex!
    }
}