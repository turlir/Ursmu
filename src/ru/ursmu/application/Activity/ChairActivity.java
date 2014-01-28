package ru.ursmu.application.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import ru.ursmu.application.Abstraction.UniversalCallback;
import ru.ursmu.application.Realization.RandomChairList;
import ru.ursmu.beta.application.R;

import java.io.Serializable;


public class ChairActivity extends ActionBarActivity {
    ServiceHelper mHelper;
    private UniversalCallback mCallback = new UniversalCallback() {
        @Override
        public void sendError(String notify) {

        }

        @Override
        public void sendComplete(Serializable data) {

        }

        @Override
        public void sendStart(long id) {

        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chair_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
}