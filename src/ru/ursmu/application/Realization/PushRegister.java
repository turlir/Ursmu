package ru.ursmu.application.Realization;

import ru.ursmu.application.Abstraction.AbsPush;


public class PushRegister extends AbsPush {

    String mId, mFaculty, mGroup;

    @Override
    protected boolean getFlag() {
        return false;
    }

    public PushRegister(String id, String faculty, String group) {
        super(id, faculty, group);
        mId = id;
        mFaculty = faculty;
        mGroup = group;
    }
}
