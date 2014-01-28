package ru.ursmu.application.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import ru.ursmu.application.Abstraction.UniversalCallback;
import ru.ursmu.application.JsonObject.ChairItem;
import ru.ursmu.application.Realization.RandomChairList;
import ru.ursmu.beta.application.R;

import java.io.Serializable;


public class ChairActivity extends ActionBarActivity {
    ServiceHelper mHelper;
    private UniversalCallback mCallback = new UniversalCallback() {
        @Override
        public void sendError(String notify) {
            setProgressBarIndeterminateVisibility(false);
            showNotification(notify);
        }

        @Override
        public void sendComplete(Serializable data) {
            setProgressBarIndeterminateVisibility(false);
            postProcessing((ChairItem[]) data);
        }

        @Override
        public void sendStart(long id) {
            setProgressBarIndeterminateVisibility(true);
        }
    };


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

    private void showNotification(String notify) {
        Toast.makeText(getApplicationContext(), notify, Toast.LENGTH_SHORT).show();
    }

    private void postProcessing(ChairItem[] data) {
        ListView list = (ListView) findViewById(R.id.chair_listView);
        ArrayAdapter<ChairItem> adapter = new ChairAdapter(this, R.layout.chair_adapter, data);
        list.setAdapter(adapter);
    }
}