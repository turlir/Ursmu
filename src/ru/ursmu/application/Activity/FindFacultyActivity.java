package ru.ursmu.application.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import ru.ursmu.application.Abstraction.UniversalCallback;
import ru.ursmu.application.JsonObject.Faculty;
import ru.ursmu.application.Realization.FacultyFactory;
import ru.ursmu.application.Realization.FacultyList;
import ru.ursmu.application.Realization.ScheduleGroupFactory;
import ru.ursmu.application.R;

import java.io.Serializable;

public class FindFacultyActivity extends ActionBarActivity {
    ServiceHelper mHelper;
    boolean mLight = false;

    private AdapterView.OnItemClickListener facultyClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(getApplicationContext(), FindKursActivity.class);
            intent.putExtra(ServiceHelper.FACULTY, ((Faculty) adapterView.getItemAtPosition(i)).getOriginalName());
            startActivity(intent);
        }
    };

    private UniversalCallback mHandler = new UniversalCallback() {
        @Override
        public void sendError(String notify) {
            setProgressBarIndeterminateVisibility(false);
            showNotification(notify);
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
        mHelper = ServiceHelper.getInstance(getApplicationContext());
        mHelper.getUrsmuObject(new FacultyList(), mHandler);

        ScheduleGroupFactory object = new ScheduleGroupFactory();

        mLight = object.check(getApplicationContext());

    }

    protected void postProcessing(String[] data) {
        Faculty[] mFaculty = new Faculty[data.length];
        for (int i = 0; i < data.length; i++) {
            Faculty one_f = FacultyFactory.create(data[i]);
            mFaculty[i] = one_f;
        }

        getListView().setAdapter(new FacultyAdapter(getApplicationContext(), R.layout.faculty_adapter, mFaculty));
        getListView().setOnItemClickListener(facultyClickListener);
    }

    private ListView getListView() {
        return (ListView) findViewById(R.id.listItem);
    }

    private void showNotification(String notify) {
        Toast.makeText(getApplicationContext(), notify, Toast.LENGTH_SHORT).show();
    }


}
