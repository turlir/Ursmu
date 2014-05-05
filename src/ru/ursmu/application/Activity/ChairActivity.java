package ru.ursmu.application.Activity;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import ru.ursmu.application.Abstraction.UniversalCallback;
import ru.ursmu.application.JsonObject.ChairItem;
import ru.ursmu.application.Realization.RandomChairList;
import ru.ursmu.application.Realization.SpecificChairList;
import ru.ursmu.beta.application.R;

import java.io.Serializable;


public class ChairActivity extends Fragment implements SearchView.OnQueryTextListener, SearchView.OnSuggestionListener {
    ServiceHelper mHelper;
    boolean mLight = true;
    SearchView mSearchView;
    ActionBar mParentBar;


    public ChairActivity(ActionBar b) {
        mParentBar = b;
    }

    private UniversalCallback mCallback = new UniversalCallback() {
        @Override
        public void sendError(String notify) {
            getActivity().findViewById(R.id.chair_listView).setVisibility(View.VISIBLE);
            getActivity().setProgressBarIndeterminateVisibility(false);
            showNotification(notify);
        }

        @Override
        public void sendComplete(Serializable data) {
            getActivity().findViewById(R.id.chair_listView).setVisibility(View.VISIBLE);
            getActivity().setProgressBarIndeterminateVisibility(false);
            postProcessing((ChairItem[]) data);
        }

        @Override
        public void sendStart(long id) {
            getActivity().setProgressBarIndeterminateVisibility(true);
            getActivity().findViewById(R.id.chair_listView).setVisibility(View.INVISIBLE);
        }
    };

    //<editor-fold desc="SearchRegion">
    @Override
    public boolean onQueryTextSubmit(String s) {
        Toast.makeText(getActivity().getBaseContext(), "Выберите элемент из списка", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() > 6) {
            SpecificChairList prof = new SpecificChairList(newText); //Upper Case
            Cursor c = prof.getDataBasingBehavior(getActivity().getApplicationContext()).get();
            SuggestionsAdapter adapter = new SuggestionsAdapter(mParentBar.getThemedContext(), c, "name",
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

            ChairItem selected = new ChairItem(all_item);
            postProcessing(new ChairItem[]{
                    selected
            });

            Log.d("URSMULOG onSuggestionClick", String.valueOf(position));
            mSearchView.setQuery(selected.getName(), false);
            mSearchView.clearFocus();
            return true;
        }
        return false;
    }
    //</editor-fold>


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chair_activity, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHelper = ServiceHelper.getInstance(getActivity().getApplicationContext());
        mHelper.getUrsmuDBObject(new RandomChairList(5), mCallback);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.action_bar_faculty, menu);

        MenuItem searchItem = menu.findItem(R.id.search_professor_faculty_list);
        if (searchItem == null) return;
        searchItem.setEnabled(mLight);
        searchItem.setIcon(!mLight ? R.drawable.ic_search_inverse : R.drawable.abc_ic_search);

        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setQueryHint("Поиск кафедры");
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnSuggestionListener(this);
    }

    private void showNotification(String notify) {
        Toast.makeText(getActivity().getBaseContext(), notify, Toast.LENGTH_SHORT).show();
    }

    private void postProcessing(ChairItem[] data) {
        ListView list = (ListView) getActivity().findViewById(R.id.chair_listView);
        ArrayAdapter<ChairItem> adapter = new ChairAdapter(getActivity().getBaseContext(), R.layout.card_chair_adapter, data);
        list.setAdapter(adapter);
    }

}