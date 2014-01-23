package ru.ursmu.application.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import ru.ursmu.application.Abstraction.IUrsmuObject;
import ru.ursmu.application.Abstraction.UniversalCallback;
import ru.ursmu.application.JsonObject.ListItem;
import ru.ursmu.application.Realization.UrsmuNews;
import ru.ursmu.beta.application.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class NewsActivity extends ActionBarActivity {
    ServiceHelper mHelper;
    ArrayList<ListItem> mSource;
    ProgressBar mBar;

    UniversalCallback mCallback = new UniversalCallback() {
        @Override
        public void sendError(String notify) {
            changeIndicatorVisible(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), notify, Toast.LENGTH_SHORT).show();
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


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);
        mHelper = ServiceHelper.getInstance(getApplicationContext());
        IUrsmuObject object = new UrsmuNews(1);
        long mReqId = mHelper.getUrsmuObject(object, mCallback);

        mSource = new ArrayList<ListItem>(19);
        ListView newsList = (ListView) findViewById(R.id.list_news);
        newsList.setOnItemClickListener(mItemItemClickListener);
        mAdapter = new NewsAdapter(getApplicationContext(), R.layout.news_adapter, mSource);
        newsList.setAdapter(mAdapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    protected void changeIndicatorVisible(int visibility) {
        if (mBar == null) {
            mBar = (ProgressBar) findViewById(R.id.progress_news);
        }
        mBar.setVisibility(visibility);
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
}