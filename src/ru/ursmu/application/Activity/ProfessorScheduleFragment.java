package ru.ursmu.application.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import ru.ursmu.application.JsonObject.EducationItem;
import ru.ursmu.beta.application.R;


public class ProfessorScheduleFragment extends Fragment {
    public static final String MAIN_ARG = "MAIN_ARG";
    private ScheduleAdapter mAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.schedule_fragment, container, false);

        ListView list = (ListView) rootView.findViewById(R.id.schedule_fragment_list);
        registerForContextMenu(list);

        EducationItem[] data_list = (EducationItem[]) getArguments().getSerializable(MAIN_ARG);
        mAdapter = new ScheduleAdapter(getActivity().getApplicationContext(), R.layout.schedule_adapter, data_list, true);

        list.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.schedule_item_professor, menu);
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.schedule_prof_item_group:
                EducationItem selected = mAdapter.getItem(info.position);
                String groupName = selected.getGroupName();
                String faculty = selected.getFaculty();
                String kurs = selected.getKurs();
                if (!TextUtils.isEmpty(groupName) && !TextUtils.isEmpty(faculty) && !TextUtils.isEmpty(kurs)) {
                    Intent i = new Intent(getActivity().getApplicationContext(), GroupScheduleActivity.class);
                    i.putExtra(ServiceHelper.FACULTY, faculty);
                    i.putExtra(ServiceHelper.KURS, kurs);
                    i.putExtra(ServiceHelper.GROUP, groupName);
                    i.putExtra("IS_HARD", false);
                    startActivity(i);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Выберите пару", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.schedule_prof_item_alarm:
                mAdapter.setAlarm(info.position);
                Log.d("URSMULOG", "onContextItemSelected R.id.schedule_prof_item_alarm" + info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
