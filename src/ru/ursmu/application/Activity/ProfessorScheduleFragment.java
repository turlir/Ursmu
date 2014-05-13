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


public class ProfessorScheduleFragment extends ListFragment implements AdapterView.OnItemClickListener {
    public static final String MAIN_ARG = "MAIN_ARG";
    private int mClickedPosition;

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
        getListView().setOnItemClickListener(this);
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

        switch (item.getItemId()) {
            case R.id.schedule_prof_item_group:
                EducationItem selected = (EducationItem) getListAdapter().getItem(mClickedPosition);
                String groupName = selected.getGroupName();
                String faculty = selected.getFaculty();
                String kurs = selected.getKurs();
                if (!TextUtils.isEmpty(groupName) && !TextUtils.isEmpty(faculty) && !TextUtils.isEmpty(kurs)) {
                    Intent i = new Intent(getActivity().getApplicationContext(), SlideActivity.class);
                    i.putExtra(ServiceHelper.FACULTY, faculty);
                    i.putExtra(ServiceHelper.KURS, kurs);
                    i.putExtra(ServiceHelper.GROUP, groupName);
                    i.putExtra(ServiceHelper.IS_HARD, false);
                    startActivity(i);
                } else {
                    Toast.makeText(getActivity().getBaseContext(), "Выберите пару", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.schedule_prof_item_alarm:
                ((ScheduleAdapter) getListAdapter()).setAlarm(mClickedPosition);
                //Log.d("URSMULOG", "onContextItemSelected R.id.schedule_prof_item_alarm" + mClickedPosition);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mClickedPosition = position;
        getActivity().openContextMenu(view);
    }
}
