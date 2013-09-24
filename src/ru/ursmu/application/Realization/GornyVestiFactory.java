package ru.ursmu.application.Realization;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import ru.ursmu.application.Abstraction.IGroupUrsmuObject;
import ru.ursmu.application.Abstraction.IUrsmuObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GornyVestiFactory implements IGroupUrsmuObject<IUrsmuObject> {
    private static Calendar mCalendar;
    private static String PATTERN = "dd.MM.yyyy";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(PATTERN);
    private int mNumberPage;
    //private Date mStartDate;
    private static int mCount;

    public GornyVestiFactory(int numberPage) {
        mNumberPage = numberPage;
    }

    @Override
    public boolean first() {
        mCalendar = Calendar.getInstance();
        if (mNumberPage != 1) {
            mCalendar.set(Calendar.DAY_OF_WEEK, -19 * mNumberPage);
        }
        //mStartDate = mCalendar.getTime();
        mCount = 0;

        return true;
    }

    @Override
    public IUrsmuObject next() {
        if (mCount < 10) {      // [0;9)
            Log.d("URSMULOG", "next fabric " + mCount);
            String formattedDate = DATE_FORMAT.format(mCalendar.getTime());
            mCalendar.add(Calendar.DATE, -1);
            mCount++;
            //setCounter(+1);
            return new GornyVesti(formattedDate);
        } else
            return null;
    }

    @Override
    public void setCounter(int value) {
        switch (value) {
            case -1:
                if (mCount != 0) {
                    mCount--;
                }
                break;
            case +1:
                mCount++;
                break;
            default:
                mCount = value;
                break;
        }
    }

    @Override
    public IUrsmuObject getSample() {
        return new GornyVesti("");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mNumberPage);
    }

    public static final Parcelable.Creator<IGroupUrsmuObject> CREATOR = new Parcelable.Creator<IGroupUrsmuObject>() {
        public IGroupUrsmuObject createFromParcel(Parcel in) {
            return new GornyVestiFactory(in);
        }

        public IGroupUrsmuObject[] newArray(int size) {
            return new GornyVestiFactory[size];
        }
    };

    private GornyVestiFactory(Parcel parcel) {
        mNumberPage = parcel.readInt();
    }
}