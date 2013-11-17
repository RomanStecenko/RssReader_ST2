package com.example.RssReader_ST2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MyActivity extends ActionBarActivity {
    final String log = "mylog";
    URL url = null;
    ArrayList<ElementRss> arrayList = null;

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

        startService(new Intent(this, MyService.class));

        try {
            url = new URL(
                    //"http://news.yandex.ru/mobile.rss"
                    // "http://itnews.com.ua/export/business.rss"
                    "http://www.hyrax.ru/cgi-bin/mob_xml.cgi"
            );
        } catch (MalformedURLException e) {
            Log.d(log, e.toString());
            e.printStackTrace();
        }


        try {
            arrayList = new XmlObject().execute(url).get();
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
    }

    public String[] getInfo(ElementRss elementRss) {
        String[] info = {elementRss.getDescription(), elementRss.getLink(), elementRss.getPubDate(), elementRss.getTitle()};
        return info;
    }
}
