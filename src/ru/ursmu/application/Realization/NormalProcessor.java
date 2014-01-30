package ru.ursmu.application.Realization;

import android.content.Context;
import android.os.ResultReceiver;
import android.util.Log;
import org.json.JSONException;
import ru.ursmu.application.Abstraction.AbstractProcessor;
import ru.ursmu.application.Abstraction.IUrsmuObject;
import ru.ursmu.beta.application.R;

import java.io.IOException;
import java.io.Serializable;

public class NormalProcessor extends AbstractProcessor {
    IUrsmuObject mUrsmu;
    Context mContext;


    public NormalProcessor(IUrsmuObject object, ResultReceiver receiver, Long id, Context con) {
        super(object, id, receiver, con);
        mUrsmu = object;
        mContext = con;
    }


    @Override
    protected Object[] doInBackground(Void... params) {
        sendStart();
        if (mUrsmu != null) {
            Object[] data = getItem(mUrsmu);
            if (data != null) {
                sendComplete((Serializable[]) data);
            }
        }

        return null;
    }

    private Object[] getItem(IUrsmuObject item) {
        String s;
        Object[] q;
        try {
            Log.d("URSMULOG", item.getParameters());
            s = getDownloadBehavior().Download(item.getUri(), item.getParameters());
            q = getParseBehavior().parse(s);
            return q;
        } catch (JSONException ex) {
            sendFailure(mContext.getResources().getString(R.string.parse_error));
            ex.printStackTrace();
            return null;
        } catch (IOException ex) {
            sendFailure(mContext.getResources().getString(R.string.network_error));
            ex.printStackTrace();
            return null;
        }
    }
}
