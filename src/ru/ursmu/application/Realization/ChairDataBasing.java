package ru.ursmu.application.Realization;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import ru.ursmu.application.Abstraction.IDatabasingBehavior;
import ru.ursmu.application.Activity.LocalDataBaseHelper;
import ru.ursmu.application.JsonObject.ChairItem;

import java.io.IOException;
import java.sql.SQLException;

public class ChairDataBasing extends IDatabasingBehavior {

    private static Context mContext;
    private static SQLiteDatabase mDataBase;
    private int mRandomCount = -1;
    private String mNameChair = null;
    private static final String RANDOM_SELECT = "SELECT * FROM department ORDER BY RANDOM() LIMIT ?";
    private static final String SPECIFY_SELECT = "SELECT * FROM department WHERE name = ?";

    public ChairDataBasing(int z) {
        mRandomCount = z;
    }

    public ChairDataBasing(String name) {
        mNameChair = name;
    }

    public static ChairDataBasing getInstance(Context c, int z) {
        if (mContext == null || mDataBase == null) {
            Log.d("URSMULOG", "GroupDataBasing getInstance");
            mContext = c;
            LocalDataBaseHelper temp = new LocalDataBaseHelper(mContext);
            try {
                temp.createDataBase();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                mDataBase = temp.openDataBase();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            mDataBase.setLockingEnabled(false);  //DB use only one thread
        }
        return new ChairDataBasing(z);
    }


    @Override
    public void close() {
        if (mDataBase != null) {
            mDataBase.setLockingEnabled(true);
            mDataBase.close();
            mDataBase = null;
        }
        if (mContext != null) {
            mContext = null;
        }
    }

    @Override
    public ChairItem[] getSchedule() {
        if (mRandomCount != -1) {
            return selectRandom();
        } else if (mNameChair != null) {
            return SelselectSpecify();
        }

        return null;
    }

    private ChairItem[] SelselectSpecify() {
        Cursor c = mDataBase.rawQuery(SPECIFY_SELECT,
                new String[]{mNameChair});
        return CursorToChairArray(c);
    }

    private ChairItem[] selectRandom() {

        Cursor c = mDataBase.rawQuery(RANDOM_SELECT,
                new String[]{String.valueOf(mRandomCount)});
        return CursorToChairArray(c);
    }

    private ChairItem[] CursorToChairArray(Cursor c) {
        int count = c.getCount();
        if (c.moveToFirst() && count != 0) {
            ChairItem[] array = new ChairItem[count];

            int j = 0;
            do {
                array[j] = new ChairItem(c);
                j++;
            } while (c.moveToNext());
            return array;


        } else {
            return null;
        }
    }
}
