package ru.ursmu.application.Abstraction;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import ru.ursmu.application.Activity.ServiceHelper;
import ru.ursmu.application.Realization.UrsmuPostDownload;

import java.io.Serializable;

public abstract class AbstractProcessor extends AsyncTask<Void, Void, Object[]> {
    private IParserBehavior mParseAgent;
    private IDownloadBehavior mDownloadAgent = new UrsmuPostDownload();  //default
    private IDatabasingBehavior mDataBaseAgent;
    private ResultReceiver mCallback;
    private Long mReqId;

    public AbstractProcessor(IUrsmuObject object, Long RequestID, ResultReceiver receiver, Context context) {
        if (object == null || RequestID == null || receiver == null)
            throw new IllegalArgumentException("object == null || RequestID == null || receiver == null");
        mParseAgent = object.getParseBehavior();
        mCallback = receiver;
        mReqId = RequestID;
    }

    public AbstractProcessor(IUrsmuDBObject object, Long RequestID, ResultReceiver receiver, Context context) {
        if (object == null)
            throw new IllegalArgumentException("parseAgent == null || object == null");
        mParseAgent = object.getParseBehavior();
        mCallback = receiver;
        mReqId = RequestID;
        mDataBaseAgent = object.getDataBasingBehavior(context);
    }

    public AbstractProcessor(IGroupDBUrsmuObject<IUrsmuDBObject> object, ResultReceiver callback, long id, Context context) {
        mCallback = callback;
        mReqId = id;
        mDataBaseAgent = object.getDataBasingBehavior(context);
    }

    @Override
    protected void onPostExecute(Object[] objects) {
        super.cancel(true);
        super.onPostExecute(objects);
        onCancelled();
        //Log.d("URSMULOG", "onPostExecute isCancelled " + isCancelled());
        ServiceHelper.removeCallback(mReqId);
    }

    public void sendStart() {
        //Log.d("URSMULOG", "AbstractProcessor sendStart()");
        Bundle b = new Bundle(1);
        b.putLong(ServiceHelper.REQUEST_ID, mReqId);
        mCallback.send(ServiceHelper.DOWNLOAD_START, b);
    }

    public void sendFailure(String msg) {
        //Log.d("URSMULOG", "AbstractProcessor sendFailure() " + msg);
        Bundle b = new Bundle(1);
        b.putLong(ServiceHelper.REQUEST_ID, mReqId);
        b.putString(ServiceHelper.ERROR_NOTIFY, msg);
        mCallback.send(ServiceHelper.DOWNLOAD_FAILURE, b);
    }

    public void sendComplete(Object data) {
        //Log.d("URSMULOG", "AbstractProcessor sendComplete()");
        Bundle bundle = new Bundle(2);
        bundle.putLong(ServiceHelper.REQUEST_ID, mReqId);
        bundle.putSerializable(ServiceHelper.PARSE_DATA, (Serializable) data);
        mCallback.send(ServiceHelper.DOWNLOAD_COMPLETE, bundle);
    }

    public void sendComplete(Serializable[] data) {
        //Log.d("URSMULOG", "AbstractProcessor sendComplete()");
        Bundle bundle = new Bundle(2);
        bundle.putLong(ServiceHelper.REQUEST_ID, mReqId);
        bundle.putSerializable(ServiceHelper.PARSE_DATA, data);
        mCallback.send(ServiceHelper.DOWNLOAD_COMPLETE, bundle);
    }

    public void sendMiddle(String msg) {
        Bundle b = new Bundle(1);
        b.putLong(ServiceHelper.REQUEST_ID, mReqId);
        b.putString(ServiceHelper.MIDDLE_NOTIFY, msg);
        mCallback.send(ServiceHelper.DOWNLOAD_MIDDLE, b);
    }

    public IDownloadBehavior getDownloadBehavior() {
        return mDownloadAgent;
    }

    public IParserBehavior getParseBehavior() {
        return mParseAgent;
    }

    public IDatabasingBehavior getDataBaseBehavior() {
        return mDataBaseAgent;
    }


}
