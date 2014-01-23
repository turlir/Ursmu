package ru.ursmu.application.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import ru.ursmu.beta.application.R;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends ActionBarActivity {
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.d("URSMULOG", "MainActivity onCreate");
        if (savedInstanceState != null) {
            mImageNumber = savedInstanceState.getInt(LOGO_NUMBER);
            displayLogo();
        }
        //setShareIntent(createShareIntent());
        startNumberGeneration();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mTimer == null)
            startNumberGeneration();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_main, menu);

//        MenuItem item = menu.findItem(R.id.main_share);
//
//        ShareActionProvider myShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
//        myShareActionProvider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
//        myShareActionProvider.setShareIntent(createShareIntent());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                showInfoDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showInfoDialog() {
//        TextView about_view = new TextView(getApplicationContext());
//        final SpannableString s = new SpannableString(getApplicationContext().getText(R.string.about_text));
//        Linkify.addLinks(s, Linkify.WEB_URLS);
//        about_view.setTextColor(ColorStateList.valueOf(android.R.color.black));
//        about_view.setText(s);
//        about_view.setMovementMethod(LinkMovementMethod.getInstance());
//
//        new AlertDialog.Builder(this)
//                .setTitle("О приложении")
//                .setMessage(getResources().getString(R.string.about_text))
//                .setView(about_view)
//                .setPositiveButton(android.R.string.ok, null)
//                .show();

        DialogFragment about_dialog = new AboutDialog();
        about_dialog.show(getSupportFragmentManager(), "about_dialog");
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
        i.putExtra(Intent.EXTRA_TEXT, R.string.share_text_main_screen);
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

    public void event(View v) {
        Intent i = new Intent(this, NewsActivity.class);
        startActivity(i);
    }

    public void tv(View v) {
        openLink("https://www.youtube.com/user/TheUrsmu?feature=watch");
    }

    public void scheduleProf(View v) {
        Intent i = new Intent(this, ProfessorScheduleActivity.class);
        startActivity(i);
    }

    public void vkLogoClick(View v) {
        openLink("https://vk.com/ursmu_ru");
    }

    public void olenLogoClick(View v) {
        openLink("http://vk.com/overhear_uggu");
    }

    private void openLink(String s) {
        Uri address = Uri.parse(s);
        Intent open_link = new Intent(Intent.ACTION_VIEW, address);
        startActivity(open_link);
        mTimer.cancel();
    }

}
