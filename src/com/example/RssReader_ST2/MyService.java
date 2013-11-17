package com.example.RssReader_ST2;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: Roman
 * Date: 16.11.13
 * Time: 2:30
 * To change this template use File | Settings | File Templates.
 */
public class MyService extends Service {
    NotificationManager mNotifyMgr;

    public class LocalBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;  //To change body of implemented methods use File | Settings | File Templates.
    }
    private final IBinder mBinder = new LocalBinder();



    public void onCreate() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_av_add_to_queue)
                        .setContentTitle("Service RSS Reader")
                        .setContentText("is started(works)");

        Intent resultIntent = new Intent(this, SomeActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MyActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntentBuilder =stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);
        int mNotificationId = 001;

        mNotifyMgr =(NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());

        Timer timer = new Timer();
        TimerTask task = new ServiceTimerTusk();
        timer.schedule(task,5000,10000);

        //Intent notifyIntent = new Intent(new ComponentName(this, SomeActivity.class));
       // notifyIntent.setFlags(FLAG_ACTIVITY_NEW_TASK |FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
        mNotifyMgr.cancel(001);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
        // TODO Запустить поток в фоновом режиме для обработки.
        return Service.START_STICKY;
    }
}
