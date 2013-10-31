package ru.ursmu.application.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import com.actionbarsherlock.app.SherlockListActivity;
import ru.ursmu.beta.application.R;
import ru.ursmu.application.Abstraction.UniversalCallback;
import ru.ursmu.application.Realization.KursList;

public class FindKursActivity extends SherlockListActivity {
    private ProgressBar mBar;
    private String[] mKurs;
    private long mRequestId;
    private String faculty;
    private UniversalCallback mHandler = new UniversalCallback() {
        @Override
        public void sendError(String notify) {
            changeIndicatorVisible(View.INVISIBLE);
        }

        @Override
        public void sendComplete(Object[] data) {
            changeIndicatorVisible(View.INVISIBLE);
            postProcessing((String[]) data);
            //ServiceHelper.removeCallback(mRequestId);
        }

        @Override
        public void sendStart(long id) {
            changeIndicatorVisible(View.INVISIBLE);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common);

        ServiceHelper mHelper = ServiceHelper.getInstance(getApplicationContext());
        //String faculty = mHelper.getPreference(ServiceHelper.FACULTY);
        faculty = getIntent().getStringExtra(ServiceHelper.FACULTY);

        mRequestId = mHelper.getUrsmuObject(new KursList(faculty), mHandler);
    }

    @Override
    protected void changeIndicatorVisible(int visibility) {
        if (mBar == null) {
            mBar = (ProgressBar) findViewById(R.id.commonProgressBar);
        }
        mBar.setVisibility(visibility);
        if (visibility == View.INVISIBLE) {
            mBar = null;
        }
    }

    @Override
    protected void postProcessing(String[] data) {
        mKurs = data;

        setListAdapter(new RomanAdapter(this, R.layout.kurs_adapter, mKurs));
        getListView().setOnItemClickListener(kursClickListener);
    }

    AdapterView.OnItemClickListener kursClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            Intent intent = new Intent(getApplicationContext(), FindGroupActivity.class);
            intent.putExtra(ServiceHelper.FACULTY, faculty);
            intent.putExtra(ServiceHelper.KURS, mKurs[position]);
            startActivity(intent);
        }
    };

   /* @Override
    protected void showNotification() {
        Toast.makeText(getApplicationContext(), getResources().getText(R.string.error_notif), Toast.LENGTH_LONG).show();
    }*/


}