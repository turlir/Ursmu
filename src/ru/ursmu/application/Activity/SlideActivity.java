package ru.ursmu.application.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import ru.ursmu.application.Abstraction.DrawerItem;
import ru.ursmu.application.Realization.EntryDrawer;
import ru.ursmu.application.Realization.SectionDrawer;
import ru.ursmu.beta.application.R;

import java.util.ArrayList;

public class SlideActivity extends ActionBarActivity {
    private ArrayList<DrawerItem> mSlideItems;
    private DrawerLayout mDrawerLayout;
    private View mDrawerView;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private Intent mStartGroupScheduleParam;

    private AdapterView.OnItemClickListener mSlideMenuItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    };
    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);  //before adding content
        setContentView(R.layout.slide_activity);
        mTitle = getApplication().getString(R.string.app_name);

        mSlideItems = new ArrayList<DrawerItem>(6);
        mSlideItems.add(new SectionDrawer("Деканат"));
        mSlideItems.add(new EntryDrawer("Кафедры", R.drawable.chair));
        mSlideItems.add(new EntryDrawer("Новости", R.drawable.news_icon));

        mSlideItems.add(new SectionDrawer("Расписание"));
        mSlideItems.add(new EntryDrawer("Группы", R.drawable.new_social_group));
        mSlideItems.add(new EntryDrawer("Профессора", R.drawable.new_social_person));

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerView = findViewById(R.id.drawer_view);
        mDrawerList = (ListView) mDrawerView.findViewById(R.id.left_drawer);

        mDrawerList.setAdapter(new SlideMenuAdapter(getBaseContext(), mSlideItems));
        mDrawerList.setOnItemClickListener(mSlideMenuItemClickListener);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.drawable.ic_navigation_drawer,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mTitle);
                supportInvalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        analyzeStartIntent();
    }

    private void analyzeStartIntent() {
        Intent info = getIntent();
        if (info == null) return;
        String f = info.getStringExtra(ServiceHelper.FACULTY);
        String k = info.getStringExtra(ServiceHelper.KURS);
        String g = info.getStringExtra(ServiceHelper.GROUP);
        boolean h = info.getBooleanExtra(ServiceHelper.IS_HARD, true);
        boolean reg = info.getBooleanExtra("RE_REGISTER", false);
        if (f != null && k != null && g != null) {
            Log.d("URSMULOG", "proxy start GroupScheduleActivity");
            mStartGroupScheduleParam = info;
            selectItem(4);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectItem(int position) {
        mDrawerList.setItemChecked(position, true);
        mDrawerList.setSelection(position);
        mTitle = ((EntryDrawer) mSlideItems.get(position)).getText();
        setTitle(mTitle);
        mDrawerLayout.closeDrawer(mDrawerView);

        openActivity(position);
    }

    private void openActivity(int position) {
        if (mDrawerList.getSelectedItemPosition() == position) {
            return;
        }
        Fragment fragment = null;
        switch (position) {
            case 1:      //кафедры
                fragment = new ChairActivity(getSupportActionBar());
                break;

            case 2:      //новости
                fragment = new NewsActivity(getSupportActionBar());
                break;

            case 4:     //группы
                fragment = openGroupSchedule();
                break;

            case 5:     //профессора
                break;
        }

        if (fragment != null) {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        }
    }

    private Fragment openGroupSchedule() {
        Fragment fragment;
        ServiceHelper helper = ServiceHelper.getInstance(getApplicationContext());
        if (TextUtils.isEmpty(helper.getPreference(ServiceHelper.GROUP))) {
            Intent i = new Intent(this, FindFacultyActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            return null;
        }

        Intent i;
        if (mStartGroupScheduleParam == null) {
            i = new Intent(this, GroupScheduleActivity.class);
            String[] info = helper.getThreeInfo();
            i.putExtra(ServiceHelper.IS_HARD, false);
            i.putExtra(ServiceHelper.FACULTY, info[0]);
            i.putExtra(ServiceHelper.KURS, info[1]);
            i.putExtra(ServiceHelper.GROUP, info[2]);
        } else {
            i = mStartGroupScheduleParam;
        }
        fragment = new GroupScheduleActivity(getSupportActionBar(), i);
        return fragment;
    }

    @Override
    public void setTitle(CharSequence title) {
        ActionBar b = getSupportActionBar();
        b.setNavigationMode(ActionBar.DISPLAY_SHOW_TITLE);
        b.setDisplayShowTitleEnabled(true);
        b.setTitle(mTitle);
        //supportInvalidateOptionsMenu();
    }
}