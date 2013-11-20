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

import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

/**
 * Created with IntelliJ IDEA.
 * User: Roman
 * Date: 16.11.13
 * Time: 2:30
 * To change this template use File | Settings | File Templates.
 */
public class MyService extends Service {
    public static final String log = "mylog";
    NotificationManager mNotifyMgr;
    String chekingPubDate="";
    URL url=null;
    ArrayList<ElementRss> arrayList = null;

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public ArrayList<ElementRss> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<ElementRss> arrayList) {
        this.arrayList = arrayList;
    }

    public String getChekingPubDate() {
        return chekingPubDate;
    }

    public void setChekingPubDate(String chekingPubDate) {
        this.chekingPubDate = chekingPubDate;
    }


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


//        try {
//            setArrayList(new XmlObject().execute(getUrl()).get());
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }

        //getArrayList().get(0).toString();
//        Log.d(log, "INSIDE onCreate(), try to print received array list: " + getArrayList().get(0).getPubDate() );
//        Timer timer = new Timer();
//        ServiceTimerTusk task = new ServiceTimerTusk();
//        task.setPubDate(getArrayList().get(0).getPubDate());
//        timer.schedule(task,5000,60000);

        //Intent notifyIntent = new Intent(new ComponentName(this, SomeActivity.class));
       // notifyIntent.setFlags(FLAG_ACTIVITY_NEW_TASK |FLAG_ACTIVITY_CLEAR_TASK);
    }

//    @Override
//    public void onDestroy() {
//        Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
//        mNotifyMgr.cancel(001);
//    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
        // TODO Запустить поток в фоновом режиме для обработки.

//        String pubDate = intent.getStringExtra("sendStringToService");
//        setChekingPubDate(pubDate);
//        setUrl((URL) intent.getSerializableExtra("sendUrlToService"));
//        Log.d(log, "INSIDE onStartCommand, try to print pubDate: " + pubDate);
//        Log.d(log,"INSIDE onStartCommand, try to print sended URL: "+ getUrl().toString());

        return Service.START_STICKY;
    }
}
