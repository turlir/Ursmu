package ru.ursmu.application.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.*;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import ru.ursmu.application.Abstraction.UniversalCallback;
import ru.ursmu.application.Realization.EducationWeek;
import ru.ursmu.application.Realization.ProfessorSchedule;
import ru.ursmu.application.Realization.ScheduleGroupFactory;
import ru.ursmu.beta.application.R;

import java.io.Serializable;
import java.util.Calendar;

public class ProfessorScheduleActivity extends Fragment implements SearchView.OnQueryTextListener, SearchView.OnSuggestionListener {
    String mProfessor;
    ServiceHelper mHelper;
    private ProgressBar mBar;
    private TextView mDesc;
    private boolean mLight = false;
    private SearchView mSearchView;
    private Long mRequestId;
    private ActionBar mParentBar;
    private Intent mStartParam;

    public ProfessorScheduleActivity(ActionBar b, Intent p) {
        mParentBar = b;
        mStartParam = p;
    }

    private UniversalCallback mHandlerTwo = new UniversalCallback() {
        @Override
        public void sendError(String notify) {
            Toast.makeText(getActivity().getBaseContext(), notify, Toast.LENGTH_SHORT).show();
            getActivity().findViewById(R.id.viewpager).setVisibility(View.VISIBLE);
        }

        @Override
        public void sendComplete(Serializable data) {
            changeIndicatorVisible(View.INVISIBLE);
            mRequestId = null;
            if (data != null) {
                MyPagerAdapter pager_adapter = new MyPagerAdapter(getChildFragmentManager(),
                        (EducationWeek) data,
                        getActivity().getApplicationContext(),
                        true);

                ViewPager view_pager = (ViewPager) getActivity().findViewById(R.id.professor_viewpager);
                view_pager.setVisibility(View.VISIBLE);
                view_pager.setAdapter(pager_adapter);
                view_pager.setCurrentItem(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2, true);

                PagerTabStrip pagerTabStrip = (PagerTabStrip) getActivity().findViewById(R.id.professor_pagerTabStrip);
                pagerTabStrip.setDrawFullUnderline(true);
                pagerTabStrip.setTabIndicatorColor(Color.parseColor("#0099CC"));
                pagerTabStrip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void sendStart(long id) {
            changeIndicatorVisible(View.VISIBLE);
            changeDescText(null);
            mRequestId = id;
            getActivity().findViewById(R.id.professor_viewpager).setVisibility(View.INVISIBLE);
        }


    };


    //<editor-fold desc="SearchSuggestion">
    @Override
    public boolean onQueryTextSubmit(String query) {
        Toast.makeText(getActivity().getBaseContext(), "Выбрите элемент из списка", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        getActivity().findViewById(R.id.path_to_icon).setVisibility(View.INVISIBLE);
        if (newText.length() > 4) {
            ProfessorSchedule prof = new ProfessorSchedule(newText.toLowerCase());
            Cursor suggestion = prof.getDataBasingBehavior(getActivity().getApplicationContext()).get();

            SuggestionsAdapter adapter = new SuggestionsAdapter(mParentBar.getThemedContext(),
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
        mProfessor = ((SuggestionsAdapter) mSearchView.getSuggestionsAdapter()).getString(position);
        Log.d("URSMULOG", mProfessor);
        nextStep();
        mSearchView.setQuery(mProfessor, false);
        mSearchView.clearFocus();
        return true;
    }
    //</editor-fold>


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.professor_schedule, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ScheduleGroupFactory object = new ScheduleGroupFactory();

        if (mStartParam != null) {
            mProfessor = mStartParam.getStringExtra("PROFESSOR");
        }

        if (!(mLight = object.check(getActivity().getApplicationContext()))) {
            startQuestDialog();
        } else {
            nextStep();
        }

        mParentBar.setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
       // setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        ScheduleAdapter.clearIconPair();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mBar != null) {
            outState.putInt("PROGRESS_BAR_STATE", mBar.getVisibility());
        }
        if (mDesc != null) {
            outState.putString("DESC_BAR_TEXT", (String) mDesc.getText());
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            changeIndicatorVisible(savedInstanceState.getInt("PROGRESS_BAR_STATE"));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.action_bar_professor_schedule, menu);

        MenuItem searchItem = menu.findItem(R.id.search_professor_activity);
        searchItem.setEnabled(mLight);
        searchItem.setIcon(!mLight ? R.drawable.ic_search_inverse : R.drawable.abc_ic_search);

        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setQueryHint("Поиск преподавателя");
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnSuggestionListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.global_update:
                if (mRequestId == null) {
                    startQuestDialog();
                    return true;
                } else {
                    Toast.makeText(getActivity().getBaseContext(), "Дождитесь окончания операции", Toast.LENGTH_SHORT).show();
                    return false;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startQuestDialog() {
        changeIndicatorVisible(View.INVISIBLE);

        DialogInterface.OnClickListener positiveHandler = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mRequestId = (long) 1;
                startUpdDialog();
            }
        };

        DialogFragment quest_dialog = new QuestionDialog(positiveHandler,
                getResources().getString(R.string.quest_dialog_title), getResources().getString(R.string.quest_dialog_desc));

        quest_dialog.show(getActivity().getSupportFragmentManager(), "quest_dialog");
    }

    private void startUpdDialog() {
        DialogFragment upd_dialog = new UpdateDialog(new ResultReceiver(null) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                mLight = true;
                getActivity().supportInvalidateOptionsMenu();
                mRequestId = null;
                nextStep();
            }
        });
        upd_dialog.show(getActivity().getSupportFragmentManager(), "upd_dialog");
    }

    private void nextStep() {
        if (mProfessor != null) {
            setTitle(mProfessor);
            if (mHelper == null)
                mHelper = ServiceHelper.getInstance(getActivity().getApplicationContext());

            mHelper.getUrsmuDBObject(new ProfessorSchedule(mProfessor), mHandlerTwo);
        } else {
            getActivity().findViewById(R.id.path_to_icon).setVisibility(View.VISIBLE);
            changeDescText(getResources().getString(R.string.offline_search_help));
        }
    }

    private void setTitle(String mProfessor) {
        mParentBar.setTitle(mProfessor);
    }

    protected void changeIndicatorVisible(int visibility) {
        if (mBar == null) {
            mBar = (ProgressBar) getActivity().findViewById(R.id.schedule_prof_bar);
        }
        mBar.setVisibility(visibility);
        if (visibility == View.INVISIBLE) {
            mBar = null;
        }
    }

    private void changeDescText(String txt) {
        if (mDesc == null) {
            mDesc = (TextView) getActivity().findViewById(R.id.schedule_prof_desc);
            //mDesc.setTypeface(Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf"));
        }
        if (txt != null) {
            mDesc.setVisibility(View.VISIBLE);
            mDesc.setText(txt);
        } else {
            mDesc.setVisibility(View.INVISIBLE);
        }
    }


}