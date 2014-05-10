package ru.ursmu.application.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.*;
import android.widget.*;
import ru.ursmu.application.Abstraction.IUrsmuObject;
import ru.ursmu.application.Abstraction.UniversalCallback;
import ru.ursmu.application.JsonObject.ListItem;
import ru.ursmu.application.Realization.UrsmuNews;
import ru.ursmu.beta.application.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class NewsActivity extends Fragment {
    ActionBar mParentBar = null;
    ServiceHelper mHelper;
    ArrayList<ListItem> mSource;
    ProgressBar mBar;
    long mRequest;

    public NewsActivity(ActionBar bar) {
        mParentBar = bar;
    }


    UniversalCallback mCallback = new UniversalCallback() {
        @Override
        public void sendError(String notify) {
            changeIndicatorVisible(View.INVISIBLE, true);

            mRequest = 0;
        }

        @Override
        public void sendComplete(Serializable data) {
            changeIndicatorVisible(View.INVISIBLE, false);
            Collections.addAll(mSource, (ListItem[]) data);
            mAdapter.notifyDataSetChanged();
            mRequest = 0;
        }

        @Override
        public void sendStart(long id) {
            changeIndicatorVisible(View.VISIBLE, false);
            mRequest = id;
        }
    };

    private ArrayAdapter<ListItem> mAdapter;
    private AdapterView.OnItemClickListener mItemItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListItem item = mSource.get(position);
            String url = item.getUri();

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.news_activity, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        start();
        mSource = new ArrayList<ListItem>(19);
        ListView newsList = (ListView) getActivity().findViewById(R.id.list_news);
        newsList.setOnItemClickListener(mItemItemClickListener);
        mAdapter = new NewsAdapter(getActivity().getBaseContext(), R.layout.card_news_adapter, mSource);
        newsList.setAdapter(mAdapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.action_bar_news, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.news_update:
                if (mRequest == 0) {
                    start();
                } else {
                    Toast.makeText(getActivity().getBaseContext(), getResources().getString(R.string.wait),
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void start() {
        if (mHelper == null) {
            mHelper = ServiceHelper.getInstance(getActivity().getApplicationContext());
        }
        IUrsmuObject object = new UrsmuNews(1);
        mHelper.getUrsmuObject(object, mCallback);
    }

    protected void changeIndicatorVisible(int visibility, boolean isError) {
        if (mBar == null) {
            mBar = (ProgressBar) getActivity().findViewById(R.id.progress_news);
        }
        mBar.setVisibility(visibility);
        if (visibility == View.INVISIBLE) {
            if (isError) {
                getActivity().findViewById(R.id.error_card).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.list_news).setVisibility(View.INVISIBLE);
            } else {
                getActivity().findViewById(R.id.error_card).setVisibility(View.INVISIBLE);
                getActivity().findViewById(R.id.list_news).setVisibility(View.VISIBLE);
            }
        }
    }
}