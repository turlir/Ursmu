package ru.ursmu.application.Realization;

import ru.ursmu.application.Abstraction.IParserBehavior;
import ru.ursmu.application.Abstraction.IUrsmuObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class KursList implements IUrsmuObject {

    private StringBuilder mParam = new StringBuilder("task=kurs" + "&fak=");


    public KursList(String kur) {
        mParam.append(Encode(kur));
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
        return SERVER_1;
    }

    @Override
    public String getParameters() {
        return mParam.toString();
    }

    @Override
    public IParserBehavior getParseBehavior() {
        return new JsonArrayParser();
    }
}