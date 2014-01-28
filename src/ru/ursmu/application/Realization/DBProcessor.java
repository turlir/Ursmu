package ru.ursmu.application.Realization;

import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;
import android.util.Log;
import org.json.JSONException;
import ru.ursmu.application.Abstraction.*;
import ru.ursmu.application.Activity.UrsmuService;
import ru.ursmu.beta.application.R;

import java.io.IOException;

public class DBProcessor extends AbstractProcessor {
    IUrsmuDBObject mObject;
    Context mContext;
    ResultReceiver mCallback;
    boolean mHard;

    public DBProcessor(IUrsmuDBObject to, ResultReceiver callback, long id, Context mC) {
        super(to, id, callback, mC);
        mObject = to;
        mContext = mC;
        mCallback = callback;
        mHard = to.isHard();
    }

    @Override
    protected Object[] doInBackground(Void... params) {
        Log.d("URSMULOG", "DBProcessor start");

        sendStart();

        IDatabasingBehavior dbAgent = getDataBaseBehavior();
        if (mCallback != null) {
            if (dbAgent.check() && !mHard) {
                Log.d("URSMULOG", "DBProcessor check true");
                start(dbAgent); //есть в базе И не обновляем
                return null;
            }
        }

        //если нет в базе или обновляем
        String s;
        Object[] q;

        try {
            String uri;
            String param;
            IDownloadBehavior down_agent = getDownloadBehavior();
            IParserBehavior parse_agent = getParseBehavior();

            Log.d("URSMULOG", "DBProcessor size=");

            uri = mObject.getUri();
            param = mObject.getParameters();

            s = down_agent.Download(uri, param);
            q = parse_agent.parse(s);

            if (mHard) {
                dbAgent.update(q);
            } else {
                dbAgent.add(q);
            }


            start(dbAgent);
        } catch (IOException ex) {
            sendFailure(mContext.getResources().getString(R.string.network_error));
            ex.printStackTrace();
            return null;
        } catch (JSONException ex){
            sendFailure(mContext.getResources().getString(R.string.parse_error));
            ex.printStackTrace();
            return null;
        } catch (Exception ex) {
            sendFailure(mContext.getResources().getString(R.string.null_error));
            ex.printStackTrace();
            return null;
        }

        return null;
    }


    public void start(IDatabasingBehavior db_agent) {

        Log.d("URSMULOG", "DBProcessor streamStart");
        Object items = getDataBaseBehavior().getSchedule();
        if (items == null) {
            sendFailure(mContext.getResources().getString(R.string.null_error));
            return;
        }
        super.sendComplete(items);
        db_agent.close();
        mContext.stopService(new Intent(mContext, UrsmuService.class));
        Log.d("URSMULOG", "DBProcessor stop");
    }
}