package ru.ursmu.application.Realization;

import android.util.Log;
import ru.ursmu.application.Abstraction.IParserBehavior;
import org.json.JSONArray;
import org.json.JSONException;

public class JsonArrayParser extends IParserBehavior<String> {
    @Override
    public String[] parse(String json) throws JSONException {
        //Log.d("URSMULOG", "JsonArrayParser start");
        return parseArray(json);
    }

    private String[] parseArray(String result) throws JSONException {
        JSONArray jsonArray = new JSONArray(result);

        String[] ok = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            ok[i] = jsonArray.getString(i);
        }
        //Log.d("URSMULOG", "JsonArrayParser stop");
        return ok;
    }

}