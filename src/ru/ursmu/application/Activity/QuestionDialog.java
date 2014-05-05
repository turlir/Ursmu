package ru.ursmu.application.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;

public class QuestionDialog extends DialogFragment {

    DialogInterface.OnClickListener mPositive;
    String mTitle, mMessage;

    public QuestionDialog(DialogInterface.OnClickListener positiveHandler,
                          String title,
                          String message) {
        mPositive = positiveHandler;

        mTitle = title;
        mMessage = message;
    }

    public QuestionDialog(DialogInterface.OnClickListener positiveHandler, String messages) {
        mPositive = positiveHandler;
        mMessage = messages;
        mTitle = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(mTitle == null ? DialogFragment.STYLE_NO_TITLE : DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_Dialog);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setMessage(mMessage)
                .setPositiveButton(android.R.string.ok, mPositive)
                .setNegativeButton(android.R.string.cancel, null)
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

        if (mTitle != null)
            builder.setTitle(mTitle);

        AlertDialog readyDialog = builder.create();
        readyDialog.setCanceledOnTouchOutside(false);

        return readyDialog;
    }

    private void closeDialogOpenMainActivity() {
        getDialog().dismiss();
    }

}