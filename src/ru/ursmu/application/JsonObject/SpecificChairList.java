package ru.ursmu.application.JsonObject;

import android.content.Context;
import ru.ursmu.application.Abstraction.IDatabasingBehavior;
import ru.ursmu.application.Abstraction.IParserBehavior;
import ru.ursmu.application.Abstraction.IUrsmuDBObject;
import ru.ursmu.application.Realization.ChairDataBasing;


public class SpecificChairList implements IUrsmuDBObject {

    String s;

    public SpecificChairList(String newText) {
           s = newText;
    }

    @Override
    public IDatabasingBehavior getDataBasingBehavior(Context c) {
        return ChairDataBasing.getInstance(c,s);
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
