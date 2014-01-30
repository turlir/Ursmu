package ru.ursmu.application.Realization;

import ru.ursmu.application.Abstraction.AbsPush;

public class PushReRegister extends AbsPush {

    public PushReRegister(String id, String faculty, String group) {
        super(id, faculty, group);
    }

    @Override
    protected boolean getFlag() {
        return true;
    }
}
