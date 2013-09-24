package ru.example.ursmu.Realization;

import android.util.Log;
import ru.example.ursmu.Abstraction.IParserBehavior;
import ru.example.ursmu.JsonObject.ListItem;
import org.json.JSONException;
import org.json.JSONObject;

public class NewsParser extends IParserBehavior<ListItem> {

    @Override
    public ListItem[] parse(String json) throws JSONException {
        Log.d("URSMULOG", "NewsParser start");

        JSONObject jsonArray = new JSONObject(json);
        int count = jsonArray.length();  //21
        ListItem[] array = new ListItem[count - 2];

        String title;
        String image;
        String desc;
        String url;

        JSONObject item;
        for (int i = 0; i < count - 2; i++) {     //19          [0;20)
            item = jsonArray.getJSONObject(String.valueOf(i));
            title = item.getString("title");
            image = item.getString("img_index");
            desc = item.getString("desc");
            url = "http://pressa.ursmu.ru/" + item.getString("id");

            array[i] = new ListItem(title, image, desc, url);
        }

        return array;
    }
}