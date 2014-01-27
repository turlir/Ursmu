package ru.ursmu.application.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import ru.ursmu.application.Realization.ProfessorDataBasing;
import ru.ursmu.beta.application.R;

public class MainActivity extends ActionBarActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.d("URSMULOG", "MainActivity onCreate");
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
            i.putExtra("IS_HARD", false);
            i.putExtra(ServiceHelper.FACULTY, info[0]);
            i.putExtra(ServiceHelper.KURS, info[1]);
            i.putExtra(ServiceHelper.GROUP, info[2]);
            startActivity(i);
        }
        helper.setBooleanPreferences("first_run", false);
    }


    private void citizenErased(ServiceHelper helper) {
        Log.d("URSMULOG", "MainActivity scheduleGroup citizenErased");
        //delete all info - very shortly
        helper.setThreeInfo("", "", "");

        helper.setBooleanPreferences("PROF", true);

        //drop dataBase
        new AsyncTask<Void, Void, Void>() {
            ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ProgressDialog(MainActivity.this);
                dialog.setTitle("Подождите");
                dialog.setMessage("Идет обработка");
                dialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                ProfessorDataBasing db_agent = ProfessorDataBasing.getInstance(getApplicationContext(), "");
                db_agent.clearTable();    //.close() exclusive

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                dialog.dismiss();
                scheduleGroup(null);       //open FindFaculty
            }
        }.execute(null, null, null);
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
    }

}
