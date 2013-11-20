package com.example.RssReader_ST2;

import android.app.Application;
import android.content.Intent;

/**
 * Created with IntelliJ IDEA.
 * User: Roman
 * Date: 20.11.13
 * Time: 13:52
 * To change this template use File | Settings | File Templates.
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(this, MyService.class));
    }
}
