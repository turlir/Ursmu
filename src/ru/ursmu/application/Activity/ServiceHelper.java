package ru.ursmu.application.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import ru.ursmu.application.Abstraction.*;

import java.util.HashMap;
import java.util.UUID;

public class ServiceHelper {
    public static final String IS_DB = "IS_DB";
    public static final String CALLBACK = "CALLBACK";
    public static final String TRANSFER_OBJECT = "TRANSFER_OBJECT";

    public static final String FACULTY = "FACULTY";
    public static final String KURS = "KURS";
    public static final String GROUP = "GroupName";

    //state processor
    public static final int DOWNLOAD_START = 3;
    public static final int DOWNLOAD_COMPLETE = 0;
    public static final int PROCESSOR_FAILURE = 1;
    public static final String PARSE_DATA = "PARSE_DATA";
    public static final String REQUEST_ID = "REQUEST_ID";

    private static ServiceHelper ourInstance = new ServiceHelper();
    private static Context mActivity;
    private static HashMap<Long, UniversalCallback> handlers = new HashMap<Long, UniversalCallback>();

    public static ServiceHelper getInstance(Context applicationContext) {
        if (applicationContext == null)
            throw new IllegalArgumentException();

        mActivity = applicationContext;
        return ourInstance;
    }

    private ServiceHelper() {

    }

    public long getUrsmuObject(IUrsmuObject object, final UniversalCallback toActivity) {     //not from the database
        long req_id = getnerationID();

        addListener(req_id, toActivity);

        Intent intent = new Intent(mActivity, UrsmuService.class);

        intent.putExtra(CALLBACK, generationNormalCallback());
        intent.putExtra(TRANSFER_OBJECT, object);
        intent.putExtra(IS_DB, 1);
        intent.putExtra(REQUEST_ID, req_id);

        mActivity.startService(intent);

        return req_id;
    }


    public long getUrsmuDBObject(IUrsmuDBObject object, final UniversalCallback toActivity) {

        long req_id = getnerationID();

        addListener(req_id, toActivity);

        ResultReceiver rec = generationDBCallback();


        Intent intent = new Intent(mActivity, UrsmuService.class);

        intent.putExtra(CALLBACK, rec);
        intent.putExtra(TRANSFER_OBJECT, object);

        intent.putExtra(IS_DB, 2);
        intent.putExtra(REQUEST_ID, req_id);

        mActivity.startService(intent);

        return req_id;
    }


    public Long setGroupDBObjects(IGroupDBUrsmuObject object, final UniversalCallback toActivity) {        //only group add to db
        long req_id = getnerationID();

        addListener(req_id, toActivity);

        ResultReceiver rec = generationDBCallback();

        Intent intent = new Intent(mActivity, UrsmuService.class);

        intent.putExtra(CALLBACK, rec);
        intent.putExtra(TRANSFER_OBJECT, object);
        intent.putExtra(IS_DB, 3);
        intent.putExtra(REQUEST_ID, req_id);

        mActivity.startService(intent);

        return req_id;
    }

    public Long getGroupObjects(IGroupUrsmuObject<IUrsmuObject> objects, final UniversalCallback toActivity) {

        long req_id = getnerationID();

        addListener(req_id, toActivity);

        ResultReceiver rec = generationNormalCallback();

        Intent intent = new Intent(mActivity, UrsmuService.class);

        intent.putExtra(CALLBACK, rec);
        intent.putExtra(TRANSFER_OBJECT, objects);
        intent.putExtra(IS_DB, 4);
        intent.putExtra(REQUEST_ID, req_id);

        mActivity.startService(intent);

        return req_id;
    }

    private ResultReceiver generationNormalCallback() {
        Log.d("URSMULOG", "ServiceHelper generationNormalCallback");
        //proxy

        IntermediateCallback re = new IntermediateCallback(new Handler());
        re.setReceiver(new IntermediateCallback.Receiver() {
            @Override
            public void onReceiveResult(int resultCode, Bundle data) {
                long id = data.getLong("REQUEST_ID");

                UniversalCallback callback = getCallback(id);

                switch (resultCode) {
                    case DOWNLOAD_COMPLETE:
                        //removeCallback(id);
                        callback.sendComplete((Object[]) data.getSerializable(ServiceHelper.PARSE_DATA));
                        break;
                    case PROCESSOR_FAILURE:
                        callback.sendError(data.getString("ERROR_NOTIF"));
                        break;
                    case DOWNLOAD_START:
                        callback.sendStart(id);
                }

            }
        });

        return re;
    }

    private ResultReceiver generationDBCallback() {
        Log.d("URSMULOG", "ServiceHelper generationDBCallback");
        //proxy

        IntermediateCallback re = new IntermediateCallback(new Handler());
        re.setReceiver(new IntermediateCallback.Receiver() {
            @Override
            public void onReceiveResult(int resultCode, Bundle data) {
                long id = data.getLong("REQUEST_ID");

                UniversalCallback callback = getCallback(id);

                switch (resultCode) {
                    case DOWNLOAD_COMPLETE:
                        //removeCallback(id);
                        callback.sendComplete(data.getParcelableArray(ServiceHelper.PARSE_DATA));
                        break;
                    case PROCESSOR_FAILURE:
                        callback.sendError(data.getString("ERROR_NOTIF"));
                        break;
                    case DOWNLOAD_START:
                        callback.sendStart(id);
                }

            }
        });

        return re;
    }

    public String[] getThreeInfo() {
        return new String[]{
                getPreference(FACULTY),
                getPreference(KURS),
                getPreference(GROUP)
        };
    }

    static private void addListener(long id, UniversalCallback local_callback) {
        handlers.put(id, local_callback);
    }

    public UniversalCallback getCallback(long id) {
        return handlers.get(id);
    }

    static public void removeCallback(long id) {
        Log.d("URSMULOG", "ServiceHelper removeCallback");
        handlers.remove(id);
    }

    static long getnerationID() {
        return UUID.randomUUID().getLeastSignificantBits();
    }

    public String getPreference(String name) {
        return mActivity.getSharedPreferences("UrsmuPreferences", Context.MODE_PRIVATE).getString(name, "");
    }

    public Boolean getBooleanPreference(String name) {
        return mActivity.getSharedPreferences("UrsmuPreferences", Context.MODE_PRIVATE).getBoolean
                (String.valueOf(name), true);
    }

    public void setPreferences(String name, String value) {
        mActivity.getSharedPreferences("UrsmuPreferences", Context.MODE_PRIVATE)
                .edit().
                putString(name, value).commit();
    }

    public void setBooleanPreferences(String name, Boolean value) {
        mActivity.getSharedPreferences("UrsmuPreferences", Context.MODE_PRIVATE)
                .edit().
                putBoolean(name, value).commit();
    }


    public void setThreeInfo(String fac, String kur, String mGroup) {
        mActivity.getSharedPreferences("UrsmuPreferences", Context.MODE_PRIVATE)
                .edit()
                .putString(FACULTY, fac)
                .putString(KURS, kur)
                .putString(GROUP, mGroup)
                .commit();
    }


}
