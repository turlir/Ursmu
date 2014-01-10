package ru.ursmu.application.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import ru.ursmu.application.Abstraction.UniversalCallback;
import ru.ursmu.application.Realization.EducationWeek;
import ru.ursmu.application.Realization.ScheduleGroup;
import ru.ursmu.beta.application.R;

import java.io.Serializable;
import java.util.Calendar;

public class GroupScheduleActivity extends SherlockFragmentActivity implements ActionBar.OnNavigationListener {
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
        }

        @Override
        public void sendComplete(Serializable data) {
            changeIndicatorVisible(View.INVISIBLE);
            mRequestId = 0;
            if (data != null) {
                MyPagerAdapter pager_adapter = new MyPagerAdapter(getSupportFragmentManager(), (EducationWeek) data, getApplicationContext(), false);

                ViewPager view_pager = (ViewPager) findViewById(R.id.viewpager);
                view_pager.setVisibility(View.VISIBLE);
                view_pager.setCurrentItem(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2, true);
                view_pager.setAdapter(pager_adapter);

                PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pagerTabStrip);
                pagerTabStrip.setDrawFullUnderline(true);
                pagerTabStrip.setTabIndicatorColor(Color.parseColor("#0099CC"));
            }
        }

        @Override
        public void sendStart(long id) {
            changeIndicatorVisible(View.VISIBLE);
            mRequestId = id;
        }
    };

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        if (itemPosition == 0) {
            Intent i = new Intent(this, FindFacultyActivity.class);
            startActivity(i);
            return true;
        }

        return false;
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_schedule_group);

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


    private void start() {
        if (mHelper == null) {
            mHelper = ServiceHelper.getInstance(getApplicationContext());
        }

        mHelper.getUrsmuDBObject(mObject, mHandler);
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
        getSupportMenuInflater().inflate(R.menu.action_bar_schedule, menu);
        MenuItem item = menu.findItem(R.id.schedule_group_button);
        com.actionbarsherlock.widget.ShareActionProvider provider = (com.actionbarsherlock.widget.ShareActionProvider) item.getActionProvider();
        provider.setShareHistoryFileName(com.actionbarsherlock.widget.ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
        provider.setShareIntent(createShareIntent());

        return true;
    }

    private Intent createShareIntent() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setAction(Intent.ACTION_SEND);
        i.setType("text/plain");
        String text;
        if (mObject != null) {
            text = "Мое распиание на сайте УГГУ " + mObject.getUri() + "#" + mObject.getParameters();
        } else
            text = "Мое распиание на сайте УГГУ http://rasp.ursmu.ru";
        i.putExtra(Intent.EXTRA_TEXT, text);
        return i;
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

    protected void changeIndicatorVisible(int visibility) {
        if (mBar == null) {
            mBar = (ProgressBar) findViewById(R.id.schedule_bar);
        }
        mBar.setVisibility(visibility);
        if (visibility == View.INVISIBLE) {
            mBar = null;
        }
    }
}