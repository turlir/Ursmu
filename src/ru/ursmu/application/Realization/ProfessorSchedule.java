package ru.ursmu.application.Realization;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import ru.ursmu.application.Abstraction.IDatabasingBehavior;
import ru.ursmu.application.Abstraction.IParserBehavior;
import ru.ursmu.application.Abstraction.IUrsmuDBObject;

public class ProfessorSchedule implements IUrsmuDBObject {

    String mProfessor;

    public ProfessorSchedule(String prof) {
        mProfessor = prof;
    }

    @Override
    public IDatabasingBehavior getDataBasingBehavior(Context c) {
        return ProfessorDataBasing.getInstance(c, mProfessor);
    }

    @Override
    public boolean isHard() {
        return false;
    }

    @Override
    public void setHard(boolean value) {

    }

    @Override
    public String getUri() {
        return null;
    }

    @Override
    public String getParameters() {
        return null;
    }

    @Override
    public IParserBehavior getParseBehavior() {
        return new ScheduleParser();
    }
}