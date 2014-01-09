package ru.ursmu.application.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Window;
import ru.ursmu.application.Abstraction.UniversalCallback;
import ru.ursmu.application.Realization.KursList;
import ru.ursmu.beta.application.R;

import java.io.Serializable;

public class FindKursActivity extends SherlockListActivity {
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

        ServiceHelper helper = ServiceHelper.getInstance(getApplicationContext());

        mFaculty = getIntent().getStringExtra(ServiceHelper.FACULTY);

        helper.getUrsmuObject(new KursList(mFaculty), mHandler);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    }

    @Override
    protected void postProcessing(String[] data) {
        String[] l = data;

        setListAdapter(new RomanAdapter(this, R.layout.kurs_adapter, l));
        getListView().setOnItemClickListener(mKursClickListener);
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


}