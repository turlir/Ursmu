package ru.ursmu.application.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import ru.ursmu.beta.application.R;

public class QuestionDialog extends DialogFragment {

    DialogInterface.OnClickListener mPositive;

    public QuestionDialog(DialogInterface.OnClickListener positiveHandler) {
        mPositive = positiveHandler;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_Dialog);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.quest_dialog_title))
                .setMessage(getResources().getString(R.string.quest_dialog_desc))
                .setPositiveButton(android.R.string.ok, mPositive)
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                closeDialogOpenMainActivity();
                            }
                        }
                )
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP && !event.isCanceled()) {
                            closeDialogOpenMainActivity();
                            return true;
                        } else
                            return false;
                    }
                });

        AlertDialog readyDialog = builder.create();
        readyDialog.setCanceledOnTouchOutside(false);

        return readyDialog;
    }

    private void closeDialogOpenMainActivity() {
        getDialog().dismiss();
        Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}