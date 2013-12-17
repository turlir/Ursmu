package ru.ursmu.application.Realization;

import android.os.Parcel;
import android.os.Parcelable;
import ru.ursmu.application.Abstraction.IParserBehavior;
import ru.ursmu.application.Abstraction.IUrsmuObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class GroupList implements IUrsmuObject {

    //private StringBuilder mParam = new StringBuilder("task=group"+"&fak=" + faculty + "&kurs=" + kurs);

    private String mParam;;

    public GroupList(String f, String k) {
        mParam = "task=group" + "&fak=" + Encode(f) + "&kurs=" + Encode(k);
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
        return SERVER_1;
    }

    @Override
    public String getParameters() {
        return mParam;
    }

    @Override
    public IParserBehavior getParseBehavior() {
        return new JsonArrayParser();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mParam);
    }

    public static final Parcelable.Creator<IUrsmuObject> CREATOR = new Parcelable.Creator<IUrsmuObject>() {
        public GroupList createFromParcel(Parcel in) {
            return new GroupList(in);
        }

        public GroupList[] newArray(int size) {
            return new GroupList[size];
        }
    };

    private GroupList(Parcel parcel) {
        mParam = parcel.readString();
    }
}