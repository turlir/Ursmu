package ru.ursmu.application.Activity;

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
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import ru.ursmu.application.Abstraction.IUrsmuObject;
import ru.ursmu.application.Abstraction.UniversalCallback;
import ru.ursmu.application.Realization.EducationWeek;
import ru.ursmu.application.Realization.PushReRegister;
import ru.ursmu.application.Realization.PushRegister;
import ru.ursmu.application.Realization.ScheduleGroup;
import ru.ursmu.beta.application.R;

import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;

public class GroupScheduleActivity extends ActionBarActivity implements ActionBar.OnNavigationListener {
    private ScheduleGroup mObject;
    private ProgressBar mBar;
    private ServiceHelper mHelper;
    private long mRequestId;
    private String mFaculty;

    private UniversalCallback mHandler = new UniversalCallback() {
        @Override
        public void sendError(String notify) {
            changeIndicatorVisible(View.INVISIBLE);
            showNotification(notify);
            mRequestId = 0;
            findViewById(R.id.viewpager).setVisibility(View.VISIBLE);
        }

        @Override
        public void sendComplete(Serializable data) {
            changeIndicatorVisible(View.INVISIBLE);
            mRequestId = 0;
            MyPagerAdapter pager_adapter = new MyPagerAdapter(getSupportFragmentManager(), (EducationWeek) data, getApplicationContext(), false);

            ViewPager view_pager = (ViewPager) findViewById(R.id.viewpager);
            view_pager.setVisibility(View.VISIBLE);
            view_pager.setAdapter(pager_adapter);
            view_pager.setCurrentItem(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2, true);

            PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pagerTabStrip);
            pagerTabStrip.setDrawFullUnderline(true);
            pagerTabStrip.setTabIndicatorColor(Color.parseColor("#0099CC"));

            prePushSubscribe();
        }

        @Override
        public void sendStart(long id) {
            changeIndicatorVisible(View.VISIBLE);
            mRequestId = id;
            findViewById(R.id.viewpager).setVisibility(View.INVISIBLE);
        }
    };
    private String mGroup;
    private Context mContext;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_schedule);

        Intent info = getIntent();
        mFaculty = info.getStringExtra(ServiceHelper.FACULTY);
        String kurs = info.getStringExtra(ServiceHelper.KURS);
        mGroup = info.getStringExtra(ServiceHelper.GROUP);
        boolean isHard = info.getBooleanExtra("IS_HARD", true);

        mObject = new ScheduleGroup(mFaculty, kurs, mGroup, isHard);
        mContext = getApplicationContext();
        start();

        String[] list_navigation = new String[]{"Поиск", mGroup};

        ActionBar bar = getSupportActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        ArrayAdapter<String> adapter = new SimpleCustomArrayAdapter(this,
                R.layout.simple_action_menu, list_navigation);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bar.setListNavigationCallbacks(adapter, this);
        bar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        if (itemPosition == 0) {
            Intent i = new Intent(this, FindFacultyActivity.class);
            startActivity(i);
            return true;
        }

        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().setSelectedNavigationItem(1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ScheduleAdapter.clearIconPair();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_group_schedule, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.schedule_group_update:
                if (mRequestId == 0) {
                    mObject.setHard(true);
                    start();
                    return true;
                } else {
                    Toast.makeText(getApplicationContext(), "Дождитесь завершения операции", Toast.LENGTH_SHORT).show();
                }
            case R.id.schedule_group_site:
                if (mObject != null) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mObject.getUri() + "#" + mObject.getParameters()));
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

        String current_id;
        if (mHelper.getBooleanPreference("IS_QUEST_PUSH_SUBSCRIPTION")) {
            DialogInterface.OnClickListener positiveHandler = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    pushSubscribe();
                }
            };

            DialogFragment quest_dialog = new QuestionDialog(positiveHandler,
                    getResources().getString(R.string.subsrc_dialog_title), getResources().getString(R.string.subsrc_dialog_desc));

            quest_dialog.show(getSupportFragmentManager(), "quest_dialog");
            mHelper.setBooleanPreferences("IS_QUEST_PUSH_SUBSCRIPTION", false);

        } else if (getIntent().getBooleanExtra("RE_REGISTER", false) && !(current_id = getRegistrationId(mContext)).equals("")) {
            Log.d("URSMULOG", "GroupScheduleActivity push re Register init");
            sendRegistrationIdToBackend(current_id, true);
        }
    }

    private void pushSubscribe() {
        Log.d("URSMULOG", "pushSubscribe");
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String regid = getRegistrationId(getApplicationContext());

        if (TextUtils.isEmpty(regid) ||
                !ApplicationVersionHelper.isApplicationVersionCodeEqualsSavedApplicationVersionCode(getApplicationContext())) {
            registerInBackground();
        }
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                Context context = getApplicationContext();
                String regid = null;
                try {
                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);

                    regid = gcm.register(ServiceHelper.SENDER_ID);

                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    String msg = "Error :" + ex.getMessage();
                    Log.d("URSMULOG", "registerInBackground " + msg);
                } finally {
                    return regid;
                }
            }

            @Override
            protected void onPostExecute(String msg) {
                if (msg != null) {
                    Log.d("URSMULOG registerInBackground onPostExecute", msg);
                    sendRegistrationIdToBackend(msg, false);
                } else
                    Log.d("URSMULOG", "onPostExecute error msg != null");
            }
        }.execute(null, null, null);
    }

    private void sendRegistrationIdToBackend(String regid, boolean re_register) {
        Log.d("URSMULOG", "sendRegistrationIdToBackend " + regid);
        UniversalCallback push_reg_callback = new UniversalCallback() {
            @Override
            public void sendError(String notify) {
                Log.d("URSMULOG", "push_reg_callback sendError" + notify);
            }

            @Override
            public void sendComplete(Serializable data) {
                Log.d("URSMULOG", "push_reg_callback sendComplete");
            }

            @Override
            public void sendStart(long id) {
                Log.d("URSMULOG", "push_reg_callback sendStart");
            }
        };

        if (mHelper == null) {
            mHelper = ServiceHelper.getInstance(getApplicationContext());
        }

        IUrsmuObject reg_obj = null;
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
            mHelper = ServiceHelper.getInstance(getApplicationContext());
        }

        mHelper.setPreferences(ServiceHelper.PROPERTY_REG_ID, regId);
        mHelper.setIntPreference(ServiceHelper.PROPERTY_APP_VERSION, appVersion);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {

                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        ServiceHelper.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("URSMULOG", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private String getRegistrationId(Context context) {
        if (mHelper == null) {
            mHelper = ServiceHelper.getInstance(getApplicationContext());
        }

        String registrationId = mHelper.getPreference(ServiceHelper.PROPERTY_REG_ID);

        if (TextUtils.isEmpty(registrationId)) {
            Log.i("URSMULOG", "Registration not found.");
            return "";
        }
        int registeredVersion = mHelper.getIntPreference(ServiceHelper.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
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
            mBar = (ProgressBar) findViewById(R.id.schedule_bar);
        }
        mBar.setVisibility(visibility);
        if (visibility == View.INVISIBLE) {
            mBar = null;
        }
    }

    private void showNotification(String notify) {
        Toast.makeText(getApplicationContext(), notify, Toast.LENGTH_SHORT).show();
    }
}