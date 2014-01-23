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

import java.util.UUID;

public class GroupDataBasing extends IDatabasingBehavior {


    String mFaculty, mKurs, mGroup;

    private static Context mContext;
    private static SQLiteStatement common;
    private static SQLiteStatement day;
    private static SQLiteDatabase mDataBase;
    private static String mQuerySchedule;
    private static String mQueryGroupList;

    private static Long mUIN = null;
    private static String mLastGroup = null;

    static {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * ");
        sb.append("FROM ScheduleDays ");
        sb.append("WHERE (ScheduleDays.GroupID = ?) ");
        //mQuery.append("AND (ScheduleDays.room !='')");          //show empty pair?
        sb.append("ORDER BY ScheduleDays.DayIndex, ScheduleDays.numerPair");
        mQuerySchedule = sb.toString();

        sb = new StringBuilder();
        sb.append("SELECT ScheduleCommon.GroupName, ScheduleCommon._id, ScheduleCommon.FACULTY, ScheduleCommon.KURS ");
        sb.append("FROM ScheduleCommon ");
        sb.append("WHERE ");
        sb.append("(ScheduleCommon.GroupName LIKE ?) ");
        sb.append("GROUP BY ScheduleCommon.GroupName ");
        mQueryGroupList = sb.toString();
    }

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
        }
        return new GroupDataBasing(f, k, g);
    }

    private GroupDataBasing(String faculty, String kurs, String group) {
        mGroup = group;
        mFaculty = faculty;
        mKurs = kurs;
    }

    @Override
    public void add(Object[] week) throws Exception {
        super.add(week);
        EducationItem[] temp = (EducationItem[]) week;
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
        return mDataBase.rawQuery(mQueryGroupList, new String[]{"%" + mGroup + "%"});
    }

    @Override
    public EducationWeek getSchedule() {
        getUINCurrentGroup();
        return getGroupSchedule();
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
    public void update(Object[] q) throws Exception {
        super.update(q);
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
        if (!mDataBase.isDbLockedByCurrentThread() && !mDataBase.isDbLockedByOtherThreads()) {
            Log.d("URSMULOG", "GroupDataBasing clearTable");
            mDataBase.delete("ScheduleCommon", "", new String[]{});
            mDataBase.delete("ScheduleDays", "", new String[]{});
        } else {
            String s = "GroupDataBasing blocked";
            Log.d("URSMULOG", s);
        }
        close();
    }


    public EducationWeek getGroupSchedule() {
        EducationWeek week = new EducationWeek();

        Cursor cursor = mDataBase.rawQuery(mQuerySchedule, new String[]{String.valueOf(mUIN)});
        int count = cursor.getCount();

        if (count != 0 && cursor.moveToFirst()) {
            EducationItem item;
            do {
                item = new EducationItem(cursor, false);
                week.set(item.getDayOfTheWeek(), item);
            } while (cursor.moveToNext());

        }

        return week;
    }


}