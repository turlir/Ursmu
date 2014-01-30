package ru.ursmu.application.Realization;

import android.content.Context;
import ru.ursmu.application.Abstraction.IDatabasingBehavior;
import ru.ursmu.application.Abstraction.IParserBehavior;
import ru.ursmu.application.Abstraction.IUrsmuDBObject;
import ru.ursmu.application.Abstraction.IUrsmuObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ScheduleGroup implements IUrsmuDBObject, IUrsmuObject {

    String uri = "http://mobrasp.ursmu.ru/";

    private String mFaculty, mKurs, mGroup, mParam;
    private boolean mHard;

    public ScheduleGroup(String fak, String kur, String gro, boolean isHard) {
        mFaculty = fak;
        mKurs = kur;
        mGroup = gro;
        mParam = "task=rasp" + "&fak=" + Encode(mFaculty) + "&kurs=" + mKurs + "&group=" + Encode(mGroup);
        mHard = isHard;
        //mContext = context;
    }

    public ScheduleGroup(String gro, boolean isHard) {
        mGroup = gro;

        mFaculty = "";
        mKurs = "";
        mHard = isHard;
    }

    private String Encode(String original) {
        String r = null;
        try {
            r = URLEncoder.encode(original, "utf-8");
        } catch (UnsupportedEncodingException e) {
        }
        return r;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public String getParameters() {
        return mParam;
    }

    @Override
    public boolean isHard() {
        return mHard;
    }

    @Override
    public void setHard(boolean value) {
        mHard = value;
    }

    public String getFaculty() {
        return mFaculty;
    }

    public String getKurs() {
        return mKurs;
    }

    public String getGroup() {
        return mGroup;
    }

    @Override
    public IParserBehavior getParseBehavior() {
        return new ScheduleParser();
    }

    @Override
    public IDatabasingBehavior getDataBasingBehavior(Context c) {
        //GroupDataBasing.mContext = c;
        return GroupDataBasing.getInstance(c, mFaculty, mKurs, mGroup);
    }
}