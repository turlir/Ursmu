package ru.ursmu.application.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import com.actionbarsherlock.app.SherlockListActivity;
import ru.ursmu.application.R;
import ru.ursmu.application.Abstraction.UniversalCallback;
import ru.ursmu.application.Realization.GroupList;

public class FindGroupActivity extends SherlockListActivity {
    private ServiceHelper mHelper;
    private ProgressBar mBar;
    private String[] mGroups;
    private long mRequestId;
    private String fac;
    private String kur;
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


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common);

        mHelper = ServiceHelper.getInstance(getApplicationContext());

        fac = getIntent().getStringExtra(ServiceHelper.FACULTY);
        kur = getIntent().getStringExtra(ServiceHelper.KURS);

        mHelper.getUrsmuObject(new GroupList(fac, kur), mHandler);
    }

    private AdapterView.OnItemClickListener groupsClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mHelper.setThreeInfo(fac, kur, mGroups[position]);
            Intent i = new Intent(getApplicationContext(), GroupScheduleActivity.class);
            //i.putExtra("FAVORITE_GROUP", true);
            i.putExtra(ServiceHelper.FACULTY, fac);
            i.putExtra(ServiceHelper.KURS, kur);
            i.putExtra(ServiceHelper.GROUP, mGroups[position]);
            i.putExtra("IS_HARD", true);
            startActivity(i);
        }
    };

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
        mGroups = data;

        setListAdapter(new ExtendedGroupAdapter(this, R.layout.group_adapter, mGroups));
        getListView().setOnItemClickListener(groupsClickListener);
    }

    /*@Override
    protected void showNotification() {
        Toast.makeText(getApplicationContext(), getResources().getText(R.string.error_notif), Toast.LENGTH_LONG).show();
    }*/
}