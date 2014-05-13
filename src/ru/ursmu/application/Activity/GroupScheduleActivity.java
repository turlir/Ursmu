package ru.ursmu.application.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import ru.ursmu.application.Abstraction.AbsPush;
import ru.ursmu.application.Abstraction.IUrsmuObject;
import ru.ursmu.application.Abstraction.UniversalCallback;
import ru.ursmu.application.Realization.EducationWeek;
import ru.ursmu.application.Realization.PushReRegister;
import ru.ursmu.application.Realization.PushRegister;
import ru.ursmu.application.Realization.ScheduleGroup;
import ru.ursmu.application.R;

import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;

public class GroupScheduleActivity extends Fragment implements ActionBar.OnNavigationListener {
    private ScheduleGroup mObject;
    private ProgressBar mBar;
    private ServiceHelper mHelper;
    private long mRequestId;
    private ActionBar mParentBar;
    private String mFaculty, mKurs, mGroup;
    private Intent mStartParam;

    public GroupScheduleActivity(ActionBar b, Intent i) {
        mParentBar = b;
        mStartParam = i;
    }

    private UniversalCallback mHandler = new UniversalCallback() {
        @Override
        public void sendError(String notify) {
            changeIndicatorVisible(View.INVISIBLE);
            showNotification(notify);
            mRequestId = 0;
            getActivity().findViewById(R.id.viewpager).setVisibility(View.VISIBLE);
        }

        @Override
        public void sendComplete(Serializable data) {
            changeIndicatorVisible(View.INVISIBLE);
            mRequestId = 0;
            MyPagerAdapter mPagerAdapter = new MyPagerAdapter(getChildFragmentManager(),
                    (EducationWeek) data, getActivity().getBaseContext(), false);

            ViewPager mViewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
            mViewPager.setVisibility(View.VISIBLE);
            mViewPager.setAdapter(mPagerAdapter);
            mViewPager.setCurrentItem(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2, true);

            PagerTabStrip pagerTabStrip = (PagerTabStrip) getActivity().findViewById(R.id.pagerTabStrip);
            pagerTabStrip.setDrawFullUnderline(true);
            pagerTabStrip.setTabIndicatorColor(Color.parseColor("#0099CC"));

            prePushSubscribe();
        }

        @Override
        public void sendStart(long id) {
            changeIndicatorVisible(View.VISIBLE);
            mRequestId = id;
            getActivity().findViewById(R.id.viewpager).setVisibility(View.INVISIBLE);
        }
    };
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.group_schedule, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Log.d("URSMULOG", "GroupScheduleActivity onActivityCreated");
        mFaculty = mStartParam.getStringExtra(ServiceHelper.FACULTY);
        mKurs = mStartParam.getStringExtra(ServiceHelper.KURS);
        mGroup = mStartParam.getStringExtra(ServiceHelper.GROUP);
        boolean isHard = mStartParam.getBooleanExtra(ServiceHelper.IS_HARD, true);

        mObject = new ScheduleGroup(mFaculty, mKurs, mGroup, isHard);
        mContext = getActivity().getApplicationContext();

        start();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        if (itemPosition == 0) {
            Intent i = new Intent(getActivity(), FindFacultyActivity.class);
            startActivity(i);
            return true;
        }

        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.d("URSMULOG", "GroupScheduleActivity onResume " + mGroup);

        String[] list_navigation = new String[]{"Поиск", mGroup};

        mParentBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        ArrayAdapter<String> adapter = new SimpleCustomArrayAdapter(getActivity().getApplicationContext(),
                R.layout.simple_action_menu, list_navigation);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mParentBar.setListNavigationCallbacks(adapter, this);
        mParentBar.setDisplayHomeAsUpEnabled(true);
        mParentBar.setDisplayShowTitleEnabled(false);
        mParentBar.setSelectedNavigationItem(1);
        this.getActivity().supportInvalidateOptionsMenu(); // change navigation mode
    }

    @Override
    public void onPause() {
        super.onPause();
        ScheduleAdapter.clearIconPair();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Log.d("URSMULOG", "onSaveInstanceState");
        outState.putString(ServiceHelper.FACULTY, mFaculty);
        outState.putString(ServiceHelper.KURS, mKurs);
        outState.putString(ServiceHelper.GROUP, mGroup);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            //Log.d("URSMULOG", "onRestoreInstanceState");
            mFaculty = savedInstanceState.getString(ServiceHelper.FACULTY);
            mKurs = savedInstanceState.getString(ServiceHelper.KURS);
            mGroup = savedInstanceState.getString(ServiceHelper.GROUP);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.action_bar_group_schedule, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.schedule_group_update:
                if (mRequestId == 0) {
                    mObject.setHard(true);
                    start();
                    return true;
                } else {
                    Toast.makeText(getActivity().getBaseContext(), "Дождитесь завершения операции",
                            Toast.LENGTH_SHORT).show();
                }
            case R.id.schedule_group_site:
                if (mObject != null) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(mObject.getUri() + "#" + mObject.getParameters()));
                    startActivity(browserIntent);
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void start() {
        if (mHelper == null) {
            mHelper = ServiceHelper.getInstance(mContext);
        }

        mHelper.getUrsmuDBObject(mObject, mHandler);
    }


    //<editor-fold desc="Google Cloud Messages">

    private void prePushSubscribe() {
        if (!checkPlayServices()) {
            Log.i("URSMULOG", "No valid Google Play Services APK found. prePushSubscribe fail");
            return;
        }

        if (mHelper == null) {
            mHelper = ServiceHelper.getInstance(mContext);
        }

        if (mHelper.getBooleanPreference("IS_QUEST_PUSH_SUBSCRIPTION")) {
            DialogInterface.OnClickListener positiveHandler = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    pushSubscribe();
                }
            };

            DialogFragment quest_dialog = new QuestionDialog(positiveHandler,
                    getResources().getString(R.string.subsrc_dialog_title));

            quest_dialog.show(getActivity().getSupportFragmentManager(), "quest_dialog");
            mHelper.setBooleanPreferences("IS_QUEST_PUSH_SUBSCRIPTION", false);
        }
        if (mStartParam.getBooleanExtra("RE_REGISTER", false))
            sendRegistrationIdToBackend(getRegistrationId(mContext), true);
    }

    private void pushSubscribe() {
        //Log.d("URSMULOG", "pushSubscribe");
        String regid = getRegistrationId(getActivity().getApplicationContext());

        if (TextUtils.isEmpty(regid)) {
            registerInBackground();
        }
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                Context context = getActivity().getApplicationContext();
                String regid = null;
                try {
                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);

                    regid = gcm.register(AbsPush.SENDER_ID);

                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    String msg = "Error :" + ex.getMessage();
                    //Log.d("URSMULOG", "registerInBackground " + msg);
                } finally {
                    return regid;
                }
            }

            @Override
            protected void onPostExecute(String msg) {
                if (msg != null) {
                    //Log.d("URSMULOG registerInBackground onPostExecute", msg);
                    sendRegistrationIdToBackend(msg, false);
                } else {
                    //Log.d("URSMULOG", "onPostExecute error msg != null");
                }

            }
        }.execute(null, null, null);
    }

    private void sendRegistrationIdToBackend(String regid, boolean re_register) {
        //Log.d("URSMULOG", "sendRegistrationIdToBackend " + regid);
        UniversalCallback push_reg_callback = new UniversalCallback() {
            @Override
            public void sendError(String notify) {
                //Log.d("URSMULOG", "push_reg_callback sendError" + notify);
            }

            @Override
            public void sendComplete(Serializable data) {
                //Log.d("URSMULOG", "push_reg_callback sendComplete");
            }

            @Override
            public void sendStart(long id) {
                //Log.d("URSMULOG", "push_reg_callback sendStart");
            }
        };

        if (mHelper == null) {
            mHelper = ServiceHelper.getInstance(getActivity().getApplicationContext());
        }

        IUrsmuObject reg_obj;
        if (!re_register) {
            reg_obj = new PushRegister(regid, mFaculty, mGroup);
        } else {
            reg_obj = new PushReRegister(regid, mFaculty, mGroup);
        }

        mHelper.getUrsmuObject(reg_obj, push_reg_callback);
    }

    private void storeRegistrationId(Context context, String regId) {
        int appVersion = getAppVersion(context);
        Log.i("URSMULOG", "Saving regId on app version " + appVersion);
        if (mHelper == null) {
            mHelper = ServiceHelper.getInstance(getActivity().getApplicationContext());
        }

        mHelper.setPreferences(AbsPush.PROPERTY_REG_ID, regId);
        mHelper.setIntPreference(AbsPush.PROPERTY_APP_VERSION, appVersion);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {

                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        AbsPush.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("URSMULOG", "This device is not supported.");
                getActivity().finish();
            }
            return false;
        }
        return true;
    }

    private String getRegistrationId(Context context) {
        if (mHelper == null) {
            mHelper = ServiceHelper.getInstance(getActivity().getApplicationContext());
        }

        String registrationId = mHelper.getPreference(AbsPush.PROPERTY_REG_ID);

        if (TextUtils.isEmpty(registrationId)) {
            Log.i("URSMULOG", "Registration not found.");
            return "";
        }
        int registeredVersion = mHelper.getIntPreference(AbsPush.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i("URSMULOG", "App version changed.");
            return "";
        }
        return registrationId;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
    //</editor-fold>

    protected void changeIndicatorVisible(int visibility) {
        if (mBar == null) {
            mBar = (ProgressBar) getActivity().findViewById(R.id.schedule_bar);
        }
        mBar.setVisibility(visibility);
        if (visibility == View.INVISIBLE) {
            mBar = null;
        }
    }

    private void showNotification(String notify) {
        Toast.makeText(getActivity().getBaseContext(), notify, Toast.LENGTH_SHORT).show();
    }
}