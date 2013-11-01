package ru.ursmu.application.Activity;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;
import ru.ursmu.application.Realization.DBProcessor;
import ru.ursmu.application.Realization.GroupDBProcessor;
import ru.ursmu.application.Realization.NormalProcessor;
import ru.ursmu.application.Abstraction.*;


public class UrsmuService extends Service {    //hands new Thread

    public UrsmuService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //boolean isDB = intent.getBooleanExtra(ServiceHelper.IS_DB, false);

        int type = intent.getIntExtra(ServiceHelper.IS_DB, 1);

        if (type == 2) {       //download+data base - insert + select
            Log.d("URSMULOG", "UrsmuService type 2");
            ResultReceiver callback = intent.getParcelableExtra(ServiceHelper.CALLBACK);
            long id = intent.getLongExtra(ServiceHelper.REQUEST_ID, 0);
            IUrsmuDBObject to = intent.getParcelableExtra(ServiceHelper.TRANSFER_OBJECT);

            AbstractProcessor processor = new DBProcessor(to, callback, id, getApplicationContext());
            processor.execute();
        } else if (type == 1) {    //only download
            Log.d("URSMULOG", "UrsmuService type 1");
            ResultReceiver callback = intent.getParcelableExtra(ServiceHelper.CALLBACK);
            long id = intent.getLongExtra(ServiceHelper.REQUEST_ID, 0);
            IUrsmuObject to = intent.getParcelableExtra(ServiceHelper.TRANSFER_OBJECT);

            AbstractProcessor processor = new NormalProcessor(to, callback, id);
            processor.execute();
        } else if (type == 3) {  //get DB object`s -> service
            Log.d("URSMULOG", "UrsmuService type 3");
            ResultReceiver callback = intent.getParcelableExtra(ServiceHelper.CALLBACK);
            long id = intent.getLongExtra(ServiceHelper.REQUEST_ID, 0);
            IGroupDBUrsmuObject<IUrsmuDBObject> to = intent.getParcelableExtra(ServiceHelper.TRANSFER_OBJECT);

            AbstractProcessor processor = new GroupDBProcessor(to, callback, id, getApplicationContext());
            processor.execute();
        }

        //return super.onStartCommand(intent, flags, startId);
        return Service.START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();    //To change body of overridden methods use File | Settings | File Templates.
        Log.d("URSMULOG", "UrsmuService onDestroy");
    }
}
