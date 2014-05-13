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

    //Preferences and middle activity intent
    public static final String FACULTY = "FACULTY";
    public static final String KURS = "KURS";
    public static final String GROUP = "GROUP";
    public static final String IS_HARD = "IS_HARD";

    //state processor
    public static final int DOWNLOAD_START = 0;
    public static final int DOWNLOAD_MIDDLE = 1;
    public static final int DOWNLOAD_COMPLETE = 2;
    public static final int DOWNLOAD_FAILURE = 3;
    public static final String PARSE_DATA = "PARSE_DATA";
    public static final String REQUEST_ID = "REQUEST_ID";
    public static final String MIDDLE_NOTIFY = "MIDDLE_NOTIFY";
    public static final String ERROR_NOTIFY = "ERROR_NOTIFY";



    private static final String URSMU_PREFERENCES = "UrsmuPreferences";
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

    public long getUrsmuObject(IUrsmuObject object, final UniversalCallback toActivity) {
        long req_id = generationID();

        setCallback(req_id, toActivity);

        Intent intent = new Intent(mActivity, UrsmuService.class);

        intent.putExtra(CALLBACK, generationCallback());
        intent.putExtra(TRANSFER_OBJECT, object);
        intent.putExtra(IS_DB, 1);
        intent.putExtra(REQUEST_ID, req_id);

        mActivity.startService(intent);

        return req_id;
    }

    public Long getUrsmuDBObject(IUrsmuDBObject object, final UniversalCallback toActivity) {

        long req_id = generationID();

        setCallback(req_id, toActivity);

        ResultReceiver rec = generationCallback();


        Intent intent = new Intent(mActivity, UrsmuService.class);

        intent.putExtra(CALLBACK, rec);
        intent.putExtra(TRANSFER_OBJECT, object);

        intent.putExtra(IS_DB, 2);
        intent.putExtra(REQUEST_ID, req_id);

        mActivity.startService(intent);

        return req_id;
    }

    public Long getGroupDBObjects(IGroupDBUrsmuObject object, final UniversalCallback toActivity) {        //only group add to db
        long req_id = generationID();

        setCallback(req_id, toActivity);

        ResultReceiver rec = generationCallback();

        Intent intent = new Intent(mActivity, UrsmuService.class);

        intent.putExtra(CALLBACK, rec);
        intent.putExtra(TRANSFER_OBJECT, object);
        intent.putExtra(IS_DB, 3);
        intent.putExtra(REQUEST_ID, req_id);

        mActivity.startService(intent);

        return req_id;
    }


    //<editor-fold desc="Callback">
    private ResultReceiver generationCallback() {
        //Log.d("URSMULOG", "ServiceHelper generationCallback");
        //proxy

        IntermediateCallback re = new IntermediateCallback(new Handler());
        re.setReceiver(new IntermediateCallback.Receiver() {
            @Override
            public void onReceiveResult(int resultCode, Bundle data) {
                long id = data.getLong(REQUEST_ID);

                UniversalCallback callback = getCallback(id);

                switch (resultCode) {
                    case DOWNLOAD_COMPLETE:
                        //removeCallback(id);
                        callback.sendComplete(data.getSerializable(ServiceHelper.PARSE_DATA));
                        break;
                    case DOWNLOAD_FAILURE:
                        callback.sendError(data.getString(ServiceHelper.ERROR_NOTIFY));
                        break;
                    case DOWNLOAD_START:
                        callback.sendStart(id);
                        break;
                    case DOWNLOAD_MIDDLE:
                        callback.sendMiddle(data.getString(ServiceHelper.MIDDLE_NOTIFY));
                }

            }
        });

        return re;
    }

     private static void setCallback(long id, UniversalCallback local_callback) {
        handlers.put(id, local_callback);
    }

    public static UniversalCallback getCallback(long id) {
        return handlers.get(id);
    }

    public static void removeCallback(long id) {
        //Log.d("URSMULOG", "ServiceHelper removeCallback");
        handlers.remove(id);
    }

    private static long generationID() {
        return UUID.randomUUID().getLeastSignificantBits();
    }
    //</editor-fold>

    //<editor-fold desc="Preferences">
    public String[] getThreeInfo() {
        return new String[]{
                getPreference(FACULTY),
                getPreference(KURS),
                getPreference(GROUP)
        };
    }

    public String getPreference(String name) {
        return mActivity.getSharedPreferences(URSMU_PREFERENCES, Context.MODE_PRIVATE).getString(name, "");
    }

    public Boolean getBooleanPreference(String name) {
        return mActivity.getSharedPreferences(URSMU_PREFERENCES, Context.MODE_PRIVATE).getBoolean
                (String.valueOf(name), true);
    }

    public void setPreferences(String name, String value) {
        mActivity.getSharedPreferences(URSMU_PREFERENCES, Context.MODE_PRIVATE)
                .edit().
                putString(name, value).commit();
    }

    public void setBooleanPreferences(String name, Boolean value) {
        mActivity.getSharedPreferences(URSMU_PREFERENCES, Context.MODE_PRIVATE)
                .edit().
                putBoolean(name, value).commit();
    }


    public void setThreeInfo(String fac, String kur, String mGroup) {
        mActivity.getSharedPreferences(URSMU_PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putString(FACULTY, fac)
                .putString(KURS, kur)
                .putString(GROUP, mGroup)
                .commit();
    }


    public int getIntPreference(String propertyAppVersion, int minValue) {
        return mActivity.getSharedPreferences(URSMU_PREFERENCES, Context.MODE_PRIVATE).getInt
                (propertyAppVersion, minValue);
    }

    public void setIntPreference(String name, int value) {
        mActivity.getSharedPreferences(URSMU_PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putInt(name, value)
                .commit();
    }
    //</editor-fold>
}