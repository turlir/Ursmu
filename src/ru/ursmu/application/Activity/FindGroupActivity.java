package ru.ursmu.application.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import ru.ursmu.application.Abstraction.UniversalCallback;
import ru.ursmu.application.Realization.GroupList;
import ru.ursmu.application.R;

import java.io.Serializable;

public class FindGroupActivity extends ActionBarActivity {
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
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.common);

        mHelper = ServiceHelper.getInstance(getApplicationContext());

        mFaculty = getIntent().getStringExtra(ServiceHelper.FACULTY);
        mKurs = getIntent().getStringExtra(ServiceHelper.KURS);

        mHelper.getUrsmuObject(new GroupList(mFaculty, mKurs), mHandler);
    }

    private AdapterView.OnItemClickListener groupsClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String selected_group = (String) parent.getItemAtPosition(position);

            String current_group = mHelper.getPreference(ServiceHelper.GROUP);
            boolean re_register = false;
            if (!current_group.equals(selected_group) && !current_group.equals("")) {
                //Log.d("URSMULOG", "FindGroupActivity re Register push flag");
                re_register = true;
            }

            mHelper.setThreeInfo(mFaculty, mKurs, selected_group);

            Intent i = new Intent(getApplicationContext(), SlideActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra(ServiceHelper.FACULTY, mFaculty);
            i.putExtra(ServiceHelper.KURS, mKurs);
            i.putExtra(ServiceHelper.GROUP, selected_group);
            i.putExtra(ServiceHelper.IS_HARD, true);
            i.putExtra("RE_REGISTER", re_register);

            startActivity(i);
        }
    };


    protected void postProcessing(String[] data) {
        getListView().setAdapter(new ExtendedGroupAdapter(this, R.layout.group_adapter, data));
        getListView().setOnItemClickListener(groupsClickListener);
    }

    private void showNotification(String notify) {
        Toast.makeText(getApplicationContext(), notify, Toast.LENGTH_SHORT).show();
    }

    private ListView getListView() {
        return (ListView) findViewById(R.id.listItem);
    }
}