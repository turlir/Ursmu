package ru.ursmu.application.Realization;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONException;
import ru.ursmu.application.Abstraction.IDatabasingBehavior;
import ru.ursmu.application.Abstraction.IGroupDBUrsmuObject;
import ru.ursmu.application.Activity.ServiceHelper;

import java.io.IOException;

public class ScheduleGroupFactory implements IGroupDBUrsmuObject<ScheduleGroup> {

    //non ui-thread only

    public ScheduleGroupFactory() {

    }


    UrsmuPostDownload downloadBehavior = new UrsmuPostDownload();
    JsonArrayParser parseBehavior = new JsonArrayParser();

    String[] facultys;
    String[] kurs;
    String[] groups;

    int f, k, g;

    public boolean first() throws IOException, JSONException {
        FacultyList f_list = new FacultyList();
        facultys = parseBehavior.parse(downloadBehavior.Download(f_list.getUri(), f_list.getParameters()));
        f = 0;

        KursList k_list = new KursList(facultys[f]);
        kurs = parseBehavior.parse(downloadBehavior.Download(k_list.getUri(), k_list.getParameters()));
        k = 0;

        GroupList g_list = new GroupList(facultys[f], kurs[k]);
        groups = parseBehavior.parse(downloadBehavior.Download(g_list.getUri(), g_list.getParameters()));
        g = -1;  //first next() run

        //return new ScheduleGroup(facultys[f], kurs[k], groups[g]);
        return true;
    }

    public ScheduleGroup next() throws IOException, JSONException {
        if (g < groups.length - 1) {
            g++;
            return new ScheduleGroup(facultys[f], kurs[k], groups[g], false);
        } else {
            if (k < kurs.length - 1) {
                k++;
                GroupList g_list = new GroupList(facultys[f], kurs[k]);
                groups = parseBehavior.parse(downloadBehavior.Download(g_list.getUri(), g_list.getParameters()));
                g = -1;
                return next();
            } else {
                if (f < facultys.length - 1) {
                    f++;
                    KursList k_list = new KursList(facultys[f]);
                    kurs = parseBehavior.parse(downloadBehavior.Download(k_list.getUri(), k_list.getParameters()));
                    k = 0;

                    GroupList g_list = new GroupList(facultys[f], kurs[k]);
                    groups = parseBehavior.parse(downloadBehavior.Download(g_list.getUri(), g_list.getParameters()));
                    g = -1;
                    return next();
                } else {
                    return null;
                }
            }
        }

        //return null;
    }

    @Override
    public void clearDB(IDatabasingBehavior dbAgent) {
        if (dbAgent != null) {
            dbAgent.clearTable();
        }
    }

    @Override
    public boolean check(Context c) {
        return !ServiceHelper.getInstance(c).getBooleanPreference("PROF");
    }

    @Override
    public void setCheck(Context c) {
        ServiceHelper helper = ServiceHelper.getInstance(c);
        helper.setBooleanPreferences("FIRST_RUN", false);
        helper.setBooleanPreferences("PROF", false);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public static final Parcelable.Creator<IGroupDBUrsmuObject> CREATOR = new Parcelable.Creator<IGroupDBUrsmuObject>() {
        public IGroupDBUrsmuObject createFromParcel(Parcel in) {
            return new ScheduleGroupFactory(in);
        }

        public IGroupDBUrsmuObject[] newArray(int size) {
            return new IGroupDBUrsmuObject[size];
        }
    };

    private ScheduleGroupFactory(Parcel parcel) {

    }

}