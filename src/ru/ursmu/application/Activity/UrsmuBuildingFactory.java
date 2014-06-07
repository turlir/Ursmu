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
                switch (build) {
                    case 1:
                        map_text = "ул.Куйбышева, 30";
                        lat =  56.825091;
                        lot = 60.596252;
                        break;
                    case 2:
                        map_text = "пер.Университетский, 9";
                        lat = 56.823777;
                        lot = 60.601772;
                        break;
                    case 3:
                        map_text = "ул.Хохрякова, 85";
                        lat = 56.827068;
                        lot = 60.595235;
                        break;
                    case 4:
                        map_text = "пер.Университетский, 7";
                        lat = 56.824187;
                        lot = 60.601676;
                        break;
                    default:
                        return null;
                }
                return new UrsmuBuilding(build, floor, audience, map_text, s, lat, lot);
            } else
                return null;

        } else
            return null;
    }
}
