package ru.ursmu.application.Realization;

import ru.ursmu.application.Abstraction.IParserBehavior;
import ru.ursmu.application.Abstraction.IUrsmuObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UrsmuNews implements IUrsmuObject {

    String mUri = "http://mobile.ursmu.ru/ajax";
    String mParam;

    public UrsmuNews(int pageNumber) {
       /* task	news
        page	2*/
        mParam = "task=news&page=" + Encode(String.valueOf(pageNumber));
    }

    private String Encode(String original) {
        String r = null;
        try {
            r = URLEncoder.encode(original, "utf-8");
        } catch (UnsupportedEncodingException e) {
        }
        return r;
    }

    @Override
    public String getUri() {
        return mUri;
    }

    @Override
    public String getParameters() {
        return mParam;
    }

    @Override
    public IParserBehavior getParseBehavior() {
        return new NewsParser();
    }
}