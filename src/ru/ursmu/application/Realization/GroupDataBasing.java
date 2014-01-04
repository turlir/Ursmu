package ru.ursmu.application.Realization;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Debug;
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

        mUIN = id;
        mLastGroup = mGroup;
    }

    @Override
    public void close() {
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
    private static String mLastGroup = null;

    @Override
    public Object[] get(int limit) {
        //Debug.startMethodTracing("GroupDataBasing get");
            getUINCurrentGroup();
        //Debug.stopMethodTracing();
        return getGroupSchedule(limit);
    }

    private void getUINCurrentGroup() {
        if (mUIN != null && mGroup.equals(mLastGroup)) {
           return;
        }
        Cursor c = mDataBase.rawQuery("SELECT ScheduleCommon.UIN FROM ScheduleCommon WHERE (ScheduleCommon.GroupName = ?)", new String[]{mGroup});
        try {
            int uin_index = c.getColumnIndexOrThrow("UIN");
            if (c.moveToFirst()) {
                Long uin = c.getLong(uin_index);
                c.close();
                mUIN = uin;
                Log.d("URSMULOG", "getUINCurrentGroup()" + mUIN);
                mLastGroup = mGroup;
                Log.d("URSMULOG", "getUINCurrentGroup() mLastGroup" + mLastGroup);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(ArrayList<Object> q) {
        if (check()) {
            if (mUIN != null) {
                mDataBase.delete("ScheduleCommon", "UIN=?", new String[]{String.valueOf(mUIN)});
                mDataBase.delete("ScheduleDays", "GroupID=?", new String[]{String.valueOf(mUIN)});
            } else {
                Log.d("URSMULOG", "GroupDataBasing update mUIN != null");
            }
            mUIN = null;
            mLastGroup = null;
        }
        add(q);
    }

    @Override
    public boolean check() {
        getUINCurrentGroup();
        return (mUIN != null);
    }

    @Override
    public void clearTable() {
        if (!mDataBase.isDbLockedByCurrentThread()) {
            Log.d("URSMULOG", "GroupDataBasing clearTable");
            mDataBase.delete("ScheduleCommon", "", new String[]{});
            mDataBase.delete("ScheduleDays", "", new String[]{});

            mDataBase.close();
            mUIN = null;
            mLastGroup = null;
        } else {
            Log.d("URSMULOG", "GroupDataBasing clearTable mDataBase.isDbLockedByCurrentThread() " + mDataBase.isDbLockedByCurrentThread());
        }
    }


    public Object[] getGroupSchedule(int limit) {
        //Debug.startMethodTracing("GroupDataBasing getGroupSchedule");
        Cursor data = mDataBase.rawQuery(mQueryLimit, new String[]{String.valueOf(mUIN), String.valueOf(limit)});
        int count = data.getCount();

        if (count != 0) {
            EducationItem[] arr = new EducationItem[count];
            data.moveToFirst();
            for (int i = 0; i < count; i++) {
                arr[i] = new EducationItem(data, false);
                data.moveToNext();
            }
            data.close();
            //Debug.stopMethodTracing();
            return arr;
        } else {
            data.close();
            //Debug.stopMethodTracing();
            return null;
        }
    }


}