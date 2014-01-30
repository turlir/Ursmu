package ru.ursmu.application.Realization;

import ru.ursmu.application.Abstraction.IParserBehavior;
import ru.ursmu.application.Abstraction.IUrsmuObject;

public class FacultyList implements IUrsmuObject {


    //private boolean mIsDB = false;

    public FacultyList() {

    }

    @Override
    public String getUri() {
        return SERVER_1;
    }

    @Override
    public String getParameters() {
        return "task=fak";
    }

    @Override
    public IParserBehavior getParseBehavior() {
        return new JsonArrayParser();
    }
}