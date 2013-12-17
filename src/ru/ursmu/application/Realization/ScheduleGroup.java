package ru.ursmu.application.Realization;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import ru.ursmu.application.Abstraction.IDatabasingBehavior;
import ru.ursmu.application.Abstraction.IParserBehavior;
import ru.ursmu.application.Abstraction.IUrsmuDBObject;
import ru.ursmu.application.Abstraction.IUrsmuObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ScheduleGroup implements IUrsmuDBObject, IUrsmuObject {

    String uri = "http://mobrasp.ursmu.ru/";

    public String mFaculty;
    public String mKurs;
    public String mGroup;
    public String mParam;
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

    @Override
    public IParserBehavior getParseBehavior() {
        return new ScheduleParser();
    }

    @Override
    public IDatabasingBehavior getDataBasingBehavior(Context c) {
        //GroupDataBasing.mContext = c;
        return GroupDataBasing.getInstance(c, mFaculty, mKurs, mGroup);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uri);

        dest.writeString(mFaculty);
        dest.writeString(mKurs);
        dest.writeString(mGroup);
        dest.writeString(mParam);
        dest.writeByte((byte) (mHard ? 1 : 0));
    }

    public static final Parcelable.Creator<IUrsmuObject> CREATOR = new Parcelable.Creator<IUrsmuObject>() {
        public ScheduleGroup createFromParcel(Parcel in) {
            return new ScheduleGroup(in);
        }

        public ScheduleGroup[] newArray(int size) {
            return new ScheduleGroup[size];
        }
    };

    private ScheduleGroup(Parcel parcel) {
        uri = parcel.readString();

        mFaculty = parcel.readString();
        mKurs = parcel.readString();
        mGroup = parcel.readString();
        mParam = parcel.readString();
        mHard = parcel.readByte() == 1;
    }


}