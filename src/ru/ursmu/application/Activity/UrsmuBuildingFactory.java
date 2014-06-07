package ru.ursmu.application.Activity;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.text.TextUtils;
import ru.ursmu.beta.application.R;

public class UrsmuBuildingFactory implements UrsmuHouseInfoCreator {
    private Context mContext;
    public UrsmuBuildingFactory(Context c) {
        mContext = c;
    }

    public UrsmuBuilding get(String s) {
        if (s.length() >= 3 && s.length() <= 5 && !TextUtils.isEmpty(s)) {
            Integer floor;
            String audience;
            Integer build;
            try {
                build = Integer.parseInt(String.valueOf(s.charAt(0)));          // здание
                floor = Integer.parseInt(String.valueOf(s.charAt(1)));          // этаж
                audience = s.substring(2);                                      // аудитория
            } catch (Exception e) {
                return null;
            }

            if (build > 0 && build < 5 && floor < 1000 && audience.length() < 4) {
                Resources res = getContext().getResources();
                TypedArray coord = res.obtainTypedArray(R.array.coordinates);
                if (coord == null) return null;

                int base = (build - 1) * 4;
                String map_text = coord.getString(base);
                double lat = coord.getFloat(base + 1, 56.826360f);
                double lot = coord.getFloat(base + 2, 60.595759f);
                float angle = coord.getFloat(base + 3, 175f);

                return new UrsmuBuilding(build, floor, audience, map_text, s, lat, lot, angle);
            } else
                return null;

        } else
            return null;
    }

    @Override
    public Context getContext() {
        return mContext;
    }
}
