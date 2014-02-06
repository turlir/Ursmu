package ru.ursmu.application.Realization;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.ursmu.application.Abstraction.IParserBehavior;
import ru.ursmu.application.JsonObject.EducationItem;

import java.util.ArrayList;
import java.util.Collections;

public class ScheduleParser extends IParserBehavior<EducationItem> {

    @Override
    public EducationItem[] parse(String json) throws JSONException {
        JSONArray week;
        ArrayList<EducationItem> coll = new ArrayList<EducationItem>(48);
        week = new JSONArray(json);
        JSONObject perv_para;
        JSONArray array_day;
        for (int i = 0; i < 7; i++) {    //1+7       //пара  //1+6

            perv_para = (JSONObject) week.get(i);

            for (int q = 0; q < 6; q++) {   // [0;6]          //день недели
                array_day = perv_para.getJSONArray(EducationItem.DayOfTheWeek[q]);
                EducationItem[] items = parseObject(array_day, q, i);
                if (items != null) {
                    Collections.addAll(coll, items);
                }
            }
        }
        return coll.toArray(new EducationItem[coll.size()]);
    }

    private EducationItem[] parseObject(JSONArray item, int day, int para) throws JSONException {
        switch (item.length()) {
            case 3:
                return new EducationItem[]{
                        new EducationItem(day, para + 1, item.getString(0), item.getString(1), item.getString(2))
                };
            case 6:
                //подряд - две пары одновременно
                return new EducationItem[]{
                        new EducationItem(day, para + 1, item.getString(0), item.getString(1), item.getString(4)),
                        new EducationItem(day, para + 1, item.getString(2), item.getString(3), item.getString(5))
                };
            default:
                return null;
        }
    }

}