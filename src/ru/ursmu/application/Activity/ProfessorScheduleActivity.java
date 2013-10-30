package ru.ursmu.application.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import ru.ursmu.application.Abstraction.UniversalCallback;
import ru.ursmu.application.JsonObject.EducationItem;
import ru.ursmu.application.R;
import ru.ursmu.application.Realization.ProfessorSchedule;
import ru.ursmu.application.Realization.ScheduleGroupFactory;

import java.util.ArrayList;

public class ProfessorScheduleActivity extends SherlockFragmentActivity implements SearchView.OnQueryTextListener, SearchView.OnSuggestionListener {
    String mProfessor;
    ServiceHelper mHelper;
    private ProgressBar mBar;
    private TextView mDesc;
    private ViewPager mViewPager;
    private ArrayList<ListView> mPages;
    private MyPagerAdapter mPagerAdapter;
    private TextView footerText;
    private boolean light = false;
    private SearchView mSearchView;
    private static Long mRequestId;

    private UniversalCallback mHandlerDialog = new UniversalCallback() {
        @Override
        public void sendError(String notify) {
            changeIndicatorVisible(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Обновление завершено с ошибкой " + notify,
                    Toast.LENGTH_LONG).show();
            changeDescText(null);
            mRequestId = null;
        }

        @Override
        public void sendComplete(Object[] data) {
            Toast.makeText(getApplicationContext(), "Обновление завершено успешно", Toast.LENGTH_LONG).show();
            changeIndicatorVisible(View.INVISIBLE);
            changeDescText(null);
            light = true;
            invalidateOptionsMenu();
            nextStep();
        }

        @Override
        public void sendStart(long id) {
            changeIndicatorVisible(View.VISIBLE);
            changeDescText("Выполняется обновление");
            mRequestId = id;
        }
    };


