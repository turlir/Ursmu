package ru.ursmu.application.Realization;

import android.os.ResultReceiver;
import android.util.Log;
import org.json.JSONException;
import ru.ursmu.application.Abstraction.AbstractProcessor;
import ru.ursmu.application.Abstraction.IUrsmuObject;

import java.io.IOException;
import java.io.Serializable;

public class NormalProcessor extends AbstractProcessor {
    IUrsmuObject mUrsmu;


    public NormalProcessor(IUrsmuObject object, ResultReceiver receiver, Long id) {
        super(object, id, receiver);
        mUrsmu = object;
    }


    @Override
    protected Object[] doInBackground(Void... params) {
        sendStart();
        if (mUrsmu != null) {
            Object[] data = getItem(mUrsmu);
            if (data != null) {
                sendComplete((Serializable[]) data);
            } else
                sendFailure("Ошибка");
        }

        return null;
    }

    private Object[] getItem(IUrsmuObject item) {
        String s = null;
        Object[] q = null;
        try {
            Log.d("URSMULOG", item.getParameters());
            s = getDownloadBehavior().Download(item.getUri(), item.getParameters());
            q = getParseBehavior().parse(s);
            return q;
        } catch (JSONException e) {
            sendFailure("Ошибка разбора, повторите позже");
            e.printStackTrace();
        } catch (IOException e) {
            sendFailure("Ошибка сети, повторите позже");
            e.printStackTrace();
        }
        return null;
    }
}
