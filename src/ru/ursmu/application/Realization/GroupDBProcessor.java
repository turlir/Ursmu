package ru.ursmu.application.Realization;

import android.content.Context;
import android.os.ResultReceiver;
import android.util.Log;
import org.json.JSONException;
import ru.ursmu.application.Abstraction.AbstractProcessor;
import ru.ursmu.application.Abstraction.IGroupDBUrsmuObject;
import ru.ursmu.application.Abstraction.IUrsmuDBObject;
import ru.ursmu.application.R;

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

        getDataBaseBehavior().clearTable();

        try {
            object.first();
            ScheduleGroup next;

            Object[] q;
            String uri, param, s;
            UrsmuPostDownload down_agent = new UrsmuPostDownload();
            ScheduleParser parse_agent = new ScheduleParser();

            while ((next = (ScheduleGroup) object.next()) != null) {
                if (!next.getGroup().equals("ПБ.к-11з") && !next.getGroup().equals("ГИГ-4з") && !next.getGroup().equals("ГИГ-5з")) {
                    uri = next.getUri();
                    param = next.getParameters();

                    s = down_agent.Download(uri, param);
                    q = parse_agent.parse(s);

                    next.getDataBasingBehavior(mContext).add(q);

                    sendMiddle(next.getFaculty());
                }
            }

            object.setCheck(mContext);
            super.sendComplete(new String[]{});

        } catch (IOException ex) {
            sendFailure(mContext.getResources().getString(R.string.network_error));
            ex.printStackTrace();
        } catch (JSONException ex) {
            sendFailure(mContext.getResources().getString(R.string.parse_error));
            ex.printStackTrace();
        } catch (Exception ex) {
            sendFailure(mContext.getResources().getString(R.string.null_error));
            ex.printStackTrace();
        }

        return null;
    }


}