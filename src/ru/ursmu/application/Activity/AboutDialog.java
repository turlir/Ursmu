package ru.ursmu.application.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import ru.ursmu.application.R;

public class AboutDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = inflater.inflate(R.layout.about_activity, null);
        ((TextView) view.findViewById(R.id.textViewAbout)).setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Regular.ttf"));
        builder.setView(view);

        builder.setPositiveButton(android.R.string.ok, null);
        builder.setTitle(getActivity().getResources().getString(R.string.about_title));

        return builder.create();
    }
}
