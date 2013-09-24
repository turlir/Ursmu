package ru.example.ursmu.Activity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper {

    public DataBaseHelper(Context context) {
        super(context, "DataBaseHelper", null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        //db.execSQL("PRAGMA foreign_keys=ON;");

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
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +    //1
                ServiceHelper.FACULTY + " STRING," +                                     //2
                ServiceHelper.KURS + " INTEGER," +                                       //3
                ServiceHelper.GROUP + " STRING," +                                       //4
                "UIN" + " INTEGER" +                                        //5

                ");");

        //db.execSQL("CREATE INDEX trackindex ON ScheduleCommon(UIN);");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}