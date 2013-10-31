package ru.ursmu.application.Realization;

import android.content.Context;
import android.os.ResultReceiver;
import android.util.Log;
import org.json.JSONException;
import ru.ursmu.application.Abstraction.AbstractProcessor;
import ru.ursmu.application.Abstraction.IGroupDBUrsmuObject;
import ru.ursmu.application.Abstraction.IUrsmuDBObject;
import ru.ursmu.application.JsonObject.EducationItem;

import java.io.IOException;
import java.util.ArrayList;

public class GroupDBProcessor extends AbstractProcessor {

    IGroupDBUrsmuObject<IUrsmuDBObject> object;
    Context mContext;
    //ResultReceiver mCallback;
    //Long mReqId;

    public GroupDBProcessor(IGroupDBUrsmuObject<IUrsmuDBObject> to, ResultReceiver callback, long id, Context c) {
        super(to, callback, id, c);
        object = to;
        mContext = c;
        //mCallback = callback;
        //mReqId = id;
    }

    @Override
    protected Object[] doInBackground(Void... params) {
        Log.d("URSMULOG", "GroupDBProcessor start");

        sendStart();

        GroupDataBasing dbAgent = GroupDataBasing.getInstance(mContext, null, null, null);
        object.clearDB(dbAgent);
        dbAgent.close();

        try {
            boolean first = object.first();
            ScheduleGroup next;
            if (first) {

                ArrayList<EducationItem> q;
                String uri, param, s;
                UrsmuPostDownload down_agent = new UrsmuPostDownload();
                ScheduleParser parse_agent = new ScheduleParser();

                while ((next = (ScheduleGroup) object.next()) != null) {
                    uri = next.getUri();
                    param = next.getParameters();


                    s = down_agent.Download(uri, param);
                    q = parse_agent.parseTwo(s);

                    next.getDataBasingBehavior(mContext).add(q);
                }

                object.setCheck(mContext);
                super.sendComplete(new String[]{});
            }
        } catch (IOException e) {
            sendFailure(e.getMessage());
        } catch (JSONException e) {
            sendFailure(e.getMessage());
        }
        return null;
    }

}