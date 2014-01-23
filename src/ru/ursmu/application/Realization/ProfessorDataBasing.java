package ru.ursmu.application.Realization;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import ru.ursmu.application.Abstraction.IDatabasingBehavior;
import ru.ursmu.application.Activity.DataBaseHelper;
import ru.ursmu.application.JsonObject.EducationItem;

public class ProfessorDataBasing extends IDatabasingBehavior {


    private static Context mContext;
    private static SQLiteDatabase mDataBase;
    private static String mQueryNonLimit;
    //private static StringBuilder mStringBuffer;
    private String mProfessor;
    private static String mQueryLimit;

    static {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ScheduleDays.Name, ");
        sb.append("ScheduleDays.teacher,");
        sb.append("ScheduleDays.room, ");
        sb.append("ScheduleDays.numerPair, ");
        sb.append("ScheduleDays._id, ");
        sb.append("ScheduleDays.normalProfessor, ");
        sb.append("ScheduleDays.DayIndex, ");
        sb.append("ScheduleCommon.GroupName, ");
        sb.append("ScheduleCommon.FACULTY, ");
        sb.append("ScheduleCommon.KURS ");
        sb.append("FROM ScheduleCommon, ScheduleDays ");
        sb.append("WHERE (ScheduleCommon.UIN = ScheduleDays.GroupID) ");
        sb.append("AND (ScheduleDays.normalProfessor = ?) ");
        sb.append("ORDER BY ScheduleDays.DayIndex, ScheduleDays.numerPair");
        mQueryLimit = sb.toString();


        sb = new StringBuilder();
        sb.append("SELECT ScheduleDays.normalProfessor, ScheduleDays._id ");
        sb.append("FROM ScheduleDays ");
        sb.append("WHERE (ScheduleDays.normalProfessor LIKE ?) ");
        //sb.append("'%" + "?" + "%')");
        sb.append("GROUP BY ScheduleDays.normalProfessor");
        //sb.append(" ORDER BY ScheduleDays.numerPair");
        mQueryNonLimit = sb.toString();
    }

    public static ProfessorDataBasing getInstance(Context c, String professorName) {
        if (mContext == null || mDataBase == null) {
            mContext = c;
            mDataBase = new DataBaseHelper(mContext).getWritableDatabase();

        }
        return new ProfessorDataBasing(professorName);
    }


    private ProfessorDataBasing(String professor_name) {
        mProfessor = professor_name;
    }


    @Override
    public void clearTable() {
        if (!mDataBase.isDbLockedByCurrentThread() && !mDataBase.isDbLockedByOtherThreads()) {
            Log.d("URSMULOG", "ProfessorDataBasing clearTable");
            mDataBase.rawQuery("DROP TABLE IF EXISTS ScheduleCommon", new String[]{});
            mDataBase.rawQuery("DROP TABLE IF EXISTS ScheduleDays", new String[]{});
        } else {
            String s = "ProfessorDataBasing blocked";
            Log.d("URSMULOG", s);
        }
        close();
    }

    @Override
    public void close() {
        if (mDataBase != null) {
            mDataBase.close();
            mDataBase = null;
        }
        if (mContext != null) {
            mContext = null;
        }
    }

    @Override
    public EducationWeek getSchedule() {
        return getProfessorSchedule();
    }


    @Override
    public Cursor get() {
        return mDataBase.rawQuery(mQueryNonLimit, new String[]{"%" + mProfessor + "%"}); //trick
    }

    private EducationWeek getProfessorSchedule() {
        EducationWeek week = new EducationWeek();

        Cursor cursor = mDataBase.rawQuery(mQueryLimit, new String[]{String.valueOf(mProfessor)});
        int count = cursor.getCount();

        if (count != 0 && cursor.moveToFirst()) {
            EducationItem item;
            do {
                item = new EducationItem(cursor, true);
                week.set(item.getDayOfTheWeek(), item);
            } while (cursor.moveToNext());

        }

        return week;
    }

}