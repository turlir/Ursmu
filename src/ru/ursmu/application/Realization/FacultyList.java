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
}