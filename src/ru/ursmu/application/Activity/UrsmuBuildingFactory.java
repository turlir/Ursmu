package ru.ursmu.application.Activity;

import android.content.Context;
import android.text.TextUtils;

public class UrsmuBuildingFactory {
    public static UrsmuBuilding get(String s, Context c) {
        if (s.length() >= 3 && s.length() <= 5 && !TextUtils.isEmpty(s)) {
            Integer floor = null;
            String audience = null;
            Integer build = null;
            try {
                build = Integer.parseInt(String.valueOf(s.charAt(0)));          // здание
                floor = Integer.parseInt(String.valueOf(s.charAt(1)));          // этаж
                audience = s.substring(2);                                      // аудитория
            } catch (Exception e) {
                return null;
            }

            if (build > 0 && build < 5 && floor < 1000 && audience != null && audience.length() < 4) {
                switch (build) {
                    case 1:
                        return new UrsmuBuilding(1, floor, audience, "ул.Куйбышева, 30", s);
                    case 2:
                        return new UrsmuBuilding(2, floor, audience, "пер.Университетский, 9", s);
                    case 3:
                        return new UrsmuBuilding(3, floor, audience, "ул.Хохрякова, 85", s);
                    case 4:
                        return new UrsmuBuilding(4, floor, audience, "пер.Университетский, 7", s);
                    default:
                        return null;
                }
            } else
                return null;

        } else
            return null;
    }
}
