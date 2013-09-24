package ru.example.ursmu.Realization;

import ru.example.ursmu.Abstraction.IParserBehavior;
import ru.example.ursmu.JsonObject.ListItem;
import org.json.JSONException;
import org.json.JSONObject;

public class GornyVestiParser extends IParserBehavior<ListItem> {
    @Override
    public ListItem[] parse(String json) throws JSONException {
        JSONObject j_object = new JSONObject(json);
        int c = j_object.getInt("count");
        if (c != 0) {
            ListItem[] array = new ListItem[c];
            JSONObject item;
            String title, desc, image, uri;

            for (int i = 0; i < c; i++) {
                item = j_object.getJSONObject(String.valueOf(i));
                title = item.getString("title");
                image = item.getString("img_index");
                desc = item.getString("desc");
                uri = "http://pressa.ursmu.ru/video/" + item.getString("id");

                array[i] = new ListItem(title, image, desc, uri);
            }

            return array;
        } else {
            return null;
        }
    }
}