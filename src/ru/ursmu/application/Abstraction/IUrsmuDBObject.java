package ru.ursmu.application.Abstraction;

import android.content.Context;

public interface IUrsmuDBObject extends IUrsmuObject {
    public IDatabasingBehavior getDataBasingBehavior(Context c);

    public boolean isHard();

    public void setHard(boolean value);
}
