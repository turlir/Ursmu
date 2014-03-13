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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import ru.ursmu.application.Abstraction.UniversalCallback;
import ru.ursmu.application.JsonObject.ChairItem;
import ru.ursmu.application.Realization.SpecificChairList;
import ru.ursmu.application.Realization.RandomChairList;
import ru.ursmu.beta.application.R;

import java.io.Serializable;


public class ChairActivity extends ActionBarActivity implements SearchView.OnQueryTextListener, SearchView.OnSuggestionListener {
    ServiceHelper mHelper;
    boolean mLight = true;
    SearchView mSearchView;
    private UniversalCallback mCallback = new UniversalCallback() {
        @Override
        public void sendError(String notify) {
            findViewById(R.id.chair_listView).setVisibility(View.VISIBLE);
            setProgressBarIndeterminateVisibility(false);
            showNotification(notify);
        }

        @Override
        public void sendComplete(Serializable data) {
            findViewById(R.id.chair_listView).setVisibility(View.VISIBLE);
            setProgressBarIndeterminateVisibility(false);
            postProcessing((ChairItem[]) data);
        }

        @Override
        public void sendStart(long id) {
            setProgressBarIndeterminateVisibility(true);
            findViewById(R.id.chair_listView).setVisibility(View.INVISIBLE);
        }
    };

    //<editor-fold desc="SearchRegion">
    @Override
    public boolean onQueryTextSubmit(String s) {
        Toast.makeText(getApplicationContext(), "Выберите элемент из списка", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() > 6) {
            SpecificChairList prof = new SpecificChairList(newText); //Upper Case
            Cursor c = prof.getDataBasingBehavior(getApplicationContext()).get();

            SuggestionsAdapter adapter = new SuggestionsAdapter(getSupportActionBar().getThemedContext(), c, "name",
                    R.drawable.white_chair);
            mSearchView.setSuggestionsAdapter(adapter);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onSuggestionSelect(int i) {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        Cursor all_item = mSearchView.getSuggestionsAdapter().getCursor();
        if (all_item.moveToPosition(position)) {

            postProcessing(new ChairItem[]{
                    new ChairItem(all_item)
            });

            Log.d("URSMULOG onSuggestionClick", String.valueOf(position));
            mSearchView.clearFocus();

            return true;
        }
        return false;
    }
    //</editor-fold>


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.chair_activity);

        mHelper = ServiceHelper.getInstance(getApplicationContext());

        mHelper.getUrsmuDBObject(new RandomChairList(5), mCallback);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            getMenuInflater().inflate(R.menu.action_bar_faculty, menu);

            MenuItem searchItem = menu.findItem(R.id.search_professor_faculty_list);
            searchItem.setEnabled(mLight);
            searchItem.setIcon(!mLight ? R.drawable.ic_search_inverse : R.drawable.abc_ic_search);

            mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            mSearchView.setQueryHint("Поиск кафедры");
            mSearchView.setOnQueryTextListener(this);
            mSearchView.setOnSuggestionListener(this);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void showNotification(String notify) {
        Toast.makeText(getApplicationContext(), notify, Toast.LENGTH_SHORT).show();
    }

    private void postProcessing(ChairItem[] data) {
        ListView list = (ListView) findViewById(R.id.chair_listView);
        ArrayAdapter<ChairItem> adapter = new ChairAdapter(this, R.layout.card_chair_adapter, data);
        list.setAdapter(adapter);
    }

}