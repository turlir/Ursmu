package ru.ursmu.application.Realization;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.ursmu.application.Abstraction.IParserBehavior;
import ru.ursmu.application.JsonObject.EducationItem;

import java.util.ArrayList;
import java.util.Collections;

public class ScheduleParser extends IParserBehavior<EducationItem> {

    @Override
    public ArrayList<EducationItem> parseTwo(String json) throws Exception {
        if (json==null) {
            throw new Exception();
        }
        JSONArray week = null;
        ArrayList<EducationItem> coll = new ArrayList<EducationItem>(48);
        try {
            week = new JSONArray(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject perv_para = null;
        JSONArray array_day = null;
        for (int i = 0; i < 7; i++) {    //1+7       //пара  //1+6


            try {
                perv_para = (JSONObject) week.get(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            for (int q = 0; q < 6; q++) {    //1+5          //день недели
                try {
                    array_day = perv_para.getJSONArray(EducationItem.DayOfTheWeek[q]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                EducationItem[] items = parseObject(array_day, q, i);
                if (items != null) {
                    Collections.addAll(coll, items);
                }
            }
        }
        return coll;
    }

    EducationItem[] parseObject(JSONArray item, int day, int para) {
        try {
            switch (item.length()) {
                case 3:
                    return new EducationItem[]{
                            new EducationItem(day, para + 1, item.getString(0), item.getString(1), item.getString(2))
                    };

                case 2:
                    return new EducationItem[]{
                            new EducationItem(day, para + 1, item.getString(0), item.getString(1), "")
                    };
                case 6:
                    //подряд - две пары одновременно
                    return new EducationItem[]{
                            new EducationItem(day, para + 1, item.getString(0), item.getString(1), item.getString(2)),
                            new EducationItem(day, para + 1, item.getString(3), item.getString(4), item.getString(5))
                    };
                case 5:
                    Log.d("URSMULOG", "parseObject 5");
                    return new EducationItem[]{
                            new EducationItem(day, para + 1, item.getString(0), item.getString(2), item.getString(4))
                    };
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

}