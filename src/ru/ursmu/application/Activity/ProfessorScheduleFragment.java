package ru.ursmu.application.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import ru.ursmu.application.JsonObject.EducationItem;
import ru.ursmu.application.R;


public class ProfessorScheduleFragment extends ListFragment {
    public static final String MAIN_ARG = "MAIN_ARG";

    public static Fragment getInstance(EducationItem[] value) {
        ProfessorScheduleFragment f = new ProfessorScheduleFragment();
        Bundle args = new Bundle();
        args.putSerializable(MAIN_ARG, value);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EducationItem[] data = new EducationItem[0];
        Bundle b;
        if ((b = getArguments()) != null) {
            data = (EducationItem[]) b.getSerializable(MAIN_ARG);
        }
        registerForContextMenu(getListView());
        setListAdapter(new ScheduleAdapter(getActivity().getApplicationContext(), R.layout.schedule_adapter, data, true));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.schedule_item_professor, menu);
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        if (!getUserVisibleHint()) { //stupid, idiot! 4 hour i down
            return false;
        }

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.schedule_prof_item_group:
                EducationItem selected = (EducationItem) getListAdapter().getItem(info.position);
                String groupName = selected.getGroupName();
                String faculty = selected.getFaculty();
                String kurs = selected.getKurs();
                if (!TextUtils.isEmpty(groupName) && !TextUtils.isEmpty(faculty) && !TextUtils.isEmpty(kurs)) {
                    Intent i = new Intent(getActivity().getApplicationContext(), GroupScheduleActivity.class);
                    i.putExtra(ServiceHelper.FACULTY, faculty);
                    i.putExtra(ServiceHelper.KURS, kurs);
                    i.putExtra(ServiceHelper.GROUP, groupName);
                    i.putExtra(ServiceHelper.IS_HARD, false);
                    startActivity(i);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Выберите пару", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.schedule_prof_item_alarm:
                ((ScheduleAdapter) getListAdapter()).setAlarm(info.position);
                Log.d("URSMULOG", "onContextItemSelected R.id.schedule_prof_item_alarm" + info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


}
