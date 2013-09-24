package ru.ursmu.application.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.widget.SearchView;
import ru.ursmu.application.R;
import ru.ursmu.application.Abstraction.UniversalCallback;
import ru.ursmu.application.JsonObject.Faculty;
import ru.ursmu.application.Realization.FacultyFactory;
import ru.ursmu.application.Realization.FacultyList;
import ru.ursmu.application.Realization.ScheduleGroup;
import ru.ursmu.application.Realization.ScheduleGroupFactory;

public class FindFacultyActivity extends SherlockListActivity implements SearchView.OnQueryTextListener, SearchView.OnSuggestionListener {
    ServiceHelper mHelper;
    ProgressBar mBar;
    Faculty[] mFaculty;
    SearchView mSearchView;
    boolean mLigth = false;
    Long mRequestId;

    private AdapterView.OnItemClickListener facultyClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(getApplicationContext(), FindKursActivity.class);
            intent.putExtra(ServiceHelper.FACULTY, mFaculty[i].getOriginalName());
            startActivity(intent);

        }
    };

    private UniversalCallback mHandler = new UniversalCallback() {
        @Override
        public void sendError(String notify) {
            changeIndicatorVisible(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), notify, Toast.LENGTH_SHORT).show();
            if (mLigth) {
                View textHelp = findViewById(R.id.common_help);
                textHelp.setVisibility(View.VISIBLE);
                ((TextView) textHelp).setText(getResources().getString(R.string.offline_search_help));
            }
        }

        @Override
        public void sendComplete(Object[] data) {
            changeIndicatorVisible(View.INVISIBLE);
            postProcessing((String[]) data);
            //ServiceHelper.removeCallback(mRequestId);
        }

        @Override
        public void sendStart() {
            changeIndicatorVisible(View.VISIBLE);
        }
    };
    //private Cursor mCursor;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common);
        mHelper = ServiceHelper.getInstance(getApplicationContext());
        mRequestId = mHelper.getUrsmuObject(new FacultyList(), mHandler);

        ScheduleGroupFactory object = new ScheduleGroupFactory();
/*        if (!(mLigth = object.check(getApplicationContext()))) {
            //mSearchView.setEnabled(mLigth);
        }*/

        mLigth = object.check(getApplicationContext());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mSearchView = new SearchView(getSupportActionBar().getThemedContext());
        mSearchView.setQueryHint("Поиск группы");
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnSuggestionListener(this);
        mSearchView.setEnabled(mLigth);

        menu.add("Search")
                .setIcon(!mLigth ? R.drawable.ic_search_inverse : R.drawable.abs__ic_search)
                .setActionView(mSearchView)
                .setEnabled(mLigth)
                .setShowAsAction(com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_IF_ROOM | com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        return true;
    }

    protected void changeIndicatorVisible(int visibility) {
        if (mBar == null) {
            mBar = (ProgressBar) findViewById(R.id.commonProgressBar);
        }
        mBar.setVisibility(visibility);

        if (visibility == View.INVISIBLE) {
            mBar = null;
        }
    }

    @Override
    protected void postProcessing(String[] data) {
        mFaculty = new Faculty[data.length];
        for (int i = 0; i < data.length; i++) {
            Faculty one_f = FacultyFactory.create(data[i]);
            mFaculty[i] = one_f;
        }

        setListAdapter(new FacultyAdapter(getApplicationContext(), R.layout.faculty_adapter, mFaculty));
        getListView().setOnItemClickListener(facultyClickListener);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Toast.makeText(getApplicationContext(), "Выберите элемент из списка", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() < 6) {
            ScheduleGroup prof = new ScheduleGroup(newText, false); //Upper Case
            Cursor c = prof.getDataBasingBehavior(getApplicationContext()).get();

            SuggestionsAdapter adapter = new SuggestionsAdapter(getSupportActionBar().getThemedContext(), c, "GroupName", R.drawable.white_social_group);
            mSearchView.setSuggestionsAdapter(adapter);
            return true;
        } else {
            Toast.makeText(getApplicationContext(), "А по короче?", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        String groupName = ((SuggestionsAdapter) mSearchView.getSuggestionsAdapter()).getString(position);
        Log.d("URSMULOG onSuggestionClick", groupName);
        mSearchView.clearFocus();

        Intent intent = new Intent(this, GroupScheduleActivity.class);
        intent.putExtra(ServiceHelper.GROUP, groupName);
        intent.putExtra("IS_HARD", false);
        startActivity(intent);
        return true;
    }


}
