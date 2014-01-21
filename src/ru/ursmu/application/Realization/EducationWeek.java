package ru.ursmu.application.Realization;

import ru.ursmu.application.JsonObject.EducationItem;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: turlir
 * Date: 09.01.14
 * Time: 1:16
 * To change this template use File | Settings | File Templates.
 */
public class EducationWeek implements Serializable {

    private ArrayList<EducationItem> mMonday;
    private ArrayList<EducationItem> mTuesday;
    private ArrayList<EducationItem> mWednesday;
    private ArrayList<EducationItem> mThursday;
    private ArrayList<EducationItem> mFriday;
    private ArrayList<EducationItem> mSaturday;

    public EducationWeek() {
        int DAY_CAPACITY = 8;
        mMonday = new ArrayList<EducationItem>(DAY_CAPACITY);
        mTuesday = new ArrayList<EducationItem>(DAY_CAPACITY);
        mWednesday = new ArrayList<EducationItem>(DAY_CAPACITY);
        mThursday = new ArrayList<EducationItem>(DAY_CAPACITY);
        mFriday = new ArrayList<EducationItem>(DAY_CAPACITY);
        mSaturday = new ArrayList<EducationItem>(DAY_CAPACITY);
    }


    public void set(int i, EducationItem day) {
        switch (i) {
            case 0:
                mMonday.add(day);
                break;
            case 1:
                mTuesday.add(day);
                break;
            case 2:
                mWednesday.add(day);
                break;
            case 3:
                mThursday.add(day);
                break;
            case 4:
                mFriday.add(day);
                break;
            case 5:
                mSaturday.add(day);
                break;
        }
    }

    public EducationItem[] get(int i) {
        switch (i) {
            case 0:
                return mMonday.toArray(new EducationItem[]{});

            case 1:
                return mTuesday.toArray(new EducationItem[]{});

            case 2:
                return mWednesday.toArray(new EducationItem[]{});

            case 3:
                return mThursday.toArray(new EducationItem[]{});

            case 4:
                return mFriday.toArray(new EducationItem[]{});

            case 5:
                return mSaturday.toArray(new EducationItem[]{});

            default:
                return new EducationItem[]{};
        }
    }
}
