package com.example.RssReader_ST2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MyActivity extends ActionBarActivity {
    public static final String log = "mylog";
    URL url = null;
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



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.d(log, "INSIDE ACTIVITY onCreate() , try to print file dir: " + getApplicationContext().getFilesDir().toString() );

        try {
            url = new URL(
                    "http://news.yandex.ru/mobile.rss"
                    // "http://itnews.com.ua/export/business.rss"
                   // "http://www.hyrax.ru/cgi-bin/mob_xml.cgi"
            );
        } catch (MalformedURLException e) {
            Log.d(log, e.toString());
            e.printStackTrace();
        }


        try {
            arrayList = new XmlObject().execute(url).get();
            Log.d(log,"INSIDE CLASS MyActivi, onCreat(), try to see on received arrayList size: "+arrayList.size());
            Log.d(log,"INSIDE CLASS MyActivi, onCreat(), try to see on received arrayList "+arrayList.toString());

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        String titles[] = new String[arrayList.size()];

        for (int i = 0; i < arrayList.size(); i++) {
            titles[i] = arrayList.get(i).getTitle();
        }

        final ListView listView = (ListView) findViewById(R.id.listView);
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, titles);
        listView.setAdapter(adapter);
        if (getResources().getConfiguration().orientation == 1 & getResources().getBoolean(R.bool.istablet)) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            listView.setLayoutParams(layoutParams);
            TextView textView = (TextView) findViewById(R.id.textView);
            textView.setText(arrayList.get(0).toStringWithOutTitle());
            textView.setTextSize(30);
        }
        if (getResources().getConfiguration().orientation == 2 & getResources().getBoolean(R.bool.istablet)) {
            TextView textView = (TextView) findViewById(R.id.textView);
            textView.setText(arrayList.get(0).toStringWithOutTitle());
            textView.setTextSize(30);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String showWhatWeGot = (String) listView.getItemAtPosition(position);
                Toast.makeText(getBaseContext(), showWhatWeGot, Toast.LENGTH_SHORT).show();

                if (getResources().getConfiguration().orientation == 2 & getResources().getBoolean(R.bool.istablet)) {
                    TextView textView = (TextView) findViewById(R.id.textView);
                    textView.setText(arrayList.get(position).toStringWithOutTitle());
                } else {
                    //Log.d(log, "ORIENTT IS-" + getResources().getConfiguration().orientation);
                    Intent intent = new Intent(getApplicationContext(), DisplayActivity.class);
                    intent.putExtra("sendElement", getInfo(arrayList.get(position)));
                    startActivity(intent);
                }
            }
        });

//        Intent serviceIntent=new Intent(this, MyService.class);
//        serviceIntent.putExtra("sendStringToService",getArrayList().get(0).getPubDate());
//        serviceIntent.putExtra("sendUrlToService",getUrl());
//        startService(serviceIntent);
       // startService(new Intent(this, MyService.class));
    }

    public String[] getInfo(ElementRss elementRss) {
        String[] info = {elementRss.getDescription(), elementRss.getLink(), elementRss.getPubDate(), elementRss.getTitle()};
        return info;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item1) {
            showInCkick();
        }
        return true;
    }

    public void showInCkick() {
        Toast.makeText(this, "You click STOP SERVICE BUTTON in ActionBar", Toast.LENGTH_SHORT).show();
    }
}
