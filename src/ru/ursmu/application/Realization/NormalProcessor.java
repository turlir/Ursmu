package ru.ursmu.application.Realization;

import android.os.ResultReceiver;
import android.util.Log;
import org.json.JSONException;
import ru.ursmu.application.Abstraction.AbstractProcessor;
import ru.ursmu.application.Abstraction.IGroupUrsmuObject;
import ru.ursmu.application.Abstraction.IUrsmuObject;

import java.io.IOException;
import java.io.Serializable;

public class NormalProcessor extends AbstractProcessor {
    IUrsmuObject mUrsmu;
    IGroupUrsmuObject<IUrsmuObject> mIterator;


    public NormalProcessor(IUrsmuObject object, ResultReceiver receiver, Long id) {
        super(object, id, receiver);
        mUrsmu = object;
    }

    public NormalProcessor(IGroupUrsmuObject<IUrsmuObject> iterator, ResultReceiver receiver, Long id) {
        super(iterator.getSample(), id, receiver); //group get operations
        mUrsmu = null;
        mIterator = iterator;
    }


    @Override
    protected Object[] doInBackground(Void... params) {
        sendStart();
        if (mUrsmu != null) {
            Object[] data = getItem(mUrsmu);
            if (data != null) {
                sendComplete((Serializable[]) data);
            }
        } else {
            if (mIterator.first()) {
                IUrsmuObject item;
                while ((item = mIterator.next()) != null) {
                    Object[] data = getItem(item);
                    if (data != null) {
                        sendComplete((Serializable[]) data);
                    } else {
                        mIterator.setCounter(-1);
                    }
                }
            }
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
            sendFailure(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            sendFailure(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
