package ru.ursmu.application.Activity;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import ru.ursmu.beta.application.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;


public class AboutActivity extends Activity {
    private static final String PATH_TEXT_ASSETS = "about";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);
        addText();
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

}