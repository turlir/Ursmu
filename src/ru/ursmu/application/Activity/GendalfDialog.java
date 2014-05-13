package ru.ursmu.application.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import ru.ursmu.application.R;

public class GendalfDialog extends DialogFragment {
    private UrsmuBuilding t;

    public GendalfDialog(UrsmuBuilding s) {
        t = s;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = inflater.inflate(R.layout.gendalf_dialog, null);
        AudienceInfoView control = ((AudienceInfoView) view.findViewById(R.id.audience_control));
        control.setAudienceText(t);
        builder.setView(view);

        builder.setPositiveButton(android.R.string.ok, null);
        builder.setTitle("Аудитория " + t.getOriginal());

        return builder.create();
    }
}
