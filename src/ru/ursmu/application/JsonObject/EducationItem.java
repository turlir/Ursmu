package ru.ursmu.application.JsonObject;

import android.database.Cursor;
import android.util.Log;
import ru.ursmu.application.Activity.DataBaseHelper;

import java.io.Serializable;

public class EducationItem implements Serializable {
    private String mGroupName;
    private String mFaculty;
    private String mKurs;

    private int mDayNumber;
    private String mPredmet;
    private String mTeacher;
    private String mRoom;
    private int mNumberPar = 1;

    public static final String[] DayOfTheWeek = new String[]{"Понедельник",
            "Вторник", "Среда", "Четверг", "Пятница", "Суббота"};

    private static int name_in = 0;
    private static int teacher_in = 0;
    private static int audition_in = 0;
    private static int numberPair_in = 0;
    private static int dayNumber_in = 0;

    private static int groupName_in = 0;
    private static int faculty_in = 0;
    private static int kurs_in = 0;

    public static boolean lastPredict;

    public EducationItem(Cursor cursor, boolean f) {        //db
        if (f) {
            if (groupName_in == 0 || faculty_in == 0 || kurs_in == 0) {
                groupName_in = cursor.getColumnIndex(DataBaseHelper.GROUP);
                faculty_in = cursor.getColumnIndex(DataBaseHelper.FACULTY);
                kurs_in = cursor.getColumnIndex(DataBaseHelper.KURS);
            }
            mGroupName = cursor.getString(groupName_in);
            mFaculty = cursor.getString(faculty_in);
            mKurs = cursor.getString(kurs_in);
        }

        if (teacher_in == 0 || lastPredict != f) {
            Log.d("URSMULOG ", "Create index column");

            name_in = cursor.getColumnIndex("name");
            teacher_in = cursor.getColumnIndex("teacher");
            audition_in = cursor.getColumnIndex("room");
            numberPair_in = cursor.getColumnIndex("numerPair");
            dayNumber_in = cursor.getColumnIndex("DayIndex");
        }

        mPredmet = cursor.getString(name_in);
        mDayNumber = cursor.getInt(dayNumber_in);

        mTeacher = cursor.getString(teacher_in);
        mRoom = cursor.getString(audition_in);
        mNumberPar = cursor.getInt(numberPair_in);

        lastPredict = f;
    }


    public EducationItem(int dayIndex, int para, String predmet, String prepod, String aud) {  //json
        //Log.d("URSMULOG", "dayIndex=" + dayIndex + "para=" + para);
        if (aud.length() > prepod.length()) {
            String q;
            q = aud;
            aud = prepod;
            prepod = q;
        }
        mDayNumber = dayIndex;
        mNumberPar = para;
        mPredmet = predmet;
        mTeacher = prepod;
        this.mRoom = aud;
    }


    public int getDayOfTheWeek() {
        return mDayNumber;
    }

    public String getPredmet() {
        return mPredmet;
    }

    public String getProfessor() {
        return mTeacher;
    }

    public String getAud() {
        return mRoom;
    }

    public int getNumberPar() {
        return mNumberPar;
    }

    public String getNormalProfessor() {
        return mTeacher.toLowerCase();
    }

    public String getGroupName() {
        return mGroupName;
    }

    public  String getFaculty() {
       return mFaculty;
    }

    public  String getKurs() {
        return mKurs;
    }

}