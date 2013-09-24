package ru.example.ursmu.Realization;

import android.os.Parcel;
import android.os.Parcelable;
import ru.example.ursmu.Abstraction.IParserBehavior;
import ru.example.ursmu.Abstraction.IUrsmuObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UrsmuNews implements IUrsmuObject {

    String mUri = "http://mobile.ursmu.ru/ajax";
    String mParam;

    public UrsmuNews(int pageNumber) {
       /* task	news
        page	2*/
        mParam = "task=news&page=" + Encode(String.valueOf(pageNumber));
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
        return new NewsParser();
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
        public UrsmuNews createFromParcel(Parcel in) {
            return new UrsmuNews(in);
        }

        public UrsmuNews[] newArray(int size) {
            return new UrsmuNews[size];
        }
    };

    private UrsmuNews(Parcel parcel) {
        mUri = parcel.readString();
        mParam = parcel.readString();
    }
}