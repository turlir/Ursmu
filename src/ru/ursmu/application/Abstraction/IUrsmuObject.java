package ru.ursmu.application.Abstraction;

import java.io.Serializable;

public interface IUrsmuObject extends Serializable {

    public String SERVER_1 = "http://mobrasp.ursmu.ru/getmobilerasp/";

    public abstract String getUri();

    public abstract String getParameters();

    public abstract IParserBehavior getParseBehavior();
}
