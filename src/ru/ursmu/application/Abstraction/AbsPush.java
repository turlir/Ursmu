package ru.ursmu.application.Abstraction;


import ru.ursmu.application.Realization.EmptyParse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public abstract class AbsPush implements IUrsmuObject {
    protected abstract boolean getFlag();

    protected String mFaculty, mId, mGroup;

    public AbsPush(String i, String j, String k) {
        if (i==null) {
            i = "";
        }
        if (j == null) {
            j = "";
        }
        if (k==null) {
            k = "";
        }
        mId = i;
        mFaculty = j;
        mGroup = k;
    }

    @Override
    public String getUri() {
        if (!getFlag()) {
            return "http://192.168.0.100:8871/register";
        } else {
            return "http://192.168.0.100:8871/re_register";
        }
    }

    @Override
    public String getParameters() {
        return "id=" + Encode(mId) + "&faculty=" + Encode(mFaculty) + "&group=" + Encode(mGroup);
    }


    @Override
    public IParserBehavior getParseBehavior() {
        return new EmptyParse();
    }

    private String Encode(String original) {
        String r = null;
        try {
            r = URLEncoder.encode(original, "utf-8");
        } catch (UnsupportedEncodingException e) {

        }
        return r;
    }
}
