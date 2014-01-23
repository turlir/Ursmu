package ru.ursmu.application.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import ru.ursmu.application.Abstraction.UniversalCallback;
import ru.ursmu.application.JsonObject.Faculty;
import ru.ursmu.application.Realization.FacultyFactory;
import ru.ursmu.application.Realization.FacultyList;
import ru.ursmu.application.Realization.ScheduleGroup;
import ru.ursmu.application.Realization.ScheduleGroupFactory;
import ru.ursmu.beta.application.R;

import java.io.Serializable;

public class FindFacultyActivity extends ActionBarActivity implements SearchView.OnQueryTextListener, SearchView.OnSuggestionListener {
    ServiceHelper mHelper;
    SearchView mSearchView;
    boolean mLigth = false;

    private AdapterView.OnItemClickListener facultyClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(getApplicationContext(), FindKursActivity.class);
            intent.putExtra(ServiceHelper.FACULTY, ((Faculty) adapterView.getItemAtPosition(i)).getOriginalName());
            startActivity(intent);
        }
    };

    private UniversalCallback mHandler = new UniversalCallback() {
        @Override
        public void sendError(String notify) {
            setProgressBarIndeterminateVisibility(false);
            showNotification(notify);
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
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.common);
        mHelper = ServiceHelper.getInstance(getApplicationContext());
        mHelper.getUrsmuObject(new FacultyList(), mHandler);

        ScheduleGroupFactory object = new ScheduleGroupFactory();

        mLigth = object.check(getApplicationContext());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            getMenuInflater().inflate(R.menu.action_bar_faculty, menu);

            MenuItem searchItem = menu.findItem(R.id.search_professor_faculty_list);
            searchItem.setEnabled(mLigth);
            searchItem.setIcon(!mLigth ? R.drawable.ic_search_inverse : R.drawable.abc_ic_search);

            mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            mSearchView.setQueryHint("Поиск преподавателя");
            mSearchView.setOnQueryTextListener(this);
            mSearchView.setOnSuggestionListener(this);
        }
        return super.onCreateOptionsMenu(menu);
    }

    protected void postProcessing(String[] data) {
        Faculty[] mFaculty = new Faculty[data.length];
        for (int i = 0; i < data.length; i++) {
            Faculty one_f = FacultyFactory.create(data[i]);
            mFaculty[i] = one_f;
        }

        getListView().setAdapter(new FacultyAdapter(getApplicationContext(), R.layout.faculty_adapter, mFaculty));
        getListView().setOnItemClickListener(facultyClickListener);
    }

    private ListView getListView() {
        return (ListView) findViewById(R.id.listItem);
    }

    private void showNotification(String notify) {
        Toast.makeText(getApplicationContext(), notify, Toast.LENGTH_SHORT).show();
    }


}
