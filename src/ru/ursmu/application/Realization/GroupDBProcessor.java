package ru.ursmu.application.Realization;

import android.content.Context;
import android.os.ResultReceiver;
import android.util.Log;
import org.json.JSONException;
import ru.ursmu.application.Abstraction.AbstractProcessor;
import ru.ursmu.application.Abstraction.IGroupDBUrsmuObject;
import ru.ursmu.application.Abstraction.IUrsmuDBObject;
import ru.ursmu.beta.application.R;

import java.io.IOException;

public class GroupDBProcessor extends AbstractProcessor {
    IGroupDBUrsmuObject<IUrsmuDBObject> object;
    Context mContext;

    public GroupDBProcessor(IGroupDBUrsmuObject<IUrsmuDBObject> to, ResultReceiver callback, long id, Context c) {
        super(to, callback, id, c);
        object = to;
        mContext = c;
    }

    @Override
    protected Object[] doInBackground(Void... params) {
        Log.d("URSMULOG", "GroupDBProcessor start");
        sendStart();
        ScheduleGroup next = null;
        getDataBaseBehavior().clearTable();


        try {
            object.first();
            //next = (ScheduleGroup) object.next();
        } catch (JSONException e) {
            sendFailure(mContext.getResources().getString(R.string.parse_error));
        } catch (IOException e) {
            sendFailure(mContext.getResources().getString(R.string.null_error));
        }


        Object[] q = null;
        String uri, param, s;
        UrsmuPostDownload down_agent = new UrsmuPostDownload();
        ScheduleParser parse_agent = new ScheduleParser();
        short error_parse = 0;


        while ((next = (ScheduleGroup) object.next()) != null) {
            if (next.getFaculty().equals("ФЗО") || next.getFaculty().equals("ФСПО-з")) {
                continue;
            }
            uri = next.getUri();
            param = next.getParameters();

            try {
                s = down_agent.Download(uri, param);
            } catch (IOException e) {
                sendFailure(mContext.getResources().getString(R.string.network_error));
                return null;
            }

            try {
                q = parse_agent.parse(s);
            } catch (JSONException e) {
                error_parse++;
                if (error_parse > 15) {
                    sendFailure(mContext.getResources().getString(R.string.parse_error));
                    next.getDataBasingBehavior(mContext).clearTable();
                    return null;
                }
                Log.d("URSMULOG", "GroupDBProcessor parse JSONException " + error_parse);
                continue;
            }

            try {
                next.getDataBasingBehavior(mContext).add(q);
            } catch (Exception e) {
                sendFailure(mContext.getResources().getString(R.string.null_error));
                return null;
            }

            sendMiddle(next.getFaculty());
        }


        object.setCheck(mContext);
        super.sendComplete(new String[]{});


        return null;
    }


}