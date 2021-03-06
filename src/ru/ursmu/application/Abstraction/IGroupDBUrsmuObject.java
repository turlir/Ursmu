package ru.ursmu.application.Abstraction;

import android.content.Context;
import android.os.Parcelable;
import org.json.JSONException;

import java.io.IOException;

public interface IGroupDBUrsmuObject<T extends IUrsmuDBObject> extends Parcelable {        //factory db object; only add


    boolean check(Context c);

    void setCheck(Context c);

    public void first() throws IOException, JSONException;

    public T next();

    public IDatabasingBehavior getDataBasingBehavior(Context context);
}
