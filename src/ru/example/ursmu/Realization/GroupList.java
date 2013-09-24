package ru.example.ursmu.Realization;

import android.os.Parcel;
import android.os.Parcelable;
import ru.example.ursmu.Abstraction.IParserBehavior;
import ru.example.ursmu.Abstraction.IUrsmuObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class GroupList implements IUrsmuObject {

    //private StringBuilder mParam = new StringBuilder("task=group"+"&fak=" + faculty + "&kurs=" + kurs);

    private String mParam;
    private String mUri = SERVER_1;

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
        return mUri;
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
        dest.writeString(mUri);
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
        mUri = parcel.readString();
        mParam = parcel.readString();
    }
}