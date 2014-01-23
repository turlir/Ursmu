package ru.ursmu.application.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import ru.ursmu.application.JsonObject.EducationItem;
import ru.ursmu.beta.application.R;

import java.lang.ref.WeakReference;

public class GroupScheduleFragment extends Fragment {
    public static final String MAIN_ARG = "MAIN_ARG";
    private WeakReference<ScheduleAdapter> mAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.schedule_fragment, container, false);

        ListView list = (ListView) rootView.findViewById(R.id.schedule_fragment_list);
        registerForContextMenu(list);

        EducationItem[] data_list = (EducationItem[]) getArguments().getSerializable(MAIN_ARG);

        mAdapter = new WeakReference<ScheduleAdapter>(
                new ScheduleAdapter
                        (getActivity().getApplicationContext(), R.layout.schedule_adapter, data_list, false)
        );
        list.setAdapter(mAdapter.get());

        return rootView;
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        if (mAdapter.get() != null) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

            switch (item.getItemId()) {
                case R.id.schedule_item_professor:
                    String normalProfessor = mAdapter.get().getItem(info.position).getNormalProfessor();
                    if (!TextUtils.isEmpty(normalProfessor)) {
                        Intent i = new Intent(getActivity().getApplicationContext(), ProfessorScheduleActivity.class);
                        i.putExtra("PROFESSOR", normalProfessor);
                        startActivity(i);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Выберите пару", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                case R.id.schedule_item_alarm:
                    mAdapter.get().setAlarm(info.position);
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
        }
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.schedule_item_group, menu);
    }
}
