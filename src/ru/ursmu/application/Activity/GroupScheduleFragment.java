package ru.ursmu.application.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import ru.ursmu.application.JsonObject.EducationItem;
import ru.ursmu.beta.application.R;

public class GroupScheduleFragment extends ListFragment implements AdapterView.OnItemClickListener {
    public static final String MAIN_ARG = "MAIN_ARG";
    private int mClickedPosition;

    public static Fragment getInstance(EducationItem[] value) {
        GroupScheduleFragment f = new GroupScheduleFragment();
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
        setListAdapter(new ScheduleAdapter(getActivity().getApplicationContext(), R.layout.schedule_adapter, data, false));
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {

        if (!getUserVisibleHint()) { //stupid, idiot! 4 hour i down
            return false;
        }

        switch (item.getItemId()) {
            case R.id.schedule_item_professor:
                String normalProfessor = ((EducationItem) getListAdapter().getItem(mClickedPosition)).getNormalProfessor();
                if (!TextUtils.isEmpty(normalProfessor)) {
                    Intent i = new Intent(getActivity().getApplicationContext(), ProfessorScheduleActivity.class);
                    i.putExtra("PROFESSOR", normalProfessor);
                    startActivity(i);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Выберите пару", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.schedule_item_alarm:
                ((ScheduleAdapter) getListAdapter()).setAlarm(mClickedPosition);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.schedule_item_group, menu);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mClickedPosition = position;
        getActivity().openContextMenu(view);
    }
}
