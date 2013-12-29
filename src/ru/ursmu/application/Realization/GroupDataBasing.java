package ru.ursmu.application.Realization;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import ru.ursmu.application.Abstraction.IDatabasingBehavior;
import ru.ursmu.application.Activity.DataBaseHelper;
import ru.ursmu.application.JsonObject.EducationItem;

import java.util.ArrayList;
import java.util.UUID;

public class GroupDataBasing extends IDatabasingBehavior {


    String mFaculty, mKurs, mGroup;

    private static Context mContext;
    private static SQLiteStatement common;
    private static SQLiteStatement day;
    private static SQLiteDatabase mDataBase;
    private static String mQueryLimit;
    private static String mQueryNonLimit;

    public static GroupDataBasing getInstance(Context c, String f, String k, String g) {
        if (mContext == null || mDataBase == null) {
            Log.d("URSMULOG", "GroupDataBasing getInstance");
            mContext = c;
            DataBaseHelper dbHelper = new DataBaseHelper(mContext);
            mDataBase = dbHelper.getWritableDatabase();
            mDataBase.setLockingEnabled(false);  //DB use only one thread
            try {
                common = mDataBase.compileStatement("INSERT INTO ScheduleCommon VALUES(?, ?, ?, ?, ?)");
                day = mDataBase.compileStatement("INSERT INTO ScheduleDays VALUES(?,?,?,?,?,?,?,?)");
            } catch (SQLException e) {
                Log.d("URSMULOG", "compileStatement error");
            }

            StringBuilder query_limit = new StringBuilder();
            query_limit.append("SELECT ScheduleDays.Name,");
            query_limit.append("ScheduleDays.teacher,");
            query_limit.append("ScheduleDays.room,");
            query_limit.append("ScheduleDays.numerPair,");
            query_limit.append("ScheduleDays.DayIndex");
            query_limit.append(" FROM ScheduleCommon, ScheduleDays WHERE (ScheduleCommon.UIN = ScheduleDays.GroupID)");
            query_limit.append("AND (ScheduleCommon.UIN = ");
            query_limit.append("?");
            query_limit.append(")");
            query_limit.append("AND (ScheduleDays.DayIndex = ");
            query_limit.append("?");
            query_limit.append(")");
            //mQuery.append("AND (ScheduleDays.room !='')");          //show empty pair?
            query_limit.append("ORDER BY ScheduleDays.numerPair");
            mQueryLimit = query_limit.toString();

            StringBuilder query_non_limit = new StringBuilder();
            query_non_limit.append("SELECT ScheduleCommon.GroupName, ScheduleCommon._id, ScheduleCommon.FACULTY, ScheduleCommon.KURS");
            query_non_limit.append(" FROM ScheduleCommon");
            query_non_limit.append(" WHERE ");
            query_non_limit.append(" (ScheduleCommon.GroupName LIKE ?)");
            query_non_limit.append(" GROUP BY ScheduleCommon.GroupName");
            mQueryNonLimit = query_non_limit.toString();


        }
        return new GroupDataBasing(f, k, g);
    }

    private GroupDataBasing(String faculty, String kurs, String group) {
        mGroup = group;
        mFaculty = faculty;
        mKurs = kurs;
    }

    @Override
    public void add(ArrayList<?> week) {
        ArrayList<EducationItem> temp = (ArrayList<EducationItem>) week;
        Long id = Math.abs(UUID.randomUUID().getLeastSignificantBits());

        Log.d("URSMULOG", mFaculty + " " + mKurs + " " + mGroup);

        mDataBase.beginTransaction();

        common.bindString(2, mFaculty);
        common.bindString(3, mKurs);
        common.bindString(4, mGroup);
        common.bindLong(5, id);
        common.executeInsert();


        for (EducationItem item : temp) {
            //Log.d("URSMULOG", "add " + item.getDayOfTheWeek());
            day.bindLong(2, id);
            day.bindString(3, item.getPredmet());
            day.bindString(4, item.getProfessor());
            day.bindString(5, item.getAud());
            day.bindLong(6, item.getDayOfTheWeek());
            day.bindString(7, item.getNormalProfessor());
            day.bindLong(8, item.getNumberPar());
            day.executeInsert();
        }
        mDataBase.setTransactionSuccessful();
        mDataBase.endTransaction();

    }

