package ru.example.ursmu.Realization;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import ru.example.ursmu.Abstraction.IDatabasingBehavior;
import ru.example.ursmu.Abstraction.IGroupDBUrsmuObject;
import ru.example.ursmu.Activity.ServiceHelper;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class ScheduleGroupFactory implements IGroupDBUrsmuObject<ScheduleGroup> {

    //non ui-thread only

    public ScheduleGroupFactory() {

    }

    @Override
    public ArrayList<ScheduleGroup> factory() throws Exception {

        Log.d("URSMULOG", "process ProfessorSchedule START");
        UrsmuPostDownload d_agent = new UrsmuPostDownload();
        JsonArrayParser p_agent = new JsonArrayParser();

        ArrayList<ScheduleGroup> group_schedule_object = new ArrayList<ScheduleGroup>();

        FacultyList f_list = new FacultyList();
        KursList k_list;
        GroupList g_list;
        ScheduleGroup sch_group;
        String one = f_list.getUri();
        String two = f_list.getParameters();


        String[] data = p_agent.parse(d_agent.Download(one, two));

        for (int i = 0; i < data.length; i++) {
            k_list = new KursList(data[i]);
            String[] data_two = p_agent.parse(d_agent.Download(k_list.getUri(), k_list.getParameters()));
            for (int q = 0; q < data_two.length; q++) {
                g_list = new GroupList(data[i], data_two[q]);
                String[] data_three = p_agent.parse(d_agent.Download(g_list.getUri(), g_list.getParameters()));
                for (int z = 0; z < data_three.length; z++) {
                    sch_group = new ScheduleGroup(data[i], data_two[q], data_three[z], false);
                    group_schedule_object.add(sch_group);
                }
            }
        }

        return group_schedule_object;
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
        dbAgent.clearTable();
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