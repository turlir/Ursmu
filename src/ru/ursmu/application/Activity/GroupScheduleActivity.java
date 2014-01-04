package ru.ursmu.application.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import ru.ursmu.application.Abstraction.UniversalCallback;
import ru.ursmu.application.JsonObject.EducationItem;
import ru.ursmu.application.Realization.ScheduleGroup;
import ru.ursmu.beta.application.R;

import java.util.ArrayList;
import java.util.Calendar;

public class GroupScheduleActivity extends SherlockActivity implements ActionBar.OnNavigationListener {
    private ScheduleGroup mObject;
    private ProgressBar mBar;
    private TextView footerText;

    private ViewPager mViewPager;
    private ArrayList<ListView> mPages;
    private MyPagerAdapter mPagerAdapter;

    private long mRequestId;
    private UniversalCallback mHandler = new UniversalCallback() {
        @Override
        public void sendError(String notify) {
            changeIndicatorVisible(View.INVISIBLE);
            showNotification(notify);
        }

        @Override
        public void sendComplete(Object[] data) {
            if (data != null) {
                ListView list_view = new ListView(getApplicationContext());
                ArrayAdapter<EducationItem> list_adapter = new ScheduleAdapter(getApplicationContext(),
                        R.layout.schedule_adapter, (EducationItem[]) data, false);
                list_view.setAdapter(list_adapter);
                registerForContextMenu(list_view);
                mPages.add(list_view);
                mPagerAdapter.notifyDataSetChanged();

                if (mPagerAdapter.getCount() == 6) {
                    changeIndicatorVisible(View.INVISIBLE);
                    mViewPager.setVisibility(View.VISIBLE);
                    current(null);
                    //ServiceHelper.removeCallback(mRequestId);
                }
            }
        }

        @Override
        public void sendStart(long id) {
            changeIndicatorVisible(View.VISIBLE);
        }
    };

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i2) {
        }

        @Override
        public void onPageSelected(int i) {
            changeFooter(i);
        }

        @Override
        public void onPageScrollStateChanged(int i) {
        }
    };
    private ServiceHelper mHelper;

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        if (itemPosition == 0) {
            Intent i = new Intent(this, FindFacultyActivity.class);
            startActivity(i);
        }

        return true;
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_schedule_group);

        Intent info = getIntent();
        mHelper = ServiceHelper.getInstance(getApplicationContext());

        mPages = new ArrayList<ListView>(6);
        mPagerAdapter = new MyPagerAdapter(mPages);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(pageChangeListener);

        String faculty = info.getStringExtra(ServiceHelper.FACULTY);
        String kurs = info.getStringExtra(ServiceHelper.KURS);
        String group = info.getStringExtra(ServiceHelper.GROUP);
        boolean isHard = info.getBooleanExtra("IS_HARD", true);

        String[] list_navigation = new String[2];
        list_navigation[0] = "Поиск";
        list_navigation[1] = group;

        mObject = new ScheduleGroup(faculty, kurs, group, isHard);

        mRequestId = mHelper.getUrsmuDBObject(mObject, mHandler);

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
    protected void onResume() {
        super.onResume();
        getSupportActionBar().setSelectedNavigationItem(1);
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.schedule_item_professor:
                ListAdapter ada = mPages.get(mViewPager.getCurrentItem()).getAdapter();
                String normalProfessor = ((EducationItem) ada.getItem(info.position)).getNormalProfessor();
                if (!TextUtils.isEmpty(normalProfessor)) {
                    Intent i = new Intent(this, ProfessorScheduleActivity.class);
                    i.putExtra("PROFESSOR", normalProfessor);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Выберите пару", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.schedule_item_alarm:
                ListAdapter temp = mPages.get(mViewPager.getCurrentItem()).getAdapter();
                ScheduleAdapter adapter = (ScheduleAdapter) temp;
                adapter.setAlarm(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.schedule_item, menu);
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
                mPages.clear();
                mPagerAdapter = new MyPagerAdapter(mPages);
                mViewPager.setAdapter(mPagerAdapter);

                mObject.setHard(true);
                mHelper.getUrsmuDBObject(mObject, mHandler);
                return true;
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

    @Override
    protected void onPause() {
        super.onStop();
        ScheduleAdapter.clearIconPair();
    }

    public void previous(View v) {
        if (mViewPager != null) {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
        }
    }

    public void next(View v) {
        if (mViewPager != null) {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
        }
    }

    public void current(View v) {
        if (mViewPager != null) {
            mViewPager.setCurrentItem(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2, true);
        }
    }

    @Override
    protected void showNotification(String notify) {
        Toast.makeText(getApplicationContext(), notify, Toast.LENGTH_LONG).show();
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

    private void changeFooter(int i) {
        if (footerText == null) {
            footerText = (TextView) findViewById(R.id.schedule_name_day);
        }
        int n = ((ScheduleAdapter) mPagerAdapter.getItem(i).getAdapter()).getItem(0).getDayOfTheWeek();
        footerText.setText(EducationItem.DayOfTheWeek[n]);
    }
}