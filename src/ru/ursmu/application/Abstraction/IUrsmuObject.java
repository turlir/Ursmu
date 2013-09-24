package ru.ursmu.application.Abstraction;

import android.os.Parcelable;

public interface IUrsmuObject extends Parcelable {

    public String SERVER_1 = "http://rasp.ursmu.ru/getmobilerasp/";

    public abstract String getUri();

    public abstract String getParameters();

    public abstract IParserBehavior getParseBehavior();
}
