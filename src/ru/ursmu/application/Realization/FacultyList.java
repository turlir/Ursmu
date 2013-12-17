package ru.ursmu.application.Realization;

import android.os.Parcel;
import android.os.Parcelable;
import ru.ursmu.application.Abstraction.IParserBehavior;
import ru.ursmu.application.Abstraction.IUrsmuObject;

public class FacultyList implements IUrsmuObject {


    private String mParam = "task=fak";
    //private boolean mIsDB = false;

    public FacultyList() {

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

    //Parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mParam);
        //dest.writeByte((byte) (mIsDB ? 1 : 0));
    }


    public static final Parcelable.Creator<IUrsmuObject> CREATOR = new Parcelable.Creator<IUrsmuObject>() {
        public FacultyList createFromParcel(Parcel in) {
            return new FacultyList(in);
        }

        public FacultyList[] newArray(int size) {
            return new FacultyList[size];
        }
    };

    private FacultyList(Parcel parcel) {
        mParam = parcel.readString();
        //mIsDB = (parcel.readByte() == 1);
    }
}