package ru.ursmu.application.Realization;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Patterns;
import ru.ursmu.application.Abstraction.IParserBehavior;
import ru.ursmu.application.Abstraction.IUrsmuObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Pattern;


public class PushRegister implements IUrsmuObject {

    String mId, mFaculty, mEmail;

    public PushRegister(Context c, String id, String faculty) {
        mId = id;
        mFaculty = faculty;

        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(c).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                mEmail = account.name;
                break;
            }
        }
    }

    @Override
    public String getUri() {
        return "http://192.168.0.100:8871/register";
    }

    @Override
    public String getParameters() {
        return "id=" + Encode(mId) + "&faculty=" + Encode(mFaculty) + "&email=" + Encode(mEmail);
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
