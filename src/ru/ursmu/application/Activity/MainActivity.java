package ru.ursmu.application.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;
import ru.ursmu.application.Abstraction.UniversalCallback;
import ru.ursmu.application.R;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends SherlockFragmentActivity {
    private static final String STATE_BTN_1 = "SATE_BTN_1";
    private static final String STATE_BTN_2 = "SATE_BTN_2";
    private static final String NOTIFICATION_ACTION = "NOTIFICATION_ACTION";
    private int mImageNumber = 1;
    private Timer mTimer;

    private static final int mDelay = 1000 * 10;
    private static final String LOGO_NUMBER = "LOGO_NUMBER";
    private static final int mCountLogo = 10;

    private Handler mChangeLogo = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            displayLogo();
        }
    };

    private UniversalCallback mHandlerDialog = new UniversalCallback() {
        @Override
        public void sendError(String notify) {
            Toast.makeText(getApplicationContext(), "Обновление завершено с ошибкой",
                    Toast.LENGTH_LONG).show();
            findViewById(R.id.button_groups).setEnabled(true);
            findViewById(R.id.button_prof).setEnabled(true);
        }

        @Override
        public void sendComplete(Object[] data) {
            Toast.makeText(getApplicationContext(), "Обновление завершено успешно", Toast.LENGTH_LONG).show();
            findViewById(R.id.button_groups).setEnabled(true);
            findViewById(R.id.button_prof).setEnabled(true);
        }


        @Override
        public void sendStart() {
            findViewById(R.id.button_groups).setEnabled(false);
            findViewById(R.id.button_prof).setEnabled(false);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Intent i = getIntent();
        if (i != null) {
            if (i.getAction() != null) {
                if (i.getAction().equals(NOTIFICATION_ACTION)) {
                    startUpdateDialog();
                }
            }
        }
        if (savedInstanceState != null) {
            mImageNumber = savedInstanceState.getInt(LOGO_NUMBER);
            displayLogo();
        }

        startNumberGeneration();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_BTN_1, findViewById(R.id.button_groups).isEnabled());
        outState.putBoolean(STATE_BTN_2, findViewById(R.id.button_prof).isEnabled());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        findViewById(R.id.button_groups).setEnabled(savedInstanceState.getBoolean(STATE_BTN_1));
        findViewById(R.id.button_prof).setEnabled(savedInstanceState.getBoolean(STATE_BTN_2));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mTimer == null)
            startNumberGeneration();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main_action_bar, menu);
        MenuItem item = menu.findItem(R.id.main_share);
        ShareActionProvider provider = (ShareActionProvider) item.getActionProvider();
        provider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
        provider.setShareIntent(createShareIntent());

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
            mChangeLogo = null;
        }
    }

    private void displayLogo() {
        View logo = findViewById(R.id.LogoLayout);
        logo.setBackgroundDrawable(getResources().getDrawable(getLogoImageID()));
    }

    private void startNumberGeneration() {
        mTimer = new Timer();

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mImageNumber = new Random().nextInt(mCountLogo - 1);
                if (mChangeLogo != null) {
                    mChangeLogo.sendEmptyMessage(0);
                }
            }
        }, mDelay, mDelay);
    }

    private int getLogoImageID() {
        String uri = "img" + mImageNumber + "";
        return getResources().getIdentifier(uri, "drawable", getPackageName());
    }


    private Intent createShareIntent() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setAction(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, " Расписание и новости УГГУ в одном Android приложении https://play.google.com/store/apps/details?id=ru.ursmu.application");
        return i;
    }

    public void scheduleGroup(View v) {
        ServiceHelper helper = ServiceHelper.getInstance(getApplicationContext());

        if (TextUtils.isEmpty(helper.getPreference(ServiceHelper.GROUP))) {
            Intent i = new Intent(this, FindFacultyActivity.class);
            startActivity(i);
        } else {
            Intent i = new Intent(this, GroupScheduleActivity.class);
            String[] info = helper.getThreeInfo();
            i.putExtra("IS_HARD", false);
            i.putExtra(ServiceHelper.FACULTY, info[0]);
            i.putExtra(ServiceHelper.KURS, info[1]);
            i.putExtra(ServiceHelper.GROUP, info[2]);
            startActivity(i);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.getAction().equals(NOTIFICATION_ACTION)) {
            startUpdateDialog();
            //mTimer.cancel();
        }
    }

    private void startUpdateDialog() {
        DialogFragment mUpdateDialog = new UpdateDialog(mHandlerDialog);
        mUpdateDialog.setCancelable(false);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(mUpdateDialog, null);
        ft.commitAllowingStateLoss();
    }

    public void event(View v) {
        Intent i = new Intent(this, NewsActivity.class);
        startActivity(i);
    }

    public void tv(View v) {
        Intent i = new Intent(this, GrornyTVActivity.class);
        startActivity(i);
        mTimer.cancel();
    }

    public void scheduleProf(View v) {
        Intent i = new Intent(this, ProfessorScheduleActivity.class);
        startActivity(i);
    }

}
