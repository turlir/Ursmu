package ru.ursmu.application.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public NewsActivity(ActionBar bar) {
        mParentBar = bar;
    }


    UniversalCallback mCallback = new UniversalCallback() {
        @Override
        public void sendError(String notify) {
            changeIndicatorVisible(View.INVISIBLE);
            Toast.makeText(getActivity().getBaseContext(), notify, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void sendComplete(Serializable data) {
            changeIndicatorVisible(View.INVISIBLE);
            Collections.addAll(mSource, (ListItem[]) data);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void sendStart(long id) {
            changeIndicatorVisible(View.VISIBLE);
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
        mHelper = ServiceHelper.getInstance(getActivity().getApplicationContext());
        IUrsmuObject object = new UrsmuNews(1);
        mHelper.getUrsmuObject(object, mCallback);

        mSource = new ArrayList<ListItem>(19);
        ListView newsList = (ListView) getActivity().findViewById(R.id.list_news);
        newsList.setOnItemClickListener(mItemItemClickListener);
        mAdapter = new NewsAdapter(getActivity().getBaseContext(), R.layout.card_news_adapter, mSource);
        newsList.setAdapter(mAdapter);
    }

    protected void changeIndicatorVisible(int visibility) {
        if (mBar == null) {
            mBar = (ProgressBar) getActivity().findViewById(R.id.progress_news);
        }
        mBar.setVisibility(visibility);
    }
}