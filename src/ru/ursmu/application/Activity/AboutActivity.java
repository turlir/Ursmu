package ru.ursmu.application.Activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import ru.ursmu.application.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class AboutActivity extends SherlockActivity {
    private static final String PATH_TEXT_ASSETS = "about";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);
        addText();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void addText() {
        TextView about = (TextView) findViewById(R.id.textViewAbout);
        AssetManager manager = getApplicationContext().getAssets();
        if (about == null || manager == null) {
            Log.d("URSMULOG", "AboutActivity addText " + "about == null || manager == null");
            return;
        }
        try {
            InputStream is = manager.open(PATH_TEXT_ASSETS, MODE_WORLD_READABLE);
            InputStreamReader ist = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(ist);
            String line;
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null) {
                Log.d("URSMULOG", "AboutActivity addText " + line);
                sb.append(line);
                sb.append("\n");
            }
            br.close();
            ist.close();
            is.close();
            about.setText(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("URSMULOG", "AboutActivity addText " + e.getMessage());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}