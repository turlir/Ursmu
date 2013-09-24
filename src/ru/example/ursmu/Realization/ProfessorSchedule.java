package ru.example.ursmu.Realization;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import ru.example.ursmu.Abstraction.IDatabasingBehavior;
import ru.example.ursmu.Abstraction.IParserBehavior;
import ru.example.ursmu.Abstraction.IUrsmuDBObject;

public class ProfessorSchedule implements IUrsmuDBObject {

    String mProfessor;
    //String uri = "http://rasp.ursmu.ru/";

    public ProfessorSchedule(String prof) {
        mProfessor = prof;
    }

    //<editor-fold desc="OLD">
/*public void process() {
        Log.d("URSMULOG", "process ProfessorSchedule START");
        UrsmuPostDownload d_agent = new UrsmuPostDownload();
        JsonArrayParser p_agent = new JsonArrayParser();

        //ArrayList<String> uris = new ArrayList<String>();
        ArrayList<String> params = new ArrayList<String>();

        FacultyList f_list = new FacultyList();
        KursList k_list;
        GroupList g_list;
        ScheduleGroup sch_group;
        String one = f_list.getUri();
        String two = f_list.getParameters();

        try {
            String[] data = p_agent.parse(d_agent.Download(one, two));

            for (int i = 0; i < data.length; i++) {
                k_list = new KursList(data[i]);
                String[] data_two = p_agent.parse(d_agent.Download(k_list.getUri(), k_list.getParameters()));
                for (int q = 0; q < data_two.length; q++) {
                    g_list = new GroupList(data[i], data_two[q]);
                    String[] data_three = p_agent.parse(d_agent.Download(g_list.getUri(), g_list.getParameters()));
                    for (int z = 0; z < data_three.length; z++) {
                        sch_group = new ScheduleGroup(data[i], data_two[q], data_three[z]);
                        //uris.add(sch_group.getUri()[0]);
                        params.add(sch_group.getParameters());
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //uri = (String[]) uris.toArray(new String[0]);
        param = (String[]) params.toArray(new String[0]);

        Log.d("URSMULOG", "process ProfessorSchedule END");
    }*/
    //</editor-fold>

    @Override
    public IDatabasingBehavior getDataBasingBehavior(Context c) {
        return ProfessorDataBasing.getInstance(c, mProfessor);
    }

    @Override
    public boolean isHard() {
        return false;
    }

    @Override
    public void setHard(boolean value) {

    }

    @Override
    public String getUri() {
        return null;
    }

    @Override
    public String getParameters() {
        return null;
    }

    @Override
    public IParserBehavior getParseBehavior() {
        return new ScheduleParser();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mProfessor);
        //dest.writeString(uri);
        //dest.writeStringArray(param);
    }

    public static final Parcelable.Creator<IUrsmuDBObject> CREATOR = new Parcelable.Creator<IUrsmuDBObject>() {
        public ProfessorSchedule createFromParcel(Parcel in) {
            return new ProfessorSchedule(in);
        }

        public ProfessorSchedule[] newArray(int size) {
            return new ProfessorSchedule[size];
        }
    };

    private ProfessorSchedule(Parcel parcel) {
        mProfessor = parcel.readString();
        //uri = parcel.readString();
        //parcel.readStringArray(param);
    }
}