package ru.example.ursmu.JsonObject;

import java.io.Serializable;

public class ListItem implements Serializable {
    private String mTitle;
    private String mImage;
    private String mDesc;
    private String mUri;


    public ListItem(String title, String image, String desc, String uri) {
        mTitle = title;
        mImage = image;
        mDesc = desc;
        mUri = uri;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getImage() {
        return "http://pressa.ursmu.ru/upload" + mImage;
    }

    public String getDesc() {
        return mDesc;
    }

    public String getUri() {
        return mUri + ".html";
    }
}