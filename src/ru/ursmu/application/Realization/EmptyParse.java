package ru.ursmu.application.Realization;

import org.json.JSONException;
import ru.ursmu.application.Abstraction.IParserBehavior;

public class EmptyParse extends IParserBehavior {
    @Override
    public Object[] parse(String json) throws JSONException {
        return new String[]{""};
    }
}
