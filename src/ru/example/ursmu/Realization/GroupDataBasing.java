package ru.example.ursmu.Realization;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import ru.example.ursmu.Abstraction.IDatabasingBehavior;
import ru.example.ursmu.Activity.DataBaseHelper;
import ru.example.ursmu.JsonObject.EducationItem;

import java.util.ArrayList;
import java.util.UUID;

public class GroupDataBasing extends IDatabasingBehavior {


    String fac, kur, gro;

    private static Context mContext;
    private static SQLiteStatement common;
    private static SQLiteStatement day;
    private static SQLiteDatabase mDataBase;
    private static String mQueryLimit;
    private static String mQueryNonLimit;

    public static GroupDataBasing getInstance(Context c, String f, String k, String g) {
        if (mContext == null || mDataBase == null) {
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
            query_non_limit.append("SELECT ScheduleCommon.GroupName, ScheduleCommon._id");
            query_non_limit.append(" FROM ScheduleCommon");
            query_non_limit.append(" WHERE ");
            query_non_limit.append(" (ScheduleCommon.GroupName LIKE ?)");
            query_non_limit.append(" GROUP BY ScheduleCommon.GroupName");
            mQueryNonLimit = query_non_limit.toString();


        }
        return new GroupDataBasing(f, k, g);
    }

    private GroupDataBasing(String f, String k, String g) {
        fac = f;
        kur = k;
        gro = g;
    }

    @Override
    public void add(ArrayList<?> week) {
        ArrayList<EducationItem> temp = (ArrayList<EducationItem>) week;
        Long id = Math.abs(UUID.randomUUID().getLeastSignificantBits());

        Log.d("URSMULOG", fac + " " + kur + " " + gro);

        mDataBase.beginTransaction();

        common.bindString(2, fac);
        common.bindString(3, kur);
        common.bindString(4, gro);
        common.bindLong(5, id);
        common.executeInsert();


        for (EducationItem item : temp) {
            Log.d("URSMULOG", "add " + item.getDayOfTheWeek());
            day.bindLong(2, id);
            day.bindString(3, item.getmPredmet());
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
        return mDataBase.rawQuery(mQueryNonLimit, new String[]{"%" + gro + "%"});
    }

    private static Long mUIN = null;
    private static String mLastGroup = "";

    @Override
    public Object[] get(int limit) {
        if (mUIN == null || !gro.equals(mLastGroup)) {
            mLastGroup = gro;
            mUIN = getUINCurrentGroup();
        }

        return getGroupSchedule(limit, mUIN);
    }

    private Long getUINCurrentGroup() {
        Cursor c = mDataBase.rawQuery("SELECT ScheduleCommon.UIN FROM ScheduleCommon WHERE (ScheduleCommon.GroupName = ?)",
                new String[]{gro});
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
                    new String[]{gro});
            int uin_index = c.getColumnIndexOrThrow("UIN");
            c.moveToFirst();
            Long uin = c.getLong(uin_index);
            c.close();

            mDataBase.delete("ScheduleCommon", "UIN=?", new String[]{String.valueOf(uin)});
            mDataBase.delete("ScheduleDays", "GroupID=?", new String[]{String.valueOf(uin)});
        }
        add(q);
    }

    @Override
    public boolean check() {
        Cursor c = mDataBase.rawQuery("SELECT COUNT(ScheduleCommon.UIN) FROM ScheduleCommon WHERE (ScheduleCommon.GroupName = '" + gro + "')", null);
        c.moveToFirst();
        int count = c.getInt(0);
        c.close();
        return (count > 0);
    }

    @Override
    public void clearTable() {
        Log.d("URSMULOG", "GroupDataBasing clearTable");
        mDataBase.delete("ScheduleCommon", null, new String[]{});
        mDataBase.delete("ScheduleDays", null, new String[]{});

        mDataBase.close();
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