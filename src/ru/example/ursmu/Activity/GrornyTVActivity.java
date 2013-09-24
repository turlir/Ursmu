package ru.example.ursmu.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import ru.example.ursmu.Abstraction.UniversalCallback;
import ru.example.ursmu.JsonObject.ListItem;
import ru.example.ursmu.Realization.GornyVestiFactory;
import ru.example.ursmu.R;

import java.util.ArrayList;
import java.util.Collections;

public class GrornyTVActivity extends SherlockActivity {
    ServiceHelper mHelper;
    ProgressBar mBar;
    private UniversalCallback mHandler = new UniversalCallback() {
        @Override
        public void sendError(String notify) {
            changeIndicatorVisible(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), notify, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void sendComplete(Object[] data) {
            changeIndicatorVisible(View.INVISIBLE);
            Collections.addAll(mSource, (ListItem[]) data);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void sendStart() {
            changeIndicatorVisible(View.VISIBLE);
        }
    };
    private NewsAdapter mAdapter;
    private ArrayList<ListItem> mSource;
    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
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
        setContentView(R.layout.vesti_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mHelper = ServiceHelper.getInstance(getApplicationContext());
        mHelper.getGroupObjects(new GornyVestiFactory(1), mHandler);

        mSource = new ArrayList<ListItem>(10);
        mAdapter = new NewsAdapter(getApplicationContext(), R.layout.news_adapter, mSource);
        ListView listVesit = (ListView) findViewById(R.id.list_vesti);
        listVesit.setOnItemClickListener(mItemClickListener);
        listVesit.setAdapter(mAdapter);
    }

    @Override
    protected void changeIndicatorVisible(int visibility) {
        if (mBar == null) {
            mBar = (ProgressBar) findViewById(R.id.vesti_bar);
        }
        mBar.setVisibility(visibility);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}