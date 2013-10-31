package ru.ursmu.application.Realization;

import ru.ursmu.application.Abstraction.IDownloadBehavior;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UrsmuPostDownload implements IDownloadBehavior {

    public String Download(String uri, String parameters) throws IOException {
        // Log.d("URSMULOG", "UrsmuPostDownload start");

        String resultString;
        URL url = new URL(uri);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", "Android/2.6");
        connection.setRequestProperty("Content-Language", "ru-RU");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
        connection.setDoOutput(true);
        connection.setDoInput(true);

        connection.connect();

        OutputStream os = connection.getOutputStream();
        os.write(parameters.getBytes());

        os.flush();
        os.close();

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream in = connection.getInputStream();
            InputStreamReader isr = new InputStreamReader(in, "UTF-8");
            //StringBuffer data = new StringBuffer();
            StringBuilder sb = new StringBuilder();
            int c;
            while ((c = isr.read()) != -1) {
                //data.append((char) c);
                sb.append((char) c);
                //Log.d("URSMULOG", "add char");
            }
            //resultString = new String(data.toString());
            resultString = sb.toString();
            sb = null;
            isr.close();
            in.close();

        } else {
            throw new IOException();
        }
        // Log.d("URSMULOG", "UrsmuPostDownload stop");
        return resultString;
    }
}
