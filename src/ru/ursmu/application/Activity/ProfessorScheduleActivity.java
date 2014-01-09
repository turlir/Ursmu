package ru.ursmu.application.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import ru.ursmu.application.Abstraction.UniversalCallback;
import ru.ursmu.application.Realization.EducationWeek;
import ru.ursmu.application.Realization.ProfessorSchedule;
import ru.ursmu.application.Realization.ScheduleGroupFactory;
import ru.ursmu.beta.application.R;

import java.io.Serializable;
import java.util.Calendar;

public class ProfessorScheduleActivity extends SherlockFragmentActivity implements SearchView.OnQueryTextListener, SearchView.OnSuggestionListener {
    String mProfessor;
    ServiceHelper mHelper;
    private ProgressBar mBar;
    private TextView mDesc;
    private boolean light = false;
    private SearchView mSearchView;
    private Long mRequestId;

    private UniversalCallback mHandlerDialog = new UniversalCallback() {
        @Override
        public void sendError(String notify) {
            changeIndicatorVisible(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Обновление завершено с ошибкой", Toast.LENGTH_LONG).show();
            changeDescText(null);
        }

        @Override
        public void sendComplete(Serializable data) {
            Toast.makeText(getApplicationContext(), "Обновление завершено успешно", Toast.LENGTH_LONG).show();
            changeIndicatorVisible(View.INVISIBLE);
            changeDescText(null);
            light = true;
            invalidateOptionsMenu();
            nextStep();
            mRequestId = null;
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
        public void sendComplete(Serializable data) {
            changeIndicatorVisible(View.INVISIBLE);
            mRequestId = null;
            if (data != null) {
                MyPagerAdapter pager_adapter = new MyPagerAdapter(getSupportFragmentManager(), (EducationWeek) data, getApplicationContext(), true);

                ViewPager view_pager = (ViewPager) findViewById(R.id.professor_viewpager);
                view_pager.setVisibility(View.VISIBLE);
                view_pager.setCurrentItem(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2, true);
                view_pager.setAdapter(pager_adapter);

                PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.professor_pagerTabStrip);
                pagerTabStrip.setDrawFullUnderline(true);
                pagerTabStrip.setTabIndicatorColor(Color.parseColor("#33B5E5"));
                pagerTabStrip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void sendStart(long id) {
            changeIndicatorVisible(View.VISIBLE);
            changeDescText(null);
            mRequestId = id;
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
        findViewById(R.id.path_to_icon).setVisibility(View.INVISIBLE);
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
    protected void onPause() {
        super.onPause();
        ScheduleAdapter.clearIconPair();
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
                if (mRequestId == 0) {
                    startUpdateDialog();
                    return true;
                } else {
                    Toast.makeText(getApplicationContext(), "Дождитесь окончания операции", Toast.LENGTH_SHORT).show();
                    return false;
                }
            default:
                return super.onOptionsItemSelected(item);
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
            if (mHelper == null)
                mHelper = ServiceHelper.getInstance(getApplicationContext());

            mHelper.getUrsmuDBObject(new ProfessorSchedule(mProfessor), mHandlerTwo);
        } else {
            findViewById(R.id.path_to_icon).setVisibility(View.VISIBLE);
            changeDescText(getResources().getString(R.string.offline_search_help));
        }
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