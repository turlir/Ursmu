package ru.ursmu.application.Activity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper {

    //ScheduleCommon
    public static final String FACULTY = "faculty";
    public static final String KURS = "kurs";
    public static final String GROUP = "group_name";  //not sqlite service expression  'group'
    public static final String UIN = "uin";

    public DataBaseHelper(Context context) {
        super(context, "DataBaseHelper", null, 2);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d("URSMULOG", "create DB table");

        db.execSQL("create table ScheduleDays(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +    //1
                "GroupID" + " INTEGER," +                                    //2
                "name" + " STRING," +                                        //3
                "teacher" + " STRING," +                                     //4
                "room" + " STRING," +                                        //5
                "DayIndex" + " INTEGER," +                                   //6
                "normalProfessor" + " STRING," +                             //7
                "numerPair" + " INTEGER" +                                   //8
                ");");

        db.execSQL("create table ScheduleCommon(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +  //1
                FACULTY + " STRING," +                                     //2
                KURS + " INTEGER," +                                       //3
                GROUP + " STRING," +                                       //4
                UIN + " INTEGER" +                                         //5
                ");");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("URSMULOG", "DataBaseHelper onUpgrade");
        db.execSQL("DROP TABLE IF EXISTS ScheduleCommon");
        db.execSQL("DROP TABLE IF EXISTS ScheduleDays");
        onCreate(db);
    }


}