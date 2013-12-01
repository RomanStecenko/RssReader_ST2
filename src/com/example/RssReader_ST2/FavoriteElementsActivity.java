package com.example.RssReader_ST2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Roman
 * Date: 16.11.13
 * Time: 18:42
 * To change this template use File | Settings | File Templates.
 */
public class FavoriteElementsActivity extends ActionBarActivity {
    public static final String log = "mylog";
    ArrayList<String> likeTitles = new ArrayList<String>();
    ArrayList<ElementRss> elements = new ArrayList<ElementRss>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_elements_activity_layout);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FeedReaderContract mDbContract = new FeedReaderContract();
                FeedReaderContract.FeedReaderDbHelper mDbHelper = mDbContract.new FeedReaderDbHelper(getApplicationContext());
                SQLiteDatabase dbRead = mDbHelper.getReadableDatabase();
                String[] projection = {
                        FeedReaderContract.FeedEntry._ID,
                        FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE,
                        FeedReaderContract.FeedEntry.COLUMN_NAME_DESCRIPTION,
                        FeedReaderContract.FeedEntry.COLUMN_NAME_LINK,
                        FeedReaderContract.FeedEntry.COLUMN_NAME_PUBDATE
                };
//        String sortOrder = FeedReaderContract.FeedEntry.COLUMN_NAME_UPDATED + " DESC";

                Cursor cursor = dbRead.query(
                        FeedReaderContract.FeedEntry.TABLE_NAME, // The table to query
                        projection, // The columns to return
                        null, //selection , // The columns for the WHERE clause
                        null, //selectionArgs , // The values for the WHERE clause
                        null, // don't group the rows
                        null, // don't filter by row groups
                        null // The sort order
                );

                if (cursor.moveToFirst()) {
                    int idColIndex = cursor.getColumnIndex(FeedReaderContract.FeedEntry._ID);
                    int titleColIndex = cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE);
                    int descriptionColIndex = cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_NAME_DESCRIPTION);
                    int linkColIndex = cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_NAME_LINK);
                    int pubDateColIndex = cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_NAME_PUBDATE);

                    do {
                        Log.d(log,
                                "ID = " + cursor.getInt(idColIndex) +
                                        ", title = " + cursor.getString(titleColIndex) +
                                        ", description = " + cursor.getString(descriptionColIndex) +
                                        ", link = " + cursor.getString(linkColIndex) +
                                        ", pubDate = " + cursor.getString(pubDateColIndex)
                        );
                        Log.d(log, "TITLE= " + cursor.getString(titleColIndex));
                        likeTitles.add(cursor.getString(titleColIndex));
                        elements.add(new ElementRss(
                                cursor.getString(titleColIndex),
                                cursor.getString(linkColIndex),
                                cursor.getString(descriptionColIndex),
                                cursor.getString(pubDateColIndex)
                        ));
                    } while (cursor.moveToNext());
                }
                cursor.close();
                dbRead.close();
            }
        });

        final ListView listView = (ListView) findViewById(R.id.FElistView);
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, likeTitles);
        listView.setAdapter(adapter);
        if (getResources().getConfiguration().orientation == 1 & getResources().getBoolean(R.bool.istablet)) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            listView.setLayoutParams(layoutParams);
            TextView textView = (TextView) findViewById(R.id.FEtextView);
            if(elements.size()>0){
                textView.setText(elements.get(0).toStringWithOutTitle());
            }
            textView.setTextSize(30);
        }
        if (getResources().getConfiguration().orientation == 2 & getResources().getBoolean(R.bool.istablet)) {
            TextView textView = (TextView) findViewById(R.id.FEtextView);
            if(elements.size()>0){
                textView.setText(elements.get(0).toStringWithOutTitle());
            }
            textView.setTextSize(30);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String showWhatWeGot = (String) listView.getItemAtPosition(position);
                Toast.makeText(getBaseContext(), showWhatWeGot, Toast.LENGTH_SHORT).show();
                if (getResources().getConfiguration().orientation == 2 & getResources().getBoolean(R.bool.istablet)) {
                    TextView textView = (TextView) findViewById(R.id.FEtextView);
                    textView.setText(elements.get(position).toStringWithOutTitle());
                } else {
                    Intent intent = new Intent(getApplicationContext(), FavoriteElementsDisplayActivity.class);
                    intent.putExtra("sendElement", getInfo(elements.get(position)));
                    startActivity(intent);
                }
            }
        });

    }
    public String[] getInfo(ElementRss elementRss) {
        return new String[]{elementRss.getDescription(), elementRss.getLink(), elementRss.getPubDate(), elementRss.getTitle()};
    }
}
