package ru.ursmu.application.Activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import ru.ursmu.application.R;

public class MainActivity extends ActionBarActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.d("URSMULOG", "MainActivity onCreate");

        Typeface typeface = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        ((TextView) findViewById(R.id.textView)).setTypeface(typeface);


        //mTypefaceContext = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        ((TextView) findViewById(R.id.textView2)).setTypeface(typeface);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_main, menu);
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
        DialogFragment about_dialog = new AboutDialog();
        about_dialog.show(getSupportFragmentManager(), "about_dialog");
    }


    public void scheduleGroup(View v) {
        ServiceHelper helper = ServiceHelper.getInstance(getApplicationContext());

        if (TextUtils.isEmpty(helper.getPreference(ServiceHelper.GROUP))) {
            Intent i = new Intent(this, FindFacultyActivity.class);
            startActivity(i);
        } else {

            if (helper.getBooleanPreference("first_run")) {      //true
                Log.d("URSMULOG", "MainActivity scheduleGroup first_run");
                citizenErased(helper);
                return;
            }

            Intent i = new Intent(this, GroupScheduleActivity.class);
            String[] info = helper.getThreeInfo();
            i.putExtra(ServiceHelper.IS_HARD, false);
            i.putExtra(ServiceHelper.FACULTY, info[0]);
            i.putExtra(ServiceHelper.KURS, info[1]);
            i.putExtra(ServiceHelper.GROUP, info[2]);
            startActivity(i);
        }
        helper.setBooleanPreferences("first_run", false);
    }


    private void citizenErased(final ServiceHelper helper) {
        Log.d("URSMULOG", "MainActivity scheduleGroup citizenErased");

        // clear info - very shortly
        helper.setThreeInfo("", "", "");
        helper.setBooleanPreferences("PROF", true);
        scheduleGroup(null);
    }

    public void event(View v) {
        Intent i = new Intent(this, NewsActivity.class);
        startActivity(i);
    }

    public void scheduleProf(View v) {
        Intent i = new Intent(this, ProfessorScheduleActivity.class);
        startActivity(i);
    }

    public void chair(View v) {
        Intent i = new Intent(this, ChairActivity.class);
        startActivity(i);
    }

    public void happyClick(View v) {
        openLink("http://100.ursmu.ru/fond.html");
    }

    private void openLink(String s) {
        Uri address = Uri.parse(s);
        Intent open_link = new Intent(Intent.ACTION_VIEW, address);
        startActivity(open_link);
    }

}
