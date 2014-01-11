package ru.ursmu.application.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.Window;
import com.actionbarsherlock.widget.SearchView;
import ru.ursmu.application.Abstraction.Handler;
import ru.ursmu.application.Abstraction.UniversalCallback;
import ru.ursmu.application.JsonObject.Faculty;
import ru.ursmu.application.Realization.FacultyFactory;
import ru.ursmu.application.Realization.FacultyList;
import ru.ursmu.application.Realization.ScheduleGroup;
import ru.ursmu.application.Realization.ScheduleGroupFactory;
import ru.ursmu.beta.application.R;

import java.io.Serializable;

public class FindFacultyActivity extends SherlockListActivity implements SearchView.OnQueryTextListener, SearchView.OnSuggestionListener {
    ServiceHelper mHelper;
    SearchView mSearchView;
    boolean mLigth = false;

    private AdapterView.OnItemClickListener facultyClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(getApplicationContext(), FindKursActivity.class);
            intent.putExtra(ServiceHelper.FACULTY, ((Faculty)adapterView.getItemAtPosition(i)).getOriginalName());
            startActivity(intent);
        }
    };

    private UniversalCallback mHandler = new UniversalCallback() {
        @Override
        public void sendError(String notify) {
            setProgressBarIndeterminateVisibility(false);
            showNotification(notify);
            if (mLigth) {
                /*View textHelp = findViewById(R.id.common_help);
                textHelp.setVisibility(View.VISIBLE);
                ((TextView) textHelp).setText(getResources().getString(R.string.offline_search_help));*/
            }
        }

        @Override
        public void sendComplete(Serializable data) {
            setProgressBarIndeterminateVisibility(false);
            postProcessing((String[]) data);
        }

        @Override
        public void sendStart(long id) {
            setProgressBarIndeterminateVisibility(true);
        }
    };


    //<editor-fold desc="SearchSuggestion">
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
        //String groupName = ((SuggestionsAdapter) mSearchView.getSuggestionsAdapter()).getString(position);
        Cursor all_item = mSearchView.getSuggestionsAdapter().getCursor();
        if (all_item.moveToPosition(position)) {
            String fac = all_item.getString(all_item.getColumnIndexOrThrow("FACULTY"));
            String kurs = all_item.getString(all_item.getColumnIndexOrThrow("KURS"));
            String gro = all_item.getString(all_item.getColumnIndexOrThrow("GroupName"));

            Intent i = new Intent(this, GroupScheduleActivity.class);
            i.putExtra("IS_HARD", false);
            i.putExtra(ServiceHelper.FACULTY, fac);
            i.putExtra(ServiceHelper.KURS, kurs);
            i.putExtra(ServiceHelper.GROUP, gro);
            startActivity(i);

            Log.d("URSMULOG onSuggestionClick", String.valueOf(position));
            mSearchView.clearFocus();

            return true;
        }
        return false;
    }
    //</editor-fold>


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = ServiceHelper.getInstance(getApplicationContext());
        mHelper.getUrsmuObject(new FacultyList(), mHandler);

        ScheduleGroupFactory object = new ScheduleGroupFactory();

        mLigth = object.check(getApplicationContext());

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            Log.d("URSMULOG", "SearchView yes");
            mSearchView = new SearchView(getSupportActionBar().getThemedContext());
            mSearchView.setQueryHint("Поиск группы");
            mSearchView.setOnQueryTextListener(this);
            mSearchView.setOnSuggestionListener(this);
            mSearchView.setEnabled(mLigth);

            menu.add("Поиск")
                    .setIcon(!mLigth ? R.drawable.ic_search_inverse : R.drawable.abs__ic_search)
                    .setActionView(mSearchView)
                    .setEnabled(mLigth)
                    .setShowAsAction(com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_IF_ROOM | com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        }

        return true;
    }

    protected void postProcessing(String[] data) {
        Faculty[] mFaculty = new Faculty[data.length];
        for (int i = 0; i < data.length; i++) {
            Faculty one_f = FacultyFactory.create(data[i]);
            mFaculty[i] = one_f;
        }

        setListAdapter(new FacultyAdapter(getApplicationContext(), R.layout.faculty_adapter, mFaculty));
        getListView().setOnItemClickListener(facultyClickListener);
    }


}
