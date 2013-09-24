package ru.example.ursmu.Realization;

import android.content.Context;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.Log;
import ru.example.ursmu.Abstraction.*;

import java.util.ArrayList;

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
        ArrayList<Object> q;

        try {
            String uri;
            String param;
            IDownloadBehavior down_agent = getDownloadBehavior();
            IParserBehavior parse_agent = getParseBehavior();

            Log.d("URSMULOG", "DBProcessor size=");

            uri = mObject.getUri();
            param = mObject.getParameters();

            s = down_agent.Download(uri, param);
            q = parse_agent.parseTwo(s);

            if (mHard) {
                dbAgent.update(q);
            } else {
                dbAgent.add(q);
            }


            start(dbAgent);
        } catch (Exception ex) {
            sendFailure(ex.getMessage());
        }
        return null;
    }


    public void start(IDatabasingBehavior db_agent) {

        Log.d("URSMULOG", "DBProcessor streamStart");

        for (int i = 0; i < 6; i++) {
            Object[] items = getDataBaseBehavior().get(i);
            Parcelable[] day = (Parcelable[]) items;
            super.sendComplete(day);
        }
        db_agent.close();
        Log.d("URSMULOG", "DBProcessor stop");

    }
}