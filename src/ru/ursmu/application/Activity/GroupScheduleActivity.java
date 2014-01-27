package ru.ursmu.application.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;
import ru.ursmu.application.Abstraction.UniversalCallback;
import ru.ursmu.application.Realization.EducationWeek;
import ru.ursmu.application.Realization.ScheduleGroup;
import ru.ursmu.beta.application.R;

import java.io.Serializable;
import java.util.Calendar;

public class GroupScheduleActivity extends ActionBarActivity implements ActionBar.OnNavigationListener {
    private ScheduleGroup mObject;
    private ProgressBar mBar;
    private ServiceHelper mHelper;
    private long mRequestId;

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
        }

        @Override
        public void sendStart(long id) {
            changeIndicatorVisible(View.VISIBLE);
            mRequestId = id;
            findViewById(R.id.viewpager).setVisibility(View.INVISIBLE);
        }
    };


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_schedule);

        Intent info = getIntent();
        String faculty = info.getStringExtra(ServiceHelper.FACULTY);
        String kurs = info.getStringExtra(ServiceHelper.KURS);
        String group = info.getStringExtra(ServiceHelper.GROUP);
        boolean isHard = info.getBooleanExtra("IS_HARD", true);

        mObject = new ScheduleGroup(faculty, kurs, group, isHard);
        start();

        String[] list_navigation = new String[]{"Поиск", group};

        ActionBar bar = getSupportActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        ArrayAdapter<String> adapter = new SimpleCustomArrayAdapter<String>(this,
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
            mHelper = ServiceHelper.getInstance(getApplicationContext());
        }

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
        }


        mHelper.getUrsmuDBObject(mObject, mHandler);
    }

    private void pushSubscribe() {
        Log.d("URSMULOG", "pushSubscribe");
        mHelper.setBooleanPreferences("IS_QUEST_PUSH_SUBSCRIPTION", false);
    }


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