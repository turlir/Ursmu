package ru.example.ursmu.Realization;

import android.os.Parcel;
import android.os.Parcelable;
import ru.example.ursmu.Abstraction.IParserBehavior;
import ru.example.ursmu.Abstraction.IUrsmuObject;

public class GornyVesti implements IUrsmuObject {
    String mUri = "http://pressa.ursmu.ru/video.html";
    String mParam;

    public GornyVesti(String data) {
        mParam = "task=video&page=0&dats=" + data + "&cat=null";
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
        return new GornyVestiParser();
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
        public GornyVesti createFromParcel(Parcel in) {
            return new GornyVesti(in);
        }

        public GornyVesti[] newArray(int size) {
            return new GornyVesti[size];
        }
    };

    private GornyVesti(Parcel parcel) {
        mUri = parcel.readString();
        mParam = parcel.readString();
    }

}