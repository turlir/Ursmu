package ru.ursmu.application.Realization;

import android.content.Context;
import ru.ursmu.application.Abstraction.IDatabasingBehavior;
import ru.ursmu.application.Abstraction.IParserBehavior;
import ru.ursmu.application.Abstraction.IUrsmuDBObject;


public class RandomChairList implements IUrsmuDBObject {
    int mCountRandomItem;

    public RandomChairList(int c) {
        mCountRandomItem = c;
    }

    @Override
    public IDatabasingBehavior getDataBasingBehavior(Context c) {
        return ChairDataBasing.getInstance(c, mCountRandomItem);
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
        return null;
    }
}
