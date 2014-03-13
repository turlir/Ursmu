package ru.ursmu.application.Realization;

import ru.ursmu.application.JsonObject.Faculty;

public class FacultyFactory {

    public static String[] mFullName = new String[]{
            "Горно-Механический",
            "Горно-технологический",
            "Инженерно-экономический",
            "Геологии и геофизики",
            "Гражданской защиты",
            "Среднего профессионального образования",
            "Заочного обучения",
            "ФСПО заочники",
            "Институт Сокращенной подготовки",
            "Институт мировой экономики"
    };

    public static String[] mShortName = new String[]{
            "гмф",
            "гтф",
            "иэф",
            "фгиг",
            "фгз",
            "фспо",
            "фзо",
            "фспоз",
            "исп",
            "имэ"
    };

    public static String[] mColor = new String[]{
            "#046ab4",
            "#a50f20",
            "#dcb900",
            "#1e8333",
            "#3c6134",
            "#845c40",
            "#99854e",
            "#845c40",
            "#cd873a",
            "#ffea7b"
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