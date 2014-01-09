package ru.ursmu.application.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Window;
import ru.ursmu.application.Abstraction.UniversalCallback;
import ru.ursmu.application.Realization.GroupList;
import ru.ursmu.beta.application.R;

import java.io.Serializable;

public class FindGroupActivity extends SherlockListActivity {
    private String mFaculty;
    private String mKurs;
    private ServiceHelper mHelper;
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


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHelper = ServiceHelper.getInstance(getApplicationContext());

        mFaculty = getIntent().getStringExtra(ServiceHelper.FACULTY);
        mKurs = getIntent().getStringExtra(ServiceHelper.KURS);

        mHelper.getUrsmuObject(new GroupList(mFaculty, mKurs), mHandler);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    }

    private AdapterView.OnItemClickListener groupsClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String selected_group = (String) parent.getItemAtPosition(position);

            mHelper.setThreeInfo(mFaculty, mKurs, selected_group);

            Intent i = new Intent(getApplicationContext(), GroupScheduleActivity.class);
            i.putExtra(ServiceHelper.FACULTY, mFaculty);
            i.putExtra(ServiceHelper.KURS, mKurs);
            i.putExtra(ServiceHelper.GROUP, selected_group);
            i.putExtra("IS_HARD", true);

            startActivity(i);
        }
    };

    @Override
    protected void postProcessing(String[] data) {
        setListAdapter(new ExtendedGroupAdapter(this, R.layout.group_adapter, data));
        getListView().setOnItemClickListener(groupsClickListener);
    }
}