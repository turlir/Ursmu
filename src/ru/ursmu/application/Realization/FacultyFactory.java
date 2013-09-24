package ru.ursmu.application.Realization;

import ru.ursmu.application.JsonObject.Faculty;

public class FacultyFactory {

    static String[] mFullName = new String[]{"Горно-Механический",
            "Горно-технологический",
            "Инженерно-экономический",
            "Геологии и геофизики",
            "Гражданской защиты",
            "Среднего профессионального образования",
            "Заочного обучения"};

    static String[] mShortName = new String[]{"гмф",
            "гтф",
            "иэф",
            "фгиг",
            "фгз",
            "фспо",
            "фзо"};

    static String[] mColor = new String[]{"#007ab2", "#a12331", "#c3952a", "#00a871", "#387b45", "#9e6544", "#907534", "#FFFFFF"};

//    public static Faculty create(String value) {
//        Faculty f = null;
//        String v = value.toLowerCase();
//
//        if (v.equals("гмф")) {
//            f = new Faculty("Горно-Механический", value, "#007ab2");
//            return f;
//        } else if (v.equals("гтф")) {
//            f = new Faculty("Горно-технологический", value, "#a12331");
//            return f;
//        } else if (v.equals("иэф")) {
//            f = new Faculty("Инженерно-экономический", value, "#c3952a");
//            return f;
//        } else if (v.equals("фгиг")) {
//            f = new Faculty("Геологии и геофизики", value, "#00a871");
//            return f;
//        } else if (v.equals("фгз")) {
//            f = new Faculty("Гражданской защиты", value, "#387b45");
//            return f;
//        } else if (v.equals("фспо")) {
//            f = new Faculty("Среднего профессионального образования", value, "#9e6544");
//            return f;
//        } else if (v.equals("фзо")) {
//            f = new Faculty("Заочного обучения", value, "#907534");
//        } else if (f == null) {
//            f = new Faculty("", value, "#FFFFFF");  //white
//        }
//
//        return f;
//    }

    public static Faculty create(String value) {
        if ((mFullName.length + mShortName.length + (mColor.length - 1)) % 3 != 0)  //exception common white color
            throw new IllegalArgumentException("(mFullName.length + mShortName.length + (mColor.length - 1)) % 3 != 0");
        String v = value.toLowerCase();
        for (int i = 0; i < mShortName.length; i++) {
            if (v.equals(mShortName[i])) {
                return new Faculty(mFullName[i], value, mColor[i]);
            }
        }

        return new Faculty("", value, mColor[mShortName.length]);
    }


    public static String toShortName(int full) {
        return mShortName[full];
    }

}