    private UniversalCallback mHandlerTwo = new UniversalCallback() {
        @Override
        public void sendError(String notify) {
            Toast.makeText(getApplicationContext(), notify, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void sendComplete(Object[] data) {
            if (data != null) {
                changeIndicatorVisible(View.INVISIBLE);
                ListView list_view = new ListView(getApplicationContext());
                ArrayAdapter<EducationItem> list_adapter = new ScheduleAdapter(getApplicationContext(),
                        R.layout.schedule_adapter, (EducationItem[]) data, true);
                list_view.setAdapter(list_adapter);
                registerForContextMenu(list_view);
                mPages.add(list_view);
                mPagerAdapter.notifyDataSetChanged();


                changeIndicatorVisible(View.INVISIBLE);
                mViewPager.setVisibility(View.VISIBLE);
                changeFooter(mViewPager.getCurrentItem());
            }
        }

        @Override
        public void sendStart(long id) {
            mRequestId = id;
            changeIndicatorVisible(View.VISIBLE);
            changeDescText(null);
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


    //<editor-fold desc="SearchSuggestion">
    @Override
    public boolean onQueryTextSubmit(String query) {
        Toast.makeText(getApplicationContext(), "Выбрите элемент из списка", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() > 4) {
            ProfessorSchedule prof = new ProfessorSchedule(newText.toLowerCase());
            Cursor suggestion = prof.getDataBasingBehavior(getApplicationContext()).get();

            SuggestionsAdapter adapter = new SuggestionsAdapter(getSupportActionBar().getThemedContext(),
                    suggestion, "normalProfessor", R.drawable.white_social_person);
            mSearchView.setSuggestionsAdapter(adapter);
            return true;
        } else
            return false;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        //mCursor.moveToPosition(position);
        //mProfessor = mCursor.getString(mCursor.getColumnIndexOrThrow("normalProfessor"));
        mProfessor = ((SuggestionsAdapter) mSearchView.getSuggestionsAdapter()).getString(position);
        Log.d("URSMULOG", mProfessor);
        nextStep();
        mSearchView.clearFocus();
        return true;
    }
    //</editor-fold>


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.professor_schedule);
        mHelper = ServiceHelper.getInstance(getApplicationContext());

        ScheduleGroupFactory object = new ScheduleGroupFactory();

        mProfessor = getIntent().getStringExtra("PROFESSOR");

        if (!(light = object.check(getApplicationContext()))) {
            startUpdateDialog();
        } else {
            nextStep();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mBar != null) {
            outState.putInt("PROGRESS_BAR_STATE", mBar.getVisibility());
        }
        if (mDesc != null) {
            outState.putString("DESC_BAR_TEXT", (String) mDesc.getText());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        changeIndicatorVisible(savedInstanceState.getInt("PROGRESS_BAR_STATE"));
        changeDescText(savedInstanceState.getString("DESC_BAR_TEXT"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mRequestId != null) {
            //ServiceHelper.removeCallback(mRequestId);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.schedule_professor_item, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            Log.d("URSMULOG", "SearchView yes");
            getSupportMenuInflater().inflate(R.menu.professor_action_bar, menu);
            mSearchView = new SearchView(getSupportActionBar().getThemedContext());
            mSearchView.setQueryHint("Поиск преподавателя");
            mSearchView.setOnQueryTextListener(this);
            mSearchView.setOnSuggestionListener(this);
            mSearchView.setEnabled(light);


            menu.add("Поиск")
                    .setIcon(!light ? R.drawable.ic_search_inverse : R.drawable.abs__ic_search)
                    .setActionView(mSearchView)
                    .setEnabled(light)
                    .setShowAsAction(com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_IF_ROOM
                            | com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                return true;
            case R.id.global_update:
                if (mBar == null) {
                    mBar = (ProgressBar) findViewById(R.id.schedule_prof_bar);
                }
                if (mBar.getVisibility() != View.VISIBLE) {
                    startUpdateDialog();
                }  else {
                    Toast.makeText(getApplicationContext(),"Дождитесь окончания операции", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.schedule_prof_item_group:
                //EducationItem etem = adapter.getItem(info.position);
                ListAdapter ada = mPages.get(mViewPager.getCurrentItem()).getAdapter();
                String groupName = ((EducationItem) ada.getItem(info.position)).getGroupName();
                if (!TextUtils.isEmpty(groupName)) {
                    Intent i = new Intent(getApplicationContext(), GroupScheduleActivity.class);
                    i.putExtra(ServiceHelper.GROUP, groupName);
                    i.putExtra("IS_HARD", false);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Выберите пару", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.schedule_prof_item_alarm:
                ListAdapter temp = mPages.get(mViewPager.getCurrentItem()).getAdapter();
                ScheduleAdapter adapter = (ScheduleAdapter) temp;
                adapter.setAlarm(info.position);
                Log.d("URSMULOG", "onContextItemSelected R.id.schedule_prof_item_alarm" + info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void startUpdateDialog() {
        changeIndicatorVisible(View.INVISIBLE);
        DialogFragment newFragment = new UpdateDialog(mHandlerDialog);
        newFragment.show(getSupportFragmentManager(), "dialog");
    }

    private void nextStep() {
        if (mProfessor != null) {
            setTitle(mProfessor);
            mPages = new ArrayList<ListView>(6);
            mPagerAdapter = new MyPagerAdapter(mPages);
            mViewPager = (ViewPager) findViewById(R.id.professor_view_pager);
            mViewPager.setAdapter(mPagerAdapter);
            mViewPager.setOnPageChangeListener(pageChangeListener);
            mRequestId = mHelper.getUrsmuDBObject(new ProfessorSchedule(mProfessor), mHandlerTwo);
            changeDescText(null);
        } else {
            changeDescText(getResources().getString(R.string.offline_search_help));
        }
    }

    public void previous(View v) {
        if (mViewPager!= null)
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
    }

    public void next(View v) {
        if (mViewPager != null)
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
    }

    public void current(View v) {
        if (mViewPager!=null) {
            mViewPager.setCurrentItem(0, true);
            changeFooter(mViewPager.getCurrentItem());
        }
    }

    private void changeFooter(int i) {
        if (footerText == null) {
            footerText = (TextView) findViewById(R.id.day_schedule_prof);
        }
        EducationItem n = ((ScheduleAdapter) mPagerAdapter.getItem(i).getAdapter()).getItem(0);
        footerText.setText(EducationItem.DayOfTheWeek[n.getDayOfTheWeek()]);
    }

    protected void changeIndicatorVisible(int visibility) {
        if (mBar == null) {
            mBar = (ProgressBar) findViewById(R.id.schedule_prof_bar);
        }
        mBar.setVisibility(visibility);

        if (visibility == View.INVISIBLE) {
            mBar = null;
        }
    }

    private void changeDescText(String txt) {
        if (mDesc == null) {
            mDesc = (TextView) findViewById(R.id.schedule_prof_desc);
        }
        if (txt != null) {
            mDesc.setVisibility(View.VISIBLE);
            mDesc.setText(txt);
        } else {
            mDesc.setVisibility(View.INVISIBLE);
        }
    }


}