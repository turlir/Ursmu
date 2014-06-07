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

            if (build > 0 && build < 5 && floor < 1000 && audience.length() < 4) {
                String map_text;
                double lat, lot;
                float angle = 0;
                switch (build) {
                    case 1:
                        map_text = "ул.Куйбышева, 30";
                        lat = 56.826360;
                        lot = 60.595759;
                        angle = 175f;
                        break;
                    case 2:
                        map_text = "пер.Университетский, 9";
                        lat = 56.82376;
                        lot = 60.601447;
                        angle = 79.43f;
                        break;
                    case 3:
                        map_text = "ул.Хохрякова, 85";
                        lat = 56.826752;
                        lot = 60.594949;
                        angle = 80.86f;
                        break;
                    case 4:
                        map_text = "пер.Университетский, 7";
                        lat = 56.824202;
                        lot = 60.601266;
                        angle = 65.78f;
                        break;
                    default:
                        return null;
                }
                return new UrsmuBuilding(build, floor, audience, map_text, s, lat, lot, angle);
            } else
                return null;

        } else
            return null;
    }
}
