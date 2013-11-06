package ru.ursmu.application.Realization;

import ru.ursmu.application.JsonObject.Faculty;

public class FacultyFactory {

    static String[] mFullName = new String[]{
            "Горно-Механический",
            "Горно-технологический",
            "Инженерно-экономический",
            "Геологии и геофизики",
            "Гражданской защиты",
            "Среднего профессионального образования",
            "Заочного обучения",
            "ФСПО заочники",
            "Институт Сокращенной подготовки"
    };

    static String[] mShortName = new String[]{
            "гмф",
            "гтф",
            "иэф",
            "фгиг",
            "фгз",
            "фспо",
            "фзо",
            "фспоз",
            "исп"
    };

    static String[] mColor = new String[]{
            "#007ab2",
            "#a12331",
            "#c3952a",
            "#00a871",
            "#387b45",
            "#9e6544",
            "#907534",
            "#9e6544",
            "#bd62b3"
    };

    public static Faculty create(String value) {
        if ((mFullName.length + mShortName.length + mColor.length) % 3 != 0)  //exception common white color
            throw new IllegalArgumentException("(mFullName.length + mShortName.length + mColor.length) % 3 != 0");
        String v = value.toLowerCase();
        for (int i = 0; i < mShortName.length; i++) {
            if (v.equals(mShortName[i])) {
                return new Faculty(mFullName[i], value, mColor[i]);
            }
        }

        return new Faculty(value, value, "#ffffff");
    }


}