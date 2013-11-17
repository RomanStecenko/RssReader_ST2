package com.example.RssReader_ST2;

import android.util.Log;

import java.util.ArrayList;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: Roman
 * Date: 17.11.13
 * Time: 19:00
 * To change this template use File | Settings | File Templates.
 */
public class ServiceTimerTusk extends TimerTask {
    public static final String ServiceTimerTuskLOG="Service_Timer_Tusk_LOG";
    ArrayList<ElementRss> arrayList = null;
    @Override
    public void run() {
        Log.d(ServiceTimerTuskLOG,"INSIDE CLASS ServiceTimerTusk");
    }
}
