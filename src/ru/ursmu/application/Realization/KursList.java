package ru.ursmu.application.Realization;

import android.os.Parcel;
import android.os.Parcelable;
import ru.ursmu.application.Abstraction.IParserBehavior;
import ru.ursmu.application.Abstraction.IUrsmuObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class KursList implements IUrsmuObject {

    private String mUri = SERVER_1;
    private StringBuilder mParam = new StringBuilder("task=kurs" + "&fak=");
    //private boolean mIsDB = false;


    public KursList(String number) {
        //String kurs = FacultyFactory.toShortName(number);
        mParam.append(Encode(number));
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
        return mParam.toString();
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
        dest.writeSerializable(mParam);
        //dest.writeByte((byte) (mIsDB ? 1 : 0));
    }

    public static final Parcelable.Creator<IUrsmuObject> CREATOR = new Parcelable.Creator<IUrsmuObject>() {
        public KursList createFromParcel(Parcel in) {
            return new KursList(in);
        }

        public KursList[] newArray(int size) {
            return new KursList[size];
        }
    };

    private KursList(Parcel parcel) {
        mUri = parcel.readString();
        mParam = (StringBuilder) parcel.readSerializable();
        //mIsDB = (parcel.readByte() == 1);
    }
}