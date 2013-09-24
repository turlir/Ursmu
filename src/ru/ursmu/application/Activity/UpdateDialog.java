package ru.ursmu.application.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import ru.ursmu.application.Abstraction.UniversalCallback;
import ru.ursmu.application.Realization.ScheduleGroupFactory;

public class UpdateDialog extends DialogFragment {

    private UniversalCallback mHandler;

    public UpdateDialog(UniversalCallback handler) {
        mHandler = handler;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_Dialog);
        //getDialog().setTitle("Обновить расписание?");
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("Обновить расписание?")
                .setMessage("Для продолжения работы необходимо обновление. Будет израсходовано 2 мегабайта трафика.")
                .setPositiveButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                update();
                            }
                        }
                )
                .setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //mHandler.send(1, null);
                            }
                        }
                );

        return builder.create();
    }

    private void update() {
        ServiceHelper helper = ServiceHelper.getInstance(getDialog().getContext());
        helper.setGroupDBObjects(new ScheduleGroupFactory(), mHandler);
    }

}