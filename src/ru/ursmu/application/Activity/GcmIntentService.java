package ru.ursmu.application.Activity;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import ru.ursmu.application.Abstraction.AbsPush;
import ru.ursmu.application.R;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {

    public GcmIntentService() {
        super(AbsPush.SENDER_ID);
    }

    public static final String TAG = "URSMULOG";


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "GcmIntentService onHandleIntent");
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Log.d("URSMULOG", "GcmIntentService GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR");
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.d("URSMULOG", "GcmIntentService GoogleCloudMessaging.MESSAGE_TYPE_DELETED");
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                sendNotification(extras.getString("param1"), extras.getString("param2"));
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String title, String msg) {
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, getIntentNotify(), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.push)
                        .setContentTitle(title)
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentText(msg)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)); //ty-ty-ty-ty-ty

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(1, mBuilder.build());
    }

    private Intent getIntentNotify() {
        ServiceHelper helper = ServiceHelper.getInstance(getApplicationContext());

        Intent i = new Intent(this, GroupScheduleActivity.class);
        String[] info = helper.getThreeInfo();
        i.putExtra(ServiceHelper.IS_HARD, true);               //only true - special update
        i.putExtra(ServiceHelper.FACULTY, info[0]);
        i.putExtra(ServiceHelper.KURS, info[1]);
        i.putExtra(ServiceHelper.GROUP, info[2]);
        i.setFlags(GroupScheduleActivity.NOTIFY_INTENT);
        return i;
    }
}
