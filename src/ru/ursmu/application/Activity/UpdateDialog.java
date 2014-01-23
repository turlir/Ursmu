package ru.ursmu.application.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.widget.Toast;
import ru.ursmu.application.Abstraction.UniversalCallback;
import ru.ursmu.application.Realization.ScheduleGroupFactory;
import ru.ursmu.beta.application.R;

import java.io.Serializable;

public class UpdateDialog extends DialogFragment {

    private ResultReceiver mHandler;
    private ProgressDialog mDialog;
    private String mLastMiddle = "";

    private UniversalCallback mHandlerDialog = new UniversalCallback() {
        @Override
        public void sendError(String notify) {
            showNotification(notify);
        }

        @Override
        public void sendComplete(Serializable data) {
            showNotification(getResources().getString(R.string.upd_success));
            mHandler.send(0, null);
        }

        @Override
        public void sendStart(long id) {
        }

        @Override
        public void sendMiddle(String s) {
            if(!mLastMiddle.equals(s) || TextUtils.isEmpty(mLastMiddle)) {
                mDialog.setMessage(s);
                mLastMiddle = s;
            }
        }
    };

    public UpdateDialog(ResultReceiver success_callback) {
        mHandler = success_callback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDialog = new ProgressDialog(getActivity());
        mDialog.setTitle(getResources().getString(R.string.upd_dialog_title));
        mDialog.setMessage("");
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        return mDialog;
    }


    @Override
    public void onStart() {
        super.onStart();
        ServiceHelper.getInstance(getActivity()).setGroupDBObjects(new ScheduleGroupFactory(), mHandlerDialog);
    }

    private void showNotification(String value) {
        getDialog().dismiss();
        Toast.makeText(getActivity().getApplicationContext(), value, Toast.LENGTH_SHORT).show();
    }
}
