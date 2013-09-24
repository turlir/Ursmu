package ru.example.ursmu.Realization;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import ru.example.ursmu.Abstraction.IDatabasingBehavior;
import ru.example.ursmu.Activity.DataBaseHelper;
import ru.example.ursmu.JsonObject.EducationItem;

import java.util.ArrayList;

public class ProfessorDataBasing extends IDatabasingBehavior {


    private static Context mContext;
    private static SQLiteDatabase mDataBase;
    private static String mQueryNonLimit;
    //private static StringBuilder mStringBuffer;
    private String mProfessor;
    private static String mQueryLimit;

    public static ProfessorDataBasing getInstance(Context c, String professorName) {
        if (mContext == null || mDataBase == null) {
            mContext = c;
            mDataBase = new DataBaseHelper(mContext).getWritableDatabase();


            StringBuilder query_limit = new StringBuilder();
            query_limit.append("SELECT ScheduleDays.Name,");
            query_limit.append("ScheduleDays.teacher,");
            query_limit.append("ScheduleDays.room,");
            query_limit.append("ScheduleDays.numerPair,");
            query_limit.append("ScheduleDays._id,");
            query_limit.append("ScheduleDays.normalProfessor,");
            query_limit.append("ScheduleDays.DayIndex,");

            query_limit.append("ScheduleCommon.GroupName,");
            query_limit.append("ScheduleCommon.FACULTY,");
            query_limit.append("ScheduleCommon.KURS");

            query_limit.append(" FROM ScheduleCommon, ScheduleDays");
            query_limit.append(" WHERE (ScheduleCommon.UIN = ScheduleDays.GroupID)");

            query_limit.append("AND (ScheduleDays.normalProfessor = ");
            query_limit.append("?");
            query_limit.append(")");

            query_limit.append("AND (ScheduleDays.DayIndex = ");
            query_limit.append("?");
            query_limit.append(")");

            query_limit.append(" ORDER BY ScheduleDays.numerPair");
            mQueryLimit = query_limit.toString();


            StringBuilder sb = new StringBuilder();
            sb.append("SELECT ScheduleDays.normalProfessor, ScheduleDays._id");
            sb.append(" FROM ScheduleDays");

            sb.append(" WHERE (ScheduleDays.normalProfessor LIKE");
            //sb.append("'%" + "?" + "%')");
            sb.append("?)");
            sb.append("GROUP BY ScheduleDays.normalProfessor");
            //sb.append(" ORDER BY ScheduleDays.numerPair");
            mQueryNonLimit = sb.toString();

        }
        return new ProfessorDataBasing(professorName);
    }


    private ProfessorDataBasing(String professor_name) {
        mProfessor = professor_name;
    }


    @Override
    public void clearTable() {
        mDataBase.rawQuery("DROP TABLE IF EXISTS ScheduleCommon", null);
        mDataBase.rawQuery("DROP TABLE IF EXISTS ScheduleDays", null);
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
    public Object[] get(int limit) {
        return getProfessorSchedule(limit);
    }


    @Override
    public Cursor get() {
        return mDataBase.rawQuery(mQueryNonLimit, new String[]{"%" + mProfessor + "%"}); //trick
    }

    @Override
    public void update(ArrayList<Object> q) {
        add(q);
    }

    private Object[] getProfessorSchedule(int limit) {
        Cursor data = mDataBase.rawQuery(mQueryLimit, new String[]{mProfessor, String.valueOf(limit)});

        int count = data.getCount();

        if (count != 0) {
            EducationItem[] arr = new EducationItem[count];
            for (int i = 0; i < count; i++) {
                data.moveToPosition(i);
                arr[i] = new EducationItem(data, true);
            }
            data.close();
            return arr;
        } else {
            data.close();
            return null;
        }

    }

}