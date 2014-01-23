package ru.ursmu.application.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import ru.ursmu.application.Abstraction.UniversalCallback;
import ru.ursmu.application.Realization.KursList;
import ru.ursmu.beta.application.R;

import java.io.Serializable;

public class FindKursActivity extends ActionBarActivity {
    private String mFaculty;
    private UniversalCallback mHandler = new UniversalCallback() {
        @Override
        public void sendError(String notify) {
            showNotification(notify);
            setProgressBarIndeterminateVisibility(false);
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.common);
        ServiceHelper helper = ServiceHelper.getInstance(getApplicationContext());

        mFaculty = getIntent().getStringExtra(ServiceHelper.FACULTY);

        helper.getUrsmuObject(new KursList(mFaculty), mHandler);
    }


    protected void postProcessing(String[] data) {
        String[] l = data;

        getListView().setAdapter(new RomanAdapter(this, R.layout.kurs_adapter, l));
        getListView().setOnItemClickListener(mKursClickListener);
    }

    private ListView getListView() {
        return (ListView) findViewById(R.id.listItem);
    }

    AdapterView.OnItemClickListener mKursClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getApplicationContext(), FindGroupActivity.class);
            intent.putExtra(ServiceHelper.FACULTY, mFaculty);
            intent.putExtra(ServiceHelper.KURS, (String) parent.getItemAtPosition(position));
            startActivity(intent);
        }
    };


    private void showNotification(String notify) {
        Toast.makeText(getApplicationContext(), notify, Toast.LENGTH_SHORT).show();
    }
}