    @Override
    public void close() {
        mUIN = null;
        mLastGroup = null;
        if (mDataBase != null) {
            mDataBase.setLockingEnabled(true);
            //mDataBase.rawQuery("PRAGMA foreign_keys = ON", null);
            mDataBase.close();
            mDataBase = null;
        }
        if (common != null) {
            common.close();
            common = null;
        }
        if (day != null) {
            day.close();
            day = null;
        }
        if (mContext != null) {
            mContext = null;
        }
    }

    @Override
    public Cursor get() {
        return mDataBase.rawQuery(mQueryNonLimit, new String[]{"%" + mGroup + "%"});
    }

    private static Long mUIN = null;
    private static String mLastGroup = "";

    @Override
    public Object[] get(int limit) {
        if (mUIN == null || !mGroup.equals(mLastGroup)) {
            mLastGroup = mGroup;
            mUIN = getUINCurrentGroup();
            Log.d("URSMULOG", "getUINCurrentGroup" + mUIN);
        }

        return getGroupSchedule(limit, mUIN);
    }

    private Long getUINCurrentGroup() {
        Cursor c = mDataBase.rawQuery("SELECT ScheduleCommon.UIN FROM ScheduleCommon WHERE (ScheduleCommon.GroupName = ?)",
                new String[]{mGroup});
        int uin_index = c.getColumnIndex("UIN");
        c.moveToFirst();
        Long uin = c.getLong(uin_index);
        c.close();
        return uin;
    }

    @Override
    public void update(ArrayList<Object> q) {
        if (check()) {
            Cursor c = mDataBase.rawQuery("SELECT ScheduleCommon.UIN FROM ScheduleCommon WHERE (ScheduleCommon.GroupName = ?)",
                    new String[]{mGroup});
            c.moveToFirst();
            mUIN = c.getLong(c.getColumnIndexOrThrow("UIN"));
            c.close();

            mDataBase.delete("ScheduleCommon", "UIN=?", new String[]{String.valueOf(mUIN)});
            mDataBase.delete("ScheduleDays", "GroupID=?", new String[]{String.valueOf(mUIN)});
        }
        add(q);
    }

    @Override
    public boolean check() {
        Cursor c = mDataBase.rawQuery("SELECT COUNT(ScheduleCommon.UIN) FROM ScheduleCommon WHERE (ScheduleCommon.GroupName = '" + mGroup + "')", new String[]{});
        if (c!=null) {
            c.moveToFirst();
            int count = c.getInt(0);
            c.close();
            return (count > 0);
        } else
            return false;
    }

    @Override
    public void clearTable() {
        if (!mDataBase.isDbLockedByCurrentThread()) {
        Log.d("URSMULOG", "GroupDataBasing clearTable");
        mDataBase.delete("ScheduleCommon", "", new String[]{});
        mDataBase.delete("ScheduleDays", "", new String[]{});

        mDataBase.close();
        } else {
            Log.d("URSMULOG", "GroupDataBasing clearTable mDataBase.isDbLockedByCurrentThread() " + mDataBase.isDbLockedByCurrentThread());
        }
    }


    public Object[] getGroupSchedule(int limit, Long uin) {
        //Long t = System.currentTimeMillis();
        Cursor data = mDataBase.rawQuery(mQueryLimit, new String[]{String.valueOf(uin), String.valueOf(limit)});
        //Log.d("URSMULOG ", "TIME SELECT " + limit + "" + (System.currentTimeMillis() - t));
        int count = data.getCount();

        if (count != 0) {
            EducationItem[] arr = new EducationItem[count];
            data.moveToFirst();
            for (int i = 0; i < count; i++) {
                arr[i] = new EducationItem(data, false);
                data.moveToNext();
            }
            //Log.d("URSMULOG ", "TIME PARSE " + (System.currentTimeMillis() - t));
            data.close();
            return arr;
        } else {
            data.close();
            return null;
        }
    }